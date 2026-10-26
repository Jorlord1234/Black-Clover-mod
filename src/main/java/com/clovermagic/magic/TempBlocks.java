package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Blocks created by spells (Stone Wall, Glacier Wall, Rock Cage...) that disappear again after a while.
 * The list is saved with the world, so after a crash the blocks are still removed on the next start.
 * Blocks in unloaded chunks are never touched (that would force the chunk to load); they wait until it loads.
 */
public final class TempBlocks {
    private record Entry(ResourceKey<Level> dim, BlockPos pos, BlockState state, long expire) {
    }

    /** The saved list (lives in the overworld's data storage). */
    private static final class Store extends SavedData {
        final List<Entry> entries = new ArrayList<>();

        static Store create() {
            return new Store();
        }

        static Store load(CompoundTag tag, HolderLookup.Provider provider) {
            Store s = new Store();
            ListTag list = tag.getList("blocks", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag t = list.getCompound(i);
                ResourceLocation dimId = ResourceLocation.tryParse(t.getString("dim"));
                if (dimId == null) continue;
                try {
                    BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), t.getCompound("state"));
                    s.entries.add(new Entry(ResourceKey.create(Registries.DIMENSION, dimId),
                            BlockPos.of(t.getLong("pos")), state, t.getLong("expire")));
                } catch (RuntimeException ignored) {
                    // a block that no longer exists (mod removed): drop the entry
                }
            }
            return s;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
            ListTag list = new ListTag();
            for (Entry e : entries) {
                CompoundTag t = new CompoundTag();
                t.putString("dim", e.dim().location().toString());
                t.putLong("pos", e.pos().asLong());
                t.put("state", NbtUtils.writeBlockState(e.state()));
                t.putLong("expire", e.expire());
                list.add(t);
            }
            tag.put("blocks", list);
            return tag;
        }
    }

    private static final String DATA_NAME = "clovermagic_temp_blocks";
    private static Store store;

    private TempBlocks() {
    }

    /**
     * Same as {@link #place(ServerLevel, BlockPos, BlockState, int)}, but asks the rest of the game first: protection and claim
     * mods listen to the place event and can cancel it (the caster is the placing entity).
     */
    public static boolean place(net.minecraft.server.level.ServerPlayer caster, BlockPos pos, BlockState state, int ticks) {
        ServerLevel level = caster.serverLevel();
        if (!com.clovermagic.Config.SPELLS_PLACE_BLOCKS.get() || store == null || !level.isLoaded(pos)) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(
                BlockSnapshot.create(level.dimension(), level, pos), level.getBlockState(pos), caster);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;
        return place(level, pos, state, ticks);
    }

    /** Places the block if the spot is loaded and free (air, plants, water...). Returns true if it was placed. */
    public static boolean place(ServerLevel level, BlockPos pos, BlockState state, int ticks) {
        if (store == null || !level.isLoaded(pos)) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        level.setBlock(pos, state, 3);
        store.entries.add(new Entry(level.dimension(), pos.immutable(), state, level.getGameTime() + ticks));
        store.setDirty();
        return true;
    }

    public static void onServerStarted(ServerStartedEvent event) {
        store = event.getServer().overworld().getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(Store::create, Store::load, null), DATA_NAME);
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        store = null;
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (store == null || !(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % 10 != 0) return;
        long now = level.getGameTime();
        boolean changed = false;
        Iterator<Entry> it = store.entries.iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            if (!e.dim().equals(level.dimension()) || now < e.expire()) continue;
            if (!level.isLoaded(e.pos())) continue; // wait until the chunk is loaded again
            if (level.getBlockState(e.pos()).equals(e.state())) level.removeBlock(e.pos(), false);
            it.remove();
            changed = true;
        }
        if (changed) store.setDirty();
    }

    /** Removes every loaded temporary block before the world is saved; entries in unloaded chunks stay in the saved list. */
    public static void onServerStopping(ServerStoppingEvent event) {
        if (store == null) return;
        MinecraftServer server = event.getServer();
        Iterator<Entry> it = store.entries.iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            ServerLevel level = server.getLevel(e.dim());
            if (level == null || !level.isLoaded(e.pos())) continue;
            if (level.getBlockState(e.pos()).equals(e.state())) level.removeBlock(e.pos(), false);
            it.remove();
        }
        store.setDirty();
    }
}
