package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Blocks created by spells (Stone Wall, Glacier Wall, Rock Cage...) that disappear again after a while. */
public final class TempBlocks {
    private record Entry(ResourceKey<Level> dim, BlockPos pos, BlockState state, long expire) {
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();

    private TempBlocks() {
    }

    /** Places the block if the spot is free (air, plants, water...). Returns true if it was placed. */
    public static boolean place(ServerLevel level, BlockPos pos, BlockState state, int ticks) {
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        level.setBlock(pos, state, 3);
        ENTRIES.add(new Entry(level.dimension(), pos.immutable(), state, level.getGameTime() + ticks));
        return true;
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % 10 != 0) return;
        long now = level.getGameTime();
        Iterator<Entry> it = ENTRIES.iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            if (!e.dim().equals(level.dimension()) || now < e.expire()) continue;
            if (level.getBlockState(e.pos()).equals(e.state())) level.removeBlock(e.pos(), false);
            it.remove();
        }
    }

    /** Removes everything before the world is saved so no temporary block stays for ever. */
    public static void onServerStopping(ServerStoppingEvent event) {
        for (Entry e : ENTRIES) {
            ServerLevel level = event.getServer().getLevel(e.dim());
            if (level != null && level.getBlockState(e.pos()).equals(e.state())) level.removeBlock(e.pos(), false);
        }
        ENTRIES.clear();
    }
}
