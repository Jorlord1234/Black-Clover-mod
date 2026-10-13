package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.clovermagic.Config;
import com.clovermagic.ModAttachments;
import com.clovermagic.ModEntities;
import com.clovermagic.entity.FireballProjectile;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Every spell in the mod. cost = mana (hunger for Anti-Magic), cooldown in ticks.
 * The cast function returns true if the spell actually fired (so mana is only spent on success).
 * "pw" is the grimoire power multiplier (1.0 normal, 1.4 four-leaf).
 */
public enum Spell {
    // ---------------------------------------------------------------- FIRE
    FLAME_BULLET("Flame Bullet", MagicAttribute.FIRE, 8, 6, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 24, 0.7)) {
            e.hurt(SpellUtil.magic(p), (float) (6 * pw));
            e.setRemainingFireTicks(80);
            break; // a bullet only hits the first target
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 24), ParticleTypes.FLAME, 1);
        SpellUtil.sound(p, SoundEvents.BLAZE_SHOOT, 0.8f, 1.3f);
        return true;
    }),
    CRIMSON_LION_BLAZE("Crimson Lion Blaze", MagicAttribute.FIRE, 35, 40, (p, pw) -> {
        ServerLevel level = p.serverLevel();
        FireballProjectile fireball = new FireballProjectile(ModEntities.FIREBALL.get(), level);
        fireball.setOwner(p);
        fireball.setDamage((float) (8 * pw));
        fireball.setPos(p.getX(), p.getEyeY() - 0.2, p.getZ());
        fireball.shootFromRotation(p, p.getXRot(), p.getYRot(), 0.0f, 1.4f, 0.0f);
        level.addFreshEntity(fireball);
        SpellUtil.sound(p, SoundEvents.BLAZE_SHOOT, 1.0f, 0.6f);
        return true;
    }),
    FIRE_RING("Flame Ring", MagicAttribute.FIRE, 25, 30, (p, pw) -> {
        ServerLevel level = p.serverLevel();
        for (LivingEntity e : SpellUtil.around(p, 5)) {
            e.hurt(SpellUtil.magic(p), (float) (6 * pw));
            e.setRemainingFireTicks(100);
        }
        SpellUtil.ring(level, p.position(), 2, ParticleTypes.FLAME, 16);
        SpellUtil.ring(level, p.position(), 4, ParticleTypes.FLAME, 28);
        SpellUtil.sound(p, SoundEvents.FIRECHARGE_USE, 1.0f, 1.0f);
        return true;
    }),

    // --------------------------------------------------------------- WATER
    WATER_BALL("Water Ball", MagicAttribute.WATER, 8, 8, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 14, 1.0)) {
            e.hurt(SpellUtil.magic(p), (float) (5 * pw));
            e.setRemainingFireTicks(0);
            SpellUtil.push(e, look.scale(0.7).add(0, 0.2, 0));
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 14), ParticleTypes.SPLASH, 6);
        SpellUtil.sound(p, SoundEvents.GENERIC_SPLASH, 1.0f, 1.2f);
        return true;
    }),
    WATER_VEIL("Water Veil", MagicAttribute.WATER, 20, 100, (p, pw) -> {
        p.setRemainingFireTicks(0);
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
        p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0));
        SpellUtil.ring(p.serverLevel(), p.position(), 1.2, ParticleTypes.SPLASH, 24);
        SpellUtil.sound(p, SoundEvents.GENERIC_SPLASH, 1.0f, 0.8f);
        return true;
    }),
    SEA_DRAGON_WAVE("Sea Dragon's Wave", MagicAttribute.WATER, 30, 40, (p, pw) -> {
        for (LivingEntity e : SpellUtil.around(p, 6)) {
            e.hurt(SpellUtil.magic(p), (float) (4 * pw));
            SpellUtil.push(e, SpellUtil.awayFrom(p, e, 1.5, 0.4));
        }
        SpellUtil.ring(p.serverLevel(), p.position(), 2, ParticleTypes.SPLASH, 24);
        SpellUtil.ring(p.serverLevel(), p.position(), 4, ParticleTypes.SPLASH, 36);
        SpellUtil.ring(p.serverLevel(), p.position(), 6, ParticleTypes.BUBBLE_POP, 40);
        SpellUtil.sound(p, SoundEvents.GENERIC_SPLASH, 1.5f, 0.6f);
        return true;
    }),

    // ---------------------------------------------------------------- WIND
    WIND_BLADE("Wind Blade", MagicAttribute.WIND, 10, 8, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 10, 1.0)) {
            e.hurt(SpellUtil.physical(p), (float) (7 * pw));
        }
        double len = SpellUtil.rayLength(p, 10);
        SpellUtil.beam(p, len, ParticleTypes.CLOUD, 2);
        SpellUtil.beam(p, len, ParticleTypes.SWEEP_ATTACK, 1);
        SpellUtil.sound(p, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 1.4f);
        return true;
    }),
    WIND_DASH("Wind Dash", MagicAttribute.WIND, 12, 15, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        p.setDeltaMovement(look.x * 1.9, Math.max(look.y * 1.2, 0.25), look.z * 1.9);
        p.hurtMarked = true;
        p.fallDistance = 0;
        p.serverLevel().sendParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 0.5, p.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
        SpellUtil.sound(p, SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.8f, 1.5f);
        return true;
    }),
    WHIRLWIND("Whirlwind", MagicAttribute.WIND, 25, 40, (p, pw) -> {
        for (LivingEntity e : SpellUtil.around(p, 5)) {
            SpellUtil.push(e, new Vec3(0, 1.1, 0));
        }
        p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));
        SpellUtil.ring(p.serverLevel(), p.position(), 1.5, ParticleTypes.CLOUD, 16);
        SpellUtil.ring(p.serverLevel(), p.position(), 3.0, ParticleTypes.CLOUD, 24);
        SpellUtil.sound(p, SoundEvents.ENDER_DRAGON_FLAP, 1.0f, 1.2f);
        return true;
    }),

    // --------------------------------------------------------------- EARTH
    EARTH_SPIKE("Earth Spike", MagicAttribute.EARTH, 12, 12, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 6, 1.0)) {
            e.hurt(SpellUtil.physical(p), (float) (8 * pw));
            SpellUtil.push(e, new Vec3(0, 0.9, 0));
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 6),
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()), 6);
        SpellUtil.sound(p, SoundEvents.GRAVEL_BREAK, 1.2f, 0.7f);
        return true;
    }),
    STONE_ARMOR("Stone Armor", MagicAttribute.EARTH, 25, 120, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1));
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 1));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 0));
        p.serverLevel().sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                p.getX(), p.getY() + 1, p.getZ(), 40, 0.5, 0.8, 0.5, 0.1);
        SpellUtil.sound(p, SoundEvents.STONE_PLACE, 1.2f, 0.6f);
        return true;
    }),
    QUAKE("Quake", MagicAttribute.EARTH, 30, 50, (p, pw) -> {
        for (LivingEntity e : SpellUtil.around(p, 5)) {
            e.hurt(SpellUtil.physical(p), (float) (5 * pw));
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            SpellUtil.push(e, new Vec3(0, 0.5, 0));
        }
        p.serverLevel().sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                p.getX(), p.getY() + 0.2, p.getZ(), 80, 2.5, 0.2, 2.5, 0.1);
        SpellUtil.sound(p, SoundEvents.GRAVEL_BREAK, 1.5f, 0.5f);
        return true;
    }),

    // ----------------------------------------------------------------- ICE
    FROST_LANCE("Frost Lance", MagicAttribute.ICE, 9, 8, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 14, 0.9)) {
            e.hurt(SpellUtil.magic(p), (float) (6 * pw));
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 14), ParticleTypes.SNOWFLAKE, 3);
        SpellUtil.sound(p, SoundEvents.GLASS_BREAK, 0.8f, 1.6f);
        return true;
    }),
    ICE_SHELL("Ice Shell", MagicAttribute.ICE, 22, 120, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1));
        p.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0));
        p.setRemainingFireTicks(0);
        p.serverLevel().sendParticles(ParticleTypes.SNOWFLAKE, p.getX(), p.getY() + 1, p.getZ(), 50, 0.6, 0.9, 0.6, 0.02);
        SpellUtil.sound(p, SoundEvents.GLASS_PLACE, 1.0f, 0.7f);
        return true;
    }),
    FROZEN_RING("Frozen Ring", MagicAttribute.ICE, 30, 50, (p, pw) -> {
        for (LivingEntity e : SpellUtil.around(p, 6)) {
            e.hurt(SpellUtil.magic(p), (float) (4 * pw));
            e.setTicksFrozen(e.getTicksRequiredToFreeze() + 100);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
        }
        SpellUtil.ring(p.serverLevel(), p.position(), 2, ParticleTypes.SNOWFLAKE, 20);
        SpellUtil.ring(p.serverLevel(), p.position(), 4, ParticleTypes.SNOWFLAKE, 30);
        SpellUtil.ring(p.serverLevel(), p.position(), 6, ParticleTypes.SNOWFLAKE, 40);
        SpellUtil.sound(p, SoundEvents.GLASS_BREAK, 1.2f, 0.6f);
        return true;
    }),

    // ----------------------------------------------------------- LIGHTNING
    THUNDER_BOLT("Thunder Bolt", MagicAttribute.LIGHTNING, 30, 30, (p, pw) -> {
        ServerLevel level = p.serverLevel();
        Vec3 target;
        List<LivingEntity> hits = SpellUtil.line(p, 40, 1.0);
        if (!hits.isEmpty()) {
            target = hits.get(0).position();
        } else {
            BlockHitResult hit = SpellUtil.rayBlock(p, 40);
            if (hit.getType() == HitResult.Type.MISS) return SpellUtil.fail(p, "No target");
            target = hit.getLocation();
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) return false;
        bolt.moveTo(target.x, target.y, target.z);
        bolt.setCause(p);
        level.addFreshEntity(bolt);
        return true;
    }),
    LIGHTNING_SPEED("Lightning Speed", MagicAttribute.LIGHTNING, 20, 100, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 2));
        p.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 1));
        p.serverLevel().sendParticles(ParticleTypes.ELECTRIC_SPARK, p.getX(), p.getY() + 1, p.getZ(), 40, 0.4, 0.8, 0.4, 0.2);
        SpellUtil.sound(p, SoundEvents.BEACON_ACTIVATE, 0.8f, 2.0f);
        return true;
    }),
    THUNDER_CHAIN("Thunder Chain", MagicAttribute.LIGHTNING, 18, 20, (p, pw) -> {
        List<LivingEntity> first = SpellUtil.line(p, 20, 1.2);
        if (first.isEmpty()) return SpellUtil.fail(p, "No target");
        ServerLevel level = p.serverLevel();
        Set<LivingEntity> done = new HashSet<>();
        LivingEntity cur = first.get(0);
        Vec3 from = p.getEyePosition().add(0, -0.3, 0);
        for (int i = 0; i < 5 && cur != null; i++) {
            done.add(cur);
            Vec3 center = cur.getBoundingBox().getCenter();
            SpellUtil.zap(level, from, center);
            cur.hurt(SpellUtil.magic(p), (float) (6 * pw));
            from = center;
            LivingEntity best = null;
            double bestDist = 36;
            for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class, cur.getBoundingBox().inflate(6),
                    t -> t != p && t.isAlive() && !done.contains(t))) {
                double d = other.distanceToSqr(cur);
                if (d < bestDist) {
                    bestDist = d;
                    best = other;
                }
            }
            cur = best;
        }
        SpellUtil.sound(p, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5f, 2.0f);
        return true;
    }),

    // ------------------------------------------------------------ DARKNESS
    BLACK_SLASH("Black Slash", MagicAttribute.DARKNESS, 12, 10, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 8, 1.0)) {
            e.hurt(SpellUtil.magic(p), (float) (7 * pw));
            e.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
            e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        }
        double len = SpellUtil.rayLength(p, 8);
        SpellUtil.beam(p, len, ParticleTypes.SQUID_INK, 2);
        SpellUtil.beam(p, len, ParticleTypes.SWEEP_ATTACK, 1);
        SpellUtil.sound(p, SoundEvents.WITHER_SHOOT, 0.7f, 1.4f);
        return true;
    }),
    DARK_SHROUD("Dark Shroud", MagicAttribute.DARKNESS, 18, 100, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0));
        p.serverLevel().sendParticles(ParticleTypes.SQUID_INK, p.getX(), p.getY() + 1, p.getZ(), 40, 0.5, 0.8, 0.5, 0.05);
        SpellUtil.sound(p, SoundEvents.ENDERMAN_TELEPORT, 0.8f, 0.6f);
        return true;
    }),
    ABYSS_DRAIN("Abyss Drain", MagicAttribute.DARKNESS, 28, 60, (p, pw) -> {
        int count = 0;
        for (LivingEntity e : SpellUtil.around(p, 6)) {
            e.hurt(SpellUtil.magic(p), (float) (3 * pw));
            e.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0));
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 0));
            count++;
        }
        p.heal(Math.min(8, count * 2));
        SpellUtil.ring(p.serverLevel(), p.position(), 3, ParticleTypes.SQUID_INK, 24);
        SpellUtil.ring(p.serverLevel(), p.position(), 6, ParticleTypes.SQUID_INK, 36);
        SpellUtil.sound(p, SoundEvents.WITHER_AMBIENT, 0.8f, 0.7f);
        return true;
    }),

    // --------------------------------------------------------------- LIGHT
    LIGHT_BEAM("Holy Ray", MagicAttribute.LIGHT, 14, 12, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 16, 0.8)) {
            float dmg = e.getType().is(EntityTypeTags.UNDEAD) ? 12 : 6;
            e.hurt(SpellUtil.magic(p), (float) (dmg * pw));
            e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 16), ParticleTypes.END_ROD, 2);
        SpellUtil.sound(p, SoundEvents.BEACON_POWER_SELECT, 1.0f, 1.6f);
        return true;
    }),
    HEALING("Healing Light", MagicAttribute.LIGHT, 20, 60, (p, pw) -> {
        p.heal((float) (8 * pw));
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
        for (LivingEntity e : SpellUtil.around(p, 4)) {
            if (e instanceof Player) {
                e.heal((float) (4 * pw));
            }
        }
        p.serverLevel().sendParticles(ParticleTypes.HAPPY_VILLAGER, p.getX(), p.getY() + 1, p.getZ(), 25, 0.5, 0.8, 0.5, 0.0);
        SpellUtil.sound(p, SoundEvents.PLAYER_LEVELUP, 0.8f, 1.5f);
        return true;
    }),
    PURIFY("Purify", MagicAttribute.LIGHT, 18, 60, (p, pw) -> {
        for (MobEffectInstance inst : new ArrayList<>(p.getActiveEffects())) {
            if (inst.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                p.removeEffect(inst.getEffect());
            }
        }
        p.setRemainingFireTicks(0);
        p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0));
        SpellUtil.ring(p.serverLevel(), p.position(), 1.5, ParticleTypes.END_ROD, 20);
        SpellUtil.sound(p, SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 1.2f);
        return true;
    }),

    // ---------------------------------------------------------- ANTI-MAGIC
    // Anti-Magic users have no mana: "cost" is converted into hunger exhaustion.
    MAGIC_NULLIFY("Magic Nullify", MagicAttribute.ANTI_MAGIC, 15, 30, (p, pw) -> {
        p.getData(ModAttachments.MAGIC.get()).nullifyUntil = p.level().getGameTime() + Config.NULLIFY_WINDOW_TICKS.get();
        for (LivingEntity e : SpellUtil.around(p, 7)) {
            for (MobEffectInstance inst : new ArrayList<>(e.getActiveEffects())) {
                e.removeEffect(inst.getEffect());
            }
            SpellUtil.push(e, SpellUtil.awayFrom(p, e, 0.8, 0.2));
        }
        for (MobEffectInstance inst : new ArrayList<>(p.getActiveEffects())) {
            if (inst.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                p.removeEffect(inst.getEffect());
            }
        }
        SpellUtil.ring(p.serverLevel(), p.position(), 3.5, ParticleTypes.SMOKE, 30);
        SpellUtil.ring(p.serverLevel(), p.position(), 7, ParticleTypes.ASH, 40);
        SpellUtil.sound(p, SoundEvents.BEACON_DEACTIVATE, 1.0f, 0.8f);
        return true;
    }),
    BLACK_ASTA("Black Asta", MagicAttribute.ANTI_MAGIC, 25, 200, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 2));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 0));
        p.serverLevel().sendParticles(ParticleTypes.SOUL_FIRE_FLAME, p.getX(), p.getY() + 1, p.getZ(), 40, 0.4, 0.8, 0.4, 0.05);
        SpellUtil.sound(p, SoundEvents.WITHER_SPAWN, 0.4f, 1.8f);
        return true;
    }),
    DEMON_DWELLER_SLASH("Demon-Dweller Slash", MagicAttribute.ANTI_MAGIC, 20, 20, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 8, 1.2)) {
            for (MobEffectInstance inst : new ArrayList<>(e.getActiveEffects())) {
                e.removeEffect(inst.getEffect());
            }
            e.hurt(SpellUtil.physical(p), 14f);
            SpellUtil.push(e, look.scale(0.8).add(0, 0.2, 0));
        }
        p.setDeltaMovement(p.getDeltaMovement().add(look.x * 0.8, 0.1, look.z * 0.8));
        p.hurtMarked = true;
        double len = SpellUtil.rayLength(p, 8);
        SpellUtil.beam(p, len, ParticleTypes.SWEEP_ATTACK, 1);
        SpellUtil.beam(p, len, ParticleTypes.SOUL_FIRE_FLAME, 2);
        SpellUtil.sound(p, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.6f);
        return true;
    });

    @FunctionalInterface
    public interface Cast {
        boolean cast(ServerPlayer player, double power);
    }

    public final String displayName;
    public final MagicAttribute attribute;
    public final int cost;
    public final int cooldown;
    private final Cast cast;

    Spell(String displayName, MagicAttribute attribute, int cost, int cooldown, Cast cast) {
        this.displayName = displayName;
        this.attribute = attribute;
        this.cost = cost;
        this.cooldown = cooldown;
        this.cast = cast;
    }

    /** Magic level required: 1st spell of an attribute = 1, 2nd = 3, 3rd = 5. */
    public int levelNeeded() {
        int idx = forAttribute(attribute).indexOf(this);
        return 1 + 2 * Math.max(0, idx);
    }

    public boolean cast(ServerPlayer player, double power) {
        return cast.cast(player, power);
    }

    public static List<Spell> forAttribute(MagicAttribute attribute) {
        List<Spell> out = new ArrayList<>();
        for (Spell s : values()) {
            if (s.attribute == attribute) out.add(s);
        }
        return out;
    }
}
