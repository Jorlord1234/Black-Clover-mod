package com.clovermagic.magic;

import java.util.List;

import com.clovermagic.Config;
import com.clovermagic.entity.SpellVfxEntity;
import com.clovermagic.vfx.Fx;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Spawns the 3D model of a spell after it was cast successfully. It only places a visual entity; it never changes what
 * a spell does. Targets and ranges are looked up the same way the spell does it (same tick, so same result).
 */
public final class SpellVfx {
    private SpellVfx() {
    }

    /** The cast circle and floating grimoire under the caster, in the color of the spell's element. */
    private static void castCircle(ServerPlayer p, Spell spell) {
        Fx cast = switch (spell.attribute) {
            case FIRE -> Fx.CAST_FIRE;
            case WATER -> Fx.CAST_WATER;
            case WIND -> Fx.CAST_WIND;
            case EARTH -> Fx.CAST_EARTH;
            case ICE -> Fx.CAST_ICE;
            case LIGHTNING -> Fx.CAST_LIGHTNING;
            case LIGHT -> Fx.CAST_LIGHT;
            case DARKNESS -> Fx.CAST_DARKNESS;
            case ANTI_MAGIC -> Fx.CAST_ANTI_MAGIC;
        };
        SpellVfxEntity.spawn(p.serverLevel(), cast, p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot(), 1f, p.getId(), 0L);
    }

    public static void play(ServerPlayer p, Spell spell) {
        if (!Config.SPELL_MODELS.get()) return;
        castCircle(p, spell);
        Fx fx;
        try {
            fx = Fx.valueOf(spell.name());
        } catch (IllegalArgumentException e) {
            return;
        }
        ServerLevel level = p.serverLevel();
        Vec3 look = p.getLookAngle();
        float yaw = p.getYRot(), pitch = p.getXRot();
        switch (fx.anchor) {
            case BEAM -> {
                double range = fx == Fx.SPIRAL_FLAME ? FireEffects.spiralRange(p, 7) : fx.range;
                Vec3 o = p.getEyePosition().add(0, -0.25, 0).add(look.scale(0.5));
                float len = (float) Math.max(0.5, SpellUtil.rayLength(p, range) - 0.5);
                SpellVfxEntity.spawn(level, fx, o.x, o.y, o.z, yaw, pitch, len, -1, 0L);
            }
            case CONE -> {
                Vec3 o = p.getEyePosition().add(0, -0.25, 0).add(look.scale(0.5));
                SpellVfxEntity.spawn(level, fx, o.x, o.y, o.z, yaw, pitch, (float) fx.range, -1, 0L);
            }
            case SELF -> SpellVfxEntity.spawn(level, fx, p.getX(), p.getY(), p.getZ(), yaw, pitch, (float) fx.range, -1, 0L);
            case SELF_FOLLOW -> SpellVfxEntity.spawn(level, fx, p.getX(), p.getY(), p.getZ(), yaw, pitch, (float) fx.range, p.getId(), 0L);
            case TARGET_FOLLOW -> {
                List<LivingEntity> hits = SpellUtil.line(p, fx.range, 1.2);
                if (hits.isEmpty()) return;
                LivingEntity t = hits.get(0);
                SpellVfxEntity.spawn(level, fx, t.getX(), t.getY(), t.getZ(), yaw, pitch, (float) fx.range, t.getId(), 0L);
            }
            case GROUND_LINE -> groundLine(p, level, fx);
            case STRIKE -> {
                Vec3 target;
                List<LivingEntity> hits = SpellUtil.line(p, fx.range, 1.0);
                if (!hits.isEmpty()) {
                    target = hits.get(0).position();
                } else {
                    BlockHitResult hit = SpellUtil.rayBlock(p, fx.range);
                    if (hit.getType() == HitResult.Type.MISS) return;
                    target = hit.getLocation();
                }
                SpellVfxEntity.spawn(level, fx, target.x, target.y, target.z, yaw, pitch, 1f, -1, 0L);
            }
            default -> {
            }
        }
    }

    /** Eight ground heights along the horizontal look line, packed one byte each (1/8 block steps, -128 = no ground). */
    private static void groundLine(ServerPlayer p, ServerLevel level, Fx fx) {
        double yawRad = Math.toRadians(p.getYRot());
        Vec3 dir = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        long aux = 0L;
        for (int i = 0; i < 8; i++) {
            double d = 0.9 + i * (fx.range - 0.9) / 7.0;
            double x = p.getX() + dir.x * d, z = p.getZ() + dir.z * d;
            BlockHitResult hit = level.clip(new ClipContext(new Vec3(x, p.getY() + 2.0, z), new Vec3(x, p.getY() - 5.0, z),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
            int b = -128;
            if (hit.getType() != HitResult.Type.MISS) {
                b = Mth.clamp((int) Math.round((hit.getLocation().y - p.getY()) * 8.0), -120, 120);
            }
            aux |= ((long) (b & 0xFF)) << (8 * i);
        }
        SpellVfxEntity.spawn(level, fx, p.getX(), p.getY(), p.getZ(), p.getYRot(), 0f, (float) fx.range, -1, aux);
    }

    /** A lightning arc between two points (Thunder Chain, Static Shock, Spark Barrage). */
    public static void arc(ServerLevel level, Vec3 from, Vec3 to, Fx style) {
        if (!Config.SPELL_MODELS.get()) return;
        Vec3 d = to.subtract(from);
        double len = d.length();
        if (len < 0.05) return;
        float yaw = (float) Math.toDegrees(Math.atan2(-d.x, d.z));
        float pitch = (float) Math.toDegrees(-Math.atan2(d.y, Math.sqrt(d.x * d.x + d.z * d.z)));
        SpellVfxEntity.spawn(level, style, from.x, from.y, from.z, yaw, pitch, (float) len, -1, 0L);
    }
}
