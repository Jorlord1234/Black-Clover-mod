package com.clovermagic.entity;

import com.clovermagic.Config;
import com.clovermagic.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Julius Novachrono is not tied to a building: now and then, near a player in the overworld, he appears on the surface
 * (never twice within 160 blocks). He stays until the world forgets him, like any NPC.
 */
public final class JuliusSpawner {
    private JuliusSpawner() {
    }

    public static void tick(ServerPlayer sp) {
        if (!Config.JULIUS_WANDERS.get() || sp.tickCount % 1200 != 0) return;
        ServerLevel level = sp.serverLevel();
        if (level.dimension() != Level.OVERWORLD) return;
        RandomSource r = level.getRandom();
        if (r.nextInt(Config.JULIUS_SPAWN_ONE_IN.get()) != 0) return;
        if (!level.getEntitiesOfClass(MagicNpcEntity.class, sp.getBoundingBox().inflate(160.0), n -> n.npcType().equals("julius")).isEmpty()) return;
        for (int i = 0; i < 12; i++) {
            double ang = r.nextDouble() * Math.PI * 2;
            double dist = 28 + r.nextInt(30);
            int x = (int) Math.floor(sp.getX() + Math.cos(ang) * dist);
            int z = (int) Math.floor(sp.getZ() + Math.sin(ang) * dist);
            if (!level.hasChunkAt(new BlockPos(x, 64, z))) continue;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.getFluidState(pos.below()).isEmpty()) continue;
            if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) continue;
            MagicNpcEntity julius = ModEntities.MAGIC_NPC.get().create(level);
            if (julius == null) return;
            julius.moveTo(x + 0.5, y, z + 0.5, r.nextFloat() * 360.0F, 0.0F);
            julius.setNpcType("julius");
            level.addFreshEntity(julius);
            return;
        }
    }
}
