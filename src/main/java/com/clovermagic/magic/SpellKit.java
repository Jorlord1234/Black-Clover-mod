package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Building blocks for spells: a new spell is usually one line that calls one of these (beam, area, multi, strike, dash,
 * buff, wall, cage...) with numbers, a particle and an "on hit" effect. Damage is multiplied by the spell power (pw).
 */
public final class SpellKit {
    private SpellKit() {
    }

    // ---------------------------------------------------------------- on-hit effects
    public static Consumer<LivingEntity> eff(Holder<MobEffect> effect, int ticks, int amp) {
        return e -> e.addEffect(new MobEffectInstance(effect, ticks, amp));
    }

    public static Consumer<LivingEntity> burn(ServerPlayer p, int ticks) {
        return e -> FireEffects.ignite(p, e, ticks);
    }

    public static Consumer<LivingEntity> freeze(int extraTicks) {
        return e -> e.setTicksFrozen(e.getTicksRequiredToFreeze() + extraTicks);
    }

    public static Consumer<LivingEntity> knock(ServerPlayer p, double power, double lift) {
        return e -> SpellUtil.push(e, SpellUtil.awayFrom(p, e, power, lift));
    }

    /** Rips every good effect off the target (anti-magic and dark magic). */
    public static Consumer<LivingEntity> strip() {
        return e -> {
            for (MobEffectInstance inst : new ArrayList<>(e.getActiveEffects())) {
                if (inst.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL) e.removeEffect(inst.getEffect());
            }
        };
    }

    @SafeVarargs
    public static Consumer<LivingEntity> all(Consumer<LivingEntity>... list) {
        return e -> {
            for (Consumer<LivingEntity> c : list) c.accept(e);
        };
    }

    private static void hurt(ServerPlayer p, LivingEntity e, double dmg, boolean phys, Consumer<LivingEntity> onHit) {
        e.hurt(phys ? SpellUtil.physical(p) : SpellUtil.magic(p), (float) dmg);
        if (onHit != null) onHit.accept(e);
    }

    // ---------------------------------------------------------------- attacks
    /** A line in front of you that hits everything on it. */
    public static boolean beam(ServerPlayer p, double pw, double range, double radius, double dmg, boolean phys,
                               ParticleOptions particle, int perStep, SoundEvent snd, Consumer<LivingEntity> onHit) {
        for (LivingEntity e : SpellUtil.line(p, range, radius)) hurt(p, e, dmg * pw, phys, onHit);
        SpellUtil.beam(p, SpellUtil.rayLength(p, range), particle, perStep);
        SpellUtil.sound(p, snd, 1.2f, 0.9f);
        return true;
    }

    /** Hits the nearest enemies around you (up to count). */
    public static boolean multi(ServerPlayer p, double pw, int count, double range, double dmg, boolean phys,
                                ParticleOptions particle, SoundEvent snd, Consumer<LivingEntity> onHit) {
        List<LivingEntity> near = new ArrayList<>(SpellUtil.around(p, range));
        if (near.isEmpty()) return SpellUtil.fail(p, "No target");
        near.sort(Comparator.comparingDouble(e -> e.distanceToSqr(p)));
        Vec3 from = p.getEyePosition().add(0, -0.3, 0);
        ServerLevel level = p.serverLevel();
        for (int i = 0; i < Math.min(count, near.size()); i++) {
            LivingEntity e = near.get(i);
            hurt(p, e, dmg * pw, phys, onHit);
            Vec3 to = e.getBoundingBox().getCenter();
            for (double t = 0; t <= 1.0; t += 0.1) {
                Vec3 pt = from.add(to.subtract(from).scale(t));
                level.sendParticles(particle, pt.x, pt.y, pt.z, 1, 0.05, 0.05, 0.05, 0);
            }
            level.sendParticles(particle, to.x, to.y, to.z, 12, 0.3, 0.4, 0.3, 0.05);
        }
        SpellUtil.sound(p, snd, 1.0f, 1.0f);
        return true;
    }

