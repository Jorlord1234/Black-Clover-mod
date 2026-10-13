package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Small helpers shared by all spells. */
public final class SpellUtil {
    private SpellUtil() {
    }

    public static boolean fail(ServerPlayer p, String message) {
        p.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), true);
        return false;
    }

    public static DamageSource magic(ServerPlayer p) {
        return p.damageSources().indirectMagic(p, p);
    }

    public static DamageSource physical(ServerPlayer p) {
        return p.damageSources().playerAttack(p);
    }

    public static void sound(ServerPlayer p, SoundEvent sound, float volume, float pitch) {
        p.serverLevel().playSound(null, p.getX(), p.getY(), p.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }

    /** First block the player is looking at (type MISS if nothing in range). */
    public static BlockHitResult rayBlock(ServerPlayer p, double range) {
        Vec3 eye = p.getEyePosition();
        Vec3 end = eye.add(p.getLookAngle().scale(range));
        return p.serverLevel().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
    }

    /** How far the look-ray travels before hitting a block (or range if it hits nothing). */
    public static double rayLength(ServerPlayer p, double range) {
        BlockHitResult hit = rayBlock(p, range);
        if (hit.getType() == HitResult.Type.MISS) return range;
        return hit.getLocation().distanceTo(p.getEyePosition());
    }

    /** All living entities along the player's look-ray, nearest first. */
    public static List<LivingEntity> line(ServerPlayer p, double range, double radius) {
        ServerLevel level = p.serverLevel();
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        double max = rayLength(p, range);
        LinkedHashSet<LivingEntity> out = new LinkedHashSet<>();
        for (double d = 0.5; d <= max; d += 0.5) {
            Vec3 pt = eye.add(look.scale(d));
            AABB box = new AABB(pt, pt).inflate(radius);
            out.addAll(level.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != p && e.isAlive() && !e.isSpectator()));
        }
        return new ArrayList<>(out);
    }

    /** Living entities around the player (excluding the player). */
    public static List<LivingEntity> around(ServerPlayer p, double radius) {
        return p.serverLevel().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(radius),
                e -> e != p && e.isAlive() && !e.isSpectator() && e.distanceTo(p) <= radius);
    }

    /** Particle trail along the look-ray. */
    public static void beam(ServerPlayer p, double length, ParticleOptions particle, int perStep) {
        ServerLevel level = p.serverLevel();
        Vec3 start = p.getEyePosition().add(0, -0.25, 0);
        Vec3 look = p.getLookAngle();
        for (double d = 1.0; d <= length; d += 0.5) {
            Vec3 pt = start.add(look.scale(d));
            level.sendParticles(particle, pt.x, pt.y, pt.z, perStep, 0.05, 0.05, 0.05, 0.0);
        }
    }

    public static void ring(ServerLevel level, Vec3 center, double radius, ParticleOptions particle, int points) {
        for (int i = 0; i < points; i++) {
            double a = (Math.PI * 2 * i) / points;
            level.sendParticles(particle, center.x + Math.cos(a) * radius, center.y + 0.2,
                    center.z + Math.sin(a) * radius, 2, 0.08, 0.08, 0.08, 0.01);
        }
    }

    public static void zap(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 dir = to.subtract(from);
        double len = dir.length();
        if (len < 0.01) return;
        Vec3 step = dir.normalize().scale(0.4);
        Vec3 pt = from;
        for (double d = 0; d < len; d += 0.4) {
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pt.x, pt.y, pt.z, 2, 0.1, 0.1, 0.1, 0.0);
            pt = pt.add(step);
        }
    }

    public static void push(LivingEntity e, Vec3 velocity) {
        e.setDeltaMovement(e.getDeltaMovement().add(velocity));
        e.hurtMarked = true;
    }

    public static Vec3 awayFrom(ServerPlayer p, LivingEntity e, double strength, double up) {
        Vec3 d = e.position().subtract(p.position());
        Vec3 flat = new Vec3(d.x, 0, d.z).normalize().scale(strength);
        return new Vec3(flat.x, up, flat.z);
    }
}