    /** Hits everything around you in a radius. */
    public static boolean area(ServerPlayer p, double pw, double radius, double dmg, boolean phys, double push, double lift,
                               ParticleOptions ring, SoundEvent snd, Consumer<LivingEntity> onHit) {
        for (LivingEntity e : SpellUtil.around(p, radius)) {
            hurt(p, e, dmg * pw, phys, onHit);
            if (push != 0 || lift != 0) SpellUtil.push(e, SpellUtil.awayFrom(p, e, push, lift));
        }
        ServerLevel level = p.serverLevel();
        for (double r = 1.5; r <= radius; r += 2.0) SpellUtil.ring(level, p.position().add(0, 0.2, 0), r, ring, (int) (8 + r * 5));
        SpellUtil.sound(p, snd, 1.3f, 0.7f);
        return true;
    }

    /** One heavy hit on the first target in front of you, with a splash around it (and a lightning bolt if asked). */
    public static boolean strike(ServerPlayer p, double pw, double range, double dmg, double splash, boolean lightning,
                                 ParticleOptions particle, SoundEvent snd, Consumer<LivingEntity> onHit) {
        ServerLevel level = p.serverLevel();
        List<LivingEntity> hits = SpellUtil.line(p, range, 1.4);
        Vec3 point = hits.isEmpty()
                ? p.getEyePosition().add(p.getLookAngle().scale(Math.max(2.0, SpellUtil.rayLength(p, range))))
                : hits.get(0).position();
        if (!hits.isEmpty()) hurt(p, hits.get(0), dmg * pw, false, onHit);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new net.minecraft.world.phys.AABB(point, point).inflate(splash))) {
            if (e == p || e instanceof Player || (!hits.isEmpty() && e == hits.get(0))) continue;
            hurt(p, e, dmg * pw * 0.5, false, onHit);
        }
        if (lightning) bolt(level, point);
        SpellUtil.ring(level, point.add(0, 0.2, 0), Math.max(1.0, splash * 0.6), particle, 24);
        level.sendParticles(particle, point.x, point.y + 1, point.z, 40, 0.6, 0.8, 0.6, 0.1);
        SpellUtil.sound(p, snd, 1.4f, 0.8f);
        return true;
    }

    /** Visual lightning bolt (no fire, no damage of its own). */
    public static void bolt(ServerLevel level, Vec3 at) {
        LightningBolt b = EntityType.LIGHTNING_BOLT.create(level);
        if (b == null) return;
        b.moveTo(at.x, at.y, at.z);
        b.setVisualOnly(true);
        level.addFreshEntity(b);
    }

    /** A lightning bolt on each of the nearest enemies. */
    public static boolean bolts(ServerPlayer p, double pw, int count, double range, double dmg) {
        List<LivingEntity> near = new ArrayList<>(SpellUtil.around(p, range));
        if (near.isEmpty()) return SpellUtil.fail(p, "No target");
        near.sort(Comparator.comparingDouble(e -> e.distanceToSqr(p)));
        for (int i = 0; i < Math.min(count, near.size()); i++) {
            LivingEntity e = near.get(i);
            hurt(p, e, dmg * pw, false, null);
            bolt(p.serverLevel(), e.position());
        }
        return true;
    }

    /** You rush forward and cut everything on the way. */
    public static boolean dash(ServerPlayer p, double pw, double speed, double range, double dmg, boolean phys,
                               ParticleOptions particle, Consumer<LivingEntity> onHit) {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, range, 1.3)) hurt(p, e, dmg * pw, phys, onHit);
        SpellUtil.beam(p, SpellUtil.rayLength(p, range), particle, 3);
        p.setDeltaMovement(look.x * speed, Math.max(look.y * speed * 0.6, 0.1), look.z * speed);
        p.hurtMarked = true;
        p.fallDistance = 0;
        SpellUtil.sound(p, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.7f);
        return true;
    }

    // ---------------------------------------------------------------- movement
    /** Teleports you forward (stops at walls). */
    public static boolean blink(ServerPlayer p, double maxDist) {
        Vec3 look = p.getLookAngle();
        double len = SpellUtil.rayLength(p, maxDist);
        Vec3 end = p.getEyePosition().add(look.scale(Math.max(0, len - 1.0))).add(0, -p.getEyeHeight(), 0);
        p.serverLevel().sendParticles(ParticleTypes.PORTAL, p.getX(), p.getY() + 1, p.getZ(), 30, 0.4, 0.8, 0.4, 0.3);
        p.teleportTo(end.x, end.y, end.z);
        p.fallDistance = 0;
        p.serverLevel().sendParticles(ParticleTypes.PORTAL, end.x, end.y + 1, end.z, 30, 0.4, 0.8, 0.4, 0.3);
        SpellUtil.sound(p, SoundEvents.ENDERMAN_TELEPORT, 0.8f, 1.2f);
        return true;
    }

    /** Teleports you right behind the nearest enemy (or forward if there is none). */
    public static boolean stepBehindNearest(ServerPlayer p, double range) {
        LivingEntity best = null;
        for (LivingEntity e : SpellUtil.around(p, range)) {
            if (best == null || e.distanceToSqr(p) < best.distanceToSqr(p)) best = e;
        }
        if (best == null) return blink(p, 8);
        Vec3 behind = best.position().subtract(best.getLookAngle().multiply(1, 0, 1).normalize().scale(1.4));
        p.serverLevel().sendParticles(ParticleTypes.SQUID_INK, p.getX(), p.getY() + 1, p.getZ(), 30, 0.4, 0.8, 0.4, 0.05);
        p.teleportTo(behind.x, best.getY(), behind.z);
        p.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, best.getEyePosition());
        SpellUtil.sound(p, SoundEvents.ENDERMAN_TELEPORT, 0.8f, 0.7f);
        return true;
    }

    // ---------------------------------------------------------------- self and friends
    /** Effects on yourself (and on players within allyRadius, if bigger than 0). */
    public static boolean buff(ServerPlayer p, ParticleOptions ring, double allyRadius, MobEffectInstance... effects) {
        for (MobEffectInstance i : effects) p.addEffect(new MobEffectInstance(i));
        if (allyRadius > 0) {
            for (LivingEntity e : SpellUtil.around(p, allyRadius)) {
                if (e instanceof Player) for (MobEffectInstance i : effects) e.addEffect(new MobEffectInstance(i));
            }
        }
        for (double h = 0; h <= 2.0; h += 0.5) SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 1.6, ring, 18);
        SpellUtil.sound(p, SoundEvents.BEACON_POWER_SELECT, 1.0f, 1.0f);
        return true;
    }

    public static MobEffectInstance fx(Holder<MobEffect> effect, int ticks, int amp) {
        return new MobEffectInstance(effect, ticks, amp);
    }

    /** Heals you and the players around you. */
    public static void healAllies(ServerPlayer p, double radius, float amount) {
        p.heal(amount);
        for (LivingEntity e : SpellUtil.around(p, radius)) {
            if (e instanceof Player) e.heal(amount);
        }
    }

    // ---------------------------------------------------------------- blocks
    /** A wall (halfWidth*2+1 wide, height high) in front of you, gone after ticks. */
    public static boolean wall(ServerPlayer p, BlockState state, int halfWidth, int height, int ticks, ParticleOptions particle) {
        ServerLevel level = p.serverLevel();
        Vec3 look = p.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        if (flat.lengthSqr() < 1.0E-4) flat = new Vec3(0, 0, 1);
        flat = flat.normalize();
        Vec3 side = new Vec3(-flat.z, 0, flat.x);
        Vec3 center = p.position().add(flat.scale(3.0));
        for (int w = -halfWidth; w <= halfWidth; w++) {
            for (int h = 0; h < height; h++) {
                BlockPos pos = BlockPos.containing(center.add(side.scale(w)).add(0, h, 0));
                if (TempBlocks.place(p, pos, state, ticks)) {
                    level.sendParticles(particle, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.02);
                }
            }
        }
        SpellUtil.sound(p, SoundEvents.STONE_PLACE, 1.2f, 0.7f);
        return true;
    }

    /** A hollow 3x3x3 cage of blocks around the first target in front of you (it also takes damage). */
    public static boolean cage(ServerPlayer p, double pw, double range, BlockState state, int ticks, double dmg,
                               ParticleOptions particle) {
        List<LivingEntity> hits = SpellUtil.line(p, range, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        hurt(p, target, dmg * pw, false, eff(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, ticks, 6));
        ServerLevel level = p.serverLevel();
        BlockPos c = target.blockPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    boolean shell = Math.abs(x) == 1 || Math.abs(z) == 1 || y == 2;
                    if (shell) TempBlocks.place(p, c.offset(x, y, z), state, ticks);
                }
            }
        }
        level.sendParticles(particle, target.getX(), target.getY() + 1, target.getZ(), 30, 0.6, 0.8, 0.6, 0.05);
        SpellUtil.sound(p, SoundEvents.STONE_PLACE, 1.2f, 0.5f);
        return true;
    }
}
