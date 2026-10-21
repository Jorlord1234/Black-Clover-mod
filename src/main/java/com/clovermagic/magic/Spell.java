package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.clovermagic.Config;
import com.clovermagic.ModAttachments;
import com.clovermagic.entity.FireballProjectile;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;
import com.clovermagic.magic.MagicData;
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
    // ---------------------------------------------------------------- FIRE (skill tree spells)
    FLAME_BULLET("Flame Bullet", MagicAttribute.FIRE, 8, 8, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 24, 0.7)) {
            FireEffects.hurt(p, e, SpellUtil.magic(p), (float) (6 * pw));
            FireEffects.ignite(p, e, 80);
            break; // a bullet only hits the first target
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 24), ParticleTypes.FLAME, 1);
        SpellUtil.sound(p, SoundEvents.BLAZE_SHOOT, 0.8f, 1.3f);
        return true;
    }),
    EXPLODING_FIREBALL("Exploding Fireball", MagicAttribute.FIRE, 30, 40, (p, pw) -> {
        ServerLevel level = p.serverLevel();
        level.addFreshEntity(FireballProjectile.create(level, p, (float) (8 * pw), FireEffects.blastRadius(p, 3.0), 1.4f, 0.0f));
        SpellUtil.sound(p, SoundEvents.BLAZE_SHOOT, 1.0f, 0.6f);
        return true;
    }),
    EXPLODING_BUCKSHOT("Exploding Buckshot", MagicAttribute.FIRE, 45, 70, (p, pw) -> {
        ServerLevel level = p.serverLevel();
        for (float yaw : new float[] {-12f, -6f, 0f, 6f, 12f}) {
            level.addFreshEntity(FireballProjectile.create(level, p, (float) (4 * pw), FireEffects.blastRadius(p, 1.8), 1.2f, yaw));
        }
        SpellUtil.sound(p, SoundEvents.BLAZE_SHOOT, 1.0f, 0.8f);
        return true;
    }),
    SPIRAL_FLAME("Spiral Flame", MagicAttribute.FIRE, 14, 14, (p, pw) -> {
        double range = FireEffects.spiralRange(p, 7);
        for (LivingEntity e : SpellUtil.line(p, range, 1.6)) {
            FireEffects.hurt(p, e, SpellUtil.magic(p), (float) (5 * pw));
            FireEffects.ignite(p, e, 100);
        }
        SpellUtil.spiral(p, Math.min(range, SpellUtil.rayLength(p, range)), ParticleTypes.FLAME);
        SpellUtil.sound(p, SoundEvents.FIRECHARGE_USE, 0.9f, 1.2f);
        return true;
    }),
    SOL_LINEA("Sol Linea", MagicAttribute.FIRE, 22, 24, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 24, 0.6)) {
            FireEffects.hurt(p, e, SpellUtil.magic(p), (float) (9 * pw));
            FireEffects.ignite(p, e, 80);
        }
        double len = SpellUtil.rayLength(p, 24);
        SpellUtil.beam(p, len, ParticleTypes.END_ROD, 1);
        SpellUtil.beam(p, len, ParticleTypes.FLAME, 2);
        SpellUtil.sound(p, SoundEvents.BEACON_POWER_SELECT, 1.0f, 1.8f);
        return true;
    }),
    LEO_RUGIENS("Leo Rugiens", MagicAttribute.FIRE, 30, 50, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        Vec3 eye = p.getEyePosition();
        for (LivingEntity e : SpellUtil.around(p, 9)) {
            Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
            if (to.length() > 0.01 && to.normalize().dot(look) > 0.55) {
                FireEffects.hurt(p, e, SpellUtil.magic(p), (float) (7 * pw));
                FireEffects.ignite(p, e, 120);
                SpellUtil.push(e, look.scale(0.5).add(0, 0.15, 0));
            }
        }
        ServerLevel level = p.serverLevel();
        for (double d = 1.0; d <= 9.0; d += 0.5) {
            double spread = d * 0.3;
            level.sendParticles(ParticleTypes.FLAME, eye.x + look.x * d, eye.y - 0.3 + look.y * d, eye.z + look.z * d,
                    6, spread, spread * 0.6, spread, 0.02);
        }
        SpellUtil.sound(p, SoundEvents.ENDER_DRAGON_GROWL, 0.8f, 1.4f);
        return true;
    }),
    LEO_PALMA("Leo Palma", MagicAttribute.FIRE, 24, 60, (p, pw) -> {
        List<LivingEntity> hits = SpellUtil.line(p, 14, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        FireEffects.hurt(p, target, SpellUtil.magic(p), (float) (3 * pw));
        FireEffects.ignite(p, target, 60);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 6));
        SpellUtil.ring(p.serverLevel(), target.position(), 0.9, ParticleTypes.FLAME, 16);
        SpellUtil.ring(p.serverLevel(), target.position().add(0, 0.8, 0), 0.7, ParticleTypes.FLAME, 12);
        SpellUtil.sound(p, SoundEvents.FIRECHARGE_USE, 1.0f, 0.5f);
        return true;
    }),
    IGNIS_COLUMNA("Ignis Columna", MagicAttribute.FIRE, 28, 160, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 140, 1));
        for (LivingEntity e : SpellUtil.around(p, 3.5)) {
            FireEffects.hurt(p, e, SpellUtil.magic(p), (float) (4 * pw));
            FireEffects.ignite(p, e, 100);
            SpellUtil.push(e, SpellUtil.awayFrom(p, e, 0.6, 0.2));
        }
        ServerLevel level = p.serverLevel();
        for (double h = 0; h <= 3.0; h += 0.5) {
            SpellUtil.ring(level, p.position().add(0, h, 0), 1.3, ParticleTypes.FLAME, 14);
        }
        SpellUtil.sound(p, SoundEvents.FIRECHARGE_USE, 1.2f, 0.6f);
        return true;
    }),

    CALIDUS_BRACHIUM("Calidus Brachium", MagicAttribute.FIRE, 30, 60, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 6, 1.8)) {
            FireEffects.hurt(p, e, SpellUtil.physical(p), (float) (14 * pw));
            FireEffects.ignite(p, e, 100);
            SpellUtil.push(e, look.scale(1.4).add(0, 0.3, 0));
        }
        double len = SpellUtil.rayLength(p, 6);
        SpellUtil.beam(p, len, ParticleTypes.FLAME, 8);
        SpellUtil.beam(p, len, ParticleTypes.LAVA, 1);
        p.setDeltaMovement(p.getDeltaMovement().add(look.x * 0.5, 0.1, look.z * 0.5));
        p.hurtMarked = true;
        SpellUtil.sound(p, SoundEvents.BLAZE_SHOOT, 1.2f, 0.6f);
        return true;
    }),
    CALIDUS_BRACHIUM_BARRAGE("Calidus Brachium Barrage", MagicAttribute.FIRE, 45, 160, (p, pw) -> {
        List<LivingEntity> near = SpellUtil.around(p, 8);
        if (near.isEmpty()) return SpellUtil.fail(p, "No target");
        for (LivingEntity e : near) {
            FireEffects.hurt(p, e, SpellUtil.physical(p), (float) (12 * pw));
            FireEffects.ignite(p, e, 120);
            SpellUtil.push(e, SpellUtil.awayFrom(p, e, 0.6, 0.25));
            p.serverLevel().sendParticles(ParticleTypes.FLAME, e.getX(), e.getY() + 1, e.getZ(), 30, 0.4, 0.5, 0.4, 0.12);
            p.serverLevel().sendParticles(ParticleTypes.EXPLOSION, e.getX(), e.getY() + 1, e.getZ(), 2, 0.3, 0.3, 0.3, 0);
        }
        SpellUtil.sound(p, SoundEvents.FIRECHARGE_USE, 1.3f, 0.7f);
        return true;
    }),
    SPIRIT_DIVE_FIRE("Spirit Dive: Salamander", MagicAttribute.FIRE, 80, 3600,
            (p, pw) -> Transform.spiritDive(p, ParticleTypes.FLAME, MobEffects.FIRE_RESISTANCE, 0)),

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

    SEA_DRAGON_ROAR("Sea Dragon's Roar", MagicAttribute.WATER, 34, 60, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 20, 1.5)) {
            e.hurt(SpellUtil.magic(p), (float) (12 * pw));
            e.setRemainingFireTicks(0);
            SpellUtil.push(e, look.scale(1.1).add(0, 0.3, 0));
        }
        double len = SpellUtil.rayLength(p, 20);
        SpellUtil.beam(p, len, ParticleTypes.SPLASH, 8);
        SpellUtil.beam(p, len, ParticleTypes.BUBBLE_POP, 2);
        SpellUtil.sound(p, SoundEvents.GENERIC_SPLASH, 1.4f, 0.5f);
        return true;
    }),
    CLIMBING_COELACANTH("Climbing Coelacanth", MagicAttribute.WATER, 24, 50, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 12, 1.5)) {
            e.hurt(SpellUtil.magic(p), (float) (5 * pw));
            SpellUtil.push(e, new Vec3(0, 1.2, 0));
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 12), ParticleTypes.SPLASH, 6);
        SpellUtil.sound(p, SoundEvents.GENERIC_SPLASH, 1.0f, 0.9f);
        return true;
    }),
    SEA_DRAGON_NEST("Sea Dragon's Nest", MagicAttribute.WATER, 32, 200, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 1));
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 1));
        for (LivingEntity e : SpellUtil.around(p, 5)) {
            if (e instanceof Player) {
                e.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 0));
            } else {
                SpellUtil.push(e, SpellUtil.awayFrom(p, e, 1.0, 0.25));
            }
        }
        for (double h = 0; h <= 2.0; h += 0.5) {
            SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 4.5, ParticleTypes.SPLASH, 24);
        }
        SpellUtil.sound(p, SoundEvents.GENERIC_SPLASH, 1.2f, 0.7f);
        return true;
    }),

    VALKYRIE_DRESS("Valkyrie Dress", MagicAttribute.WATER, 55, 900, (p, pw) -> {
        int ticks = 1200;
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, 0));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ticks, 2));
        p.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, ticks, 0));
        p.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, ticks, 0));
        for (double h = 0; h <= 2.0; h += 0.4) {
            SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 1.2, ParticleTypes.SPLASH, 18);
        }
        p.serverLevel().sendParticles(ParticleTypes.BUBBLE_POP, p.getX(), p.getY() + 1, p.getZ(), 40, 0.5, 0.9, 0.5, 0.05);
        SpellUtil.sound(p, SoundEvents.BEACON_ACTIVATE, 1.0f, 1.4f);
        return true;
    }),
    SPIRIT_DIVE_WATER("Spirit Dive: Undine", MagicAttribute.WATER, 80, 3600,
            (p, pw) -> Transform.spiritDive(p, ParticleTypes.SPLASH, MobEffects.WATER_BREATHING, 0)),

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

    GALE_WHITE_BOW("Gale White Bow", MagicAttribute.WIND, 28, 60, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 24, 1.4)) {
            e.hurt(SpellUtil.physical(p), (float) (10 * pw));
        }
        double len = SpellUtil.rayLength(p, 24);
        SpellUtil.spiral(p, len, ParticleTypes.CLOUD);
        SpellUtil.beam(p, len, ParticleTypes.END_ROD, 1);
        SpellUtil.sound(p, SoundEvents.ARROW_SHOOT, 1.2f, 1.6f);
        return true;
    }),
    THIEF_WIND_CLOUD("Thief's Wind Cloud", MagicAttribute.WIND, 22, 200, (p, pw) -> {
        p.setDeltaMovement(p.getDeltaMovement().x, 0.9, p.getDeltaMovement().z);
        p.hurtMarked = true;
        p.fallDistance = 0;
        p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 240, 0));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 240, 1));
        SpellUtil.ring(p.serverLevel(), p.position(), 1.0, ParticleTypes.CLOUD, 16);
        SpellUtil.ring(p.serverLevel(), p.position(), 2.0, ParticleTypes.CLOUD, 24);
        SpellUtil.sound(p, SoundEvents.ENDER_DRAGON_FLAP, 0.9f, 1.4f);
        return true;
    }),
    SWIFT_WHITE_HAWK("Swift White Hawk", MagicAttribute.WIND, 20, 40, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 10, 1.3)) {
            e.hurt(SpellUtil.physical(p), (float) (8 * pw));
            SpellUtil.push(e, look.scale(0.6).add(0, 0.2, 0));
        }
        p.setDeltaMovement(look.x * 1.6, Math.max(look.y * 1.0, 0.1), look.z * 1.6);
        p.hurtMarked = true;
        p.fallDistance = 0;
        double len = SpellUtil.rayLength(p, 10);
        SpellUtil.beam(p, len, ParticleTypes.CLOUD, 3);
        SpellUtil.beam(p, len, ParticleTypes.SWEEP_ATTACK, 1);
        SpellUtil.sound(p, SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 1.8f);
        return true;
    }),

    SPIRIT_DIVE_WIND("Spirit Dive: Sylph", MagicAttribute.WIND, 80, 3600,
            (p, pw) -> Transform.spiritDive(p, ParticleTypes.CLOUD, MobEffects.SLOW_FALLING, 0)),

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

    BOULDER_TOSS("Boulder Toss", MagicAttribute.EARTH, 26, 50, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 16, 1.3)) {
            e.hurt(SpellUtil.physical(p), (float) (11 * pw));
            SpellUtil.push(e, look.scale(1.0).add(0, 0.3, 0));
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 16),
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()), 5);
        SpellUtil.sound(p, SoundEvents.STONE_BREAK, 1.3f, 0.6f);
        return true;
    }),
    STONE_BIND("Stone Bind", MagicAttribute.EARTH, 22, 90, (p, pw) -> {
        List<LivingEntity> hits = SpellUtil.line(p, 12, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        target.hurt(SpellUtil.physical(p), (float) (3 * pw));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 6));
        p.serverLevel().sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                target.getX(), target.getY() + 0.8, target.getZ(), 40, 0.5, 0.7, 0.5, 0.05);
        SpellUtil.sound(p, SoundEvents.STONE_PLACE, 1.2f, 0.5f);
        return true;
    }),
    EARTHEN_SHIELD("Earthen Shield", MagicAttribute.EARTH, 28, 200, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 3));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
        for (LivingEntity e : SpellUtil.around(p, 4)) {
            if (e instanceof Player) e.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1));
        }
        p.serverLevel().sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                p.getX(), p.getY() + 1, p.getZ(), 50, 0.7, 0.9, 0.7, 0.05);
        SpellUtil.sound(p, SoundEvents.STONE_PLACE, 1.3f, 0.7f);
        return true;
    }),

    SPIRIT_DIVE_EARTH("Spirit Dive: Gnome", MagicAttribute.EARTH, 80, 3600,
            (p, pw) -> Transform.spiritDive(p, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                    MobEffects.ABSORPTION, 3)),

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

    HAILSTORM("Hailstorm", MagicAttribute.ICE, 32, 80, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 12, 3.0)) {
            e.hurt(SpellUtil.magic(p), (float) (4 * pw));
            e.setTicksFrozen(e.getTicksRequiredToFreeze() + 60);
        }
        SpellUtil.beam(p, SpellUtil.rayLength(p, 12), ParticleTypes.SNOWFLAKE, 10);
        SpellUtil.sound(p, SoundEvents.GLASS_BREAK, 1.0f, 1.2f);
        return true;
    }),
    ICE_PRISON("Ice Prison", MagicAttribute.ICE, 24, 100, (p, pw) -> {
        List<LivingEntity> hits = SpellUtil.line(p, 14, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        target.hurt(SpellUtil.magic(p), (float) (3 * pw));
        target.setTicksFrozen(target.getTicksRequiredToFreeze() + 160);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 7));
        SpellUtil.ring(p.serverLevel(), target.position(), 0.9, ParticleTypes.SNOWFLAKE, 16);
        SpellUtil.ring(p.serverLevel(), target.position().add(0, 0.9, 0), 0.7, ParticleTypes.SNOWFLAKE, 12);
        SpellUtil.sound(p, SoundEvents.GLASS_PLACE, 1.2f, 0.6f);
        return true;
    }),
    FROST_BARRIER("Frost Barrier", MagicAttribute.ICE, 30, 200, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 0));
        for (LivingEntity e : SpellUtil.around(p, 4)) {
            e.setTicksFrozen(e.getTicksRequiredToFreeze() + 60);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
            SpellUtil.push(e, SpellUtil.awayFrom(p, e, 0.9, 0.2));
        }
        for (double h = 0; h <= 2.0; h += 0.5) {
            SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 3.5, ParticleTypes.SNOWFLAKE, 24);
        }
        SpellUtil.sound(p, SoundEvents.GLASS_PLACE, 1.2f, 0.5f);
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
            SpellUtil.zap(level, from, center, com.clovermagic.vfx.Fx.THUNDER_CHAIN);
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

    STATIC_SHOCK("Static Shock", MagicAttribute.LIGHTNING, 16, 30, (p, pw) -> {
        List<LivingEntity> hits = SpellUtil.line(p, 10, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        target.hurt(SpellUtil.magic(p), (float) (5 * pw));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 7));
        SpellUtil.zap(p.serverLevel(), p.getEyePosition().add(0, -0.3, 0), target.getBoundingBox().getCenter(), com.clovermagic.vfx.Fx.STATIC_SHOCK);
        SpellUtil.sound(p, SoundEvents.LIGHTNING_BOLT_IMPACT, 0.4f, 2.0f);
        return true;
    }),
    SPARK_BARRAGE("Spark Barrage", MagicAttribute.LIGHTNING, 30, 60, (p, pw) -> {
        List<LivingEntity> near = new ArrayList<>(SpellUtil.around(p, 12));
        if (near.isEmpty()) return SpellUtil.fail(p, "No target");
        near.sort(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(p)));
        Vec3 from = p.getEyePosition().add(0, -0.3, 0);
        for (int i = 0; i < Math.min(5, near.size()); i++) {
            LivingEntity e = near.get(i);
            e.hurt(SpellUtil.magic(p), (float) (4 * pw));
            SpellUtil.zap(p.serverLevel(), from, e.getBoundingBox().getCenter(), com.clovermagic.vfx.Fx.SPARK_BARRAGE);
        }
        SpellUtil.sound(p, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.4f, 2.0f);
        return true;
    }),
    OVERCHARGE("Overcharge", MagicAttribute.LIGHTNING, 24, 240, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 0));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1));
        p.serverLevel().sendParticles(ParticleTypes.ELECTRIC_SPARK, p.getX(), p.getY() + 1, p.getZ(), 50, 0.5, 0.9, 0.5, 0.3);
        SpellUtil.sound(p, SoundEvents.BEACON_POWER_SELECT, 0.8f, 2.0f);
        return true;
    }),

    TRUE_LIGHTNING("True Lightning Magic", MagicAttribute.LIGHTNING, 40, 80, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        Vec3 from = p.getEyePosition().add(0, -0.3, 0);
        double len = SpellUtil.rayLength(p, 14);
        for (LivingEntity e : SpellUtil.line(p, 14, 1.2)) {
            e.hurt(SpellUtil.magic(p), (float) (10 * pw));
        }
        Vec3 end = p.getEyePosition().add(look.scale(Math.max(0, len - 1.0))).add(0, -p.getEyeHeight(), 0);
        for (double d = 0; d < len; d += 1.5) {
            Vec3 a = from.add(look.scale(d)), b = from.add(look.scale(Math.min(len, d + 1.5)));
            SpellUtil.zap(p.serverLevel(), a, b);
        }
        p.teleportTo(end.x, end.y, end.z);
        p.fallDistance = 0;
        SpellUtil.sound(p, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5f, 2.0f);
        return true;
    }),
    LIGHTNING_BATTLE_FIEND("Lightning Battle Fiend", MagicAttribute.LIGHTNING, 70, 1200, (p, pw) -> {
        int ticks = 1200;
        MagicData md = p.getData(ModAttachments.MAGIC.get());
        md.fiendUntil = p.level().getGameTime() + ticks;
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ticks, 2));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.JUMP, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, ticks, 0));
        p.serverLevel().sendParticles(ParticleTypes.ELECTRIC_SPARK, p.getX(), p.getY() + 1, p.getZ(), 80, 0.6, 1.0, 0.6, 0.4);
        SpellUtil.sound(p, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.7f, 1.4f);
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

    DARK_PULSE("Dark Pulse", MagicAttribute.DARKNESS, 24, 40, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        Vec3 eye = p.getEyePosition();
        for (LivingEntity e : SpellUtil.around(p, 8)) {
            Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
            if (to.length() > 0.01 && to.normalize().dot(look) > 0.5) {
                e.hurt(SpellUtil.magic(p), (float) (7 * pw));
                e.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
                SpellUtil.push(e, look.scale(0.6).add(0, 0.1, 0));
            }
        }
        ServerLevel level = p.serverLevel();
        for (double d = 1.0; d <= 8.0; d += 0.5) {
            double spread = d * 0.25;
            level.sendParticles(ParticleTypes.SQUID_INK, eye.x + look.x * d, eye.y - 0.3 + look.y * d, eye.z + look.z * d,
                    4, spread, spread * 0.6, spread, 0.02);
        }
        SpellUtil.sound(p, SoundEvents.WITHER_SHOOT, 0.8f, 0.8f);
        return true;
    }),
    SHADOW_BIND("Shadow Bind", MagicAttribute.DARKNESS, 22, 100, (p, pw) -> {
        List<LivingEntity> hits = SpellUtil.line(p, 12, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        target.hurt(SpellUtil.magic(p), (float) (3 * pw));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 6));
        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
        SpellUtil.ring(p.serverLevel(), target.position(), 0.9, ParticleTypes.SQUID_INK, 16);
        SpellUtil.sound(p, SoundEvents.ENDERMAN_TELEPORT, 0.8f, 0.5f);
        return true;
    }),
    DREAD_MARK("Dread Mark", MagicAttribute.DARKNESS, 20, 120, (p, pw) -> {
        List<LivingEntity> hits = SpellUtil.line(p, 16, 1.2);
        if (hits.isEmpty()) return SpellUtil.fail(p, "No target");
        LivingEntity target = hits.get(0);
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
        SpellUtil.ring(p.serverLevel(), target.position().add(0, 1.0, 0), 0.6, ParticleTypes.SQUID_INK, 14);
        SpellUtil.sound(p, SoundEvents.WITHER_AMBIENT, 0.8f, 0.6f);
        return true;
    }),

    AVIDYA_SLASH("Dark Cloaked Avidya Slash", MagicAttribute.DARKNESS, 22, 30, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 14, 1.6)) {
            e.hurt(SpellUtil.magic(p), (float) (9 * pw));
        }
        double len = SpellUtil.rayLength(p, 14);
        SpellUtil.beam(p, len, ParticleTypes.SQUID_INK, 5);
        SpellUtil.beam(p, len, ParticleTypes.SWEEP_ATTACK, 1);
        SpellUtil.sound(p, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.5f);
        return true;
    }),
    DIMENSION_SLASH("Dark Cloaked Dimension Slash", MagicAttribute.DARKNESS, 45, 120, (p, pw) -> {
        for (LivingEntity e : SpellUtil.line(p, 30, 2.5)) {
            e.hurt(SpellUtil.magic(p), (float) (18 * pw));
            for (MobEffectInstance inst : new ArrayList<>(e.getActiveEffects())) {
                if (inst.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL) e.removeEffect(inst.getEffect());
            }
        }
        double len = SpellUtil.rayLength(p, 30);
        SpellUtil.beam(p, len, ParticleTypes.SQUID_INK, 10);
        SpellUtil.beam(p, len, ParticleTypes.PORTAL, 4);
        SpellUtil.beam(p, len, ParticleTypes.SWEEP_ATTACK, 1);
        SpellUtil.sound(p, SoundEvents.WARDEN_SONIC_BOOM, 0.8f, 1.5f);
        return true;
    }),
    BLACK_HOLE("Black Hole", MagicAttribute.DARKNESS, 35, 160, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        double dist = Math.min(6.0, SpellUtil.rayLength(p, 6.0));
        Vec3 center = p.getEyePosition().add(look.scale(dist));
        boolean any = false;
        for (LivingEntity e : SpellUtil.around(p, 14)) {
            Vec3 to = center.subtract(e.getBoundingBox().getCenter());
            if (to.length() > 8) continue;
            any = true;
            SpellUtil.push(e, to.length() < 0.01 ? Vec3.ZERO : to.normalize().scale(0.9));
            e.hurt(SpellUtil.magic(p), (float) (3 * pw));
        }
        ServerLevel level = p.serverLevel();
        SpellUtil.ring(level, center, 1.4, ParticleTypes.SQUID_INK, 20);
        level.sendParticles(ParticleTypes.PORTAL, center.x, center.y, center.z, 60, 1.2, 1.2, 1.2, 0.3);
        SpellUtil.sound(p, SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.4f);
        return any || SpellUtil.fail(p, "No target");
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

    PRISM_BURST("Prism Burst", MagicAttribute.LIGHT, 22, 50, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        Vec3 eye = p.getEyePosition();
        for (LivingEntity e : SpellUtil.around(p, 9)) {
            Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
            if (to.length() > 0.01 && to.normalize().dot(look) > 0.5) {
                float dmg = e.getType().is(EntityTypeTags.UNDEAD) ? 10 : 6;
                e.hurt(SpellUtil.magic(p), (float) (dmg * pw));
                e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
            }
        }
        ServerLevel level = p.serverLevel();
        for (double d = 1.0; d <= 9.0; d += 0.5) {
            double spread = d * 0.28;
            level.sendParticles(ParticleTypes.END_ROD, eye.x + look.x * d, eye.y - 0.3 + look.y * d, eye.z + look.z * d,
                    3, spread, spread * 0.6, spread, 0.02);
        }
        SpellUtil.sound(p, SoundEvents.BEACON_ACTIVATE, 1.0f, 1.8f);
        return true;
    }),
    LIGHT_SPEED_DASH("Light Speed Dash", MagicAttribute.LIGHT, 18, 40, (p, pw) -> {
        Vec3 look = p.getLookAngle();
        for (LivingEntity e : SpellUtil.line(p, 12, 1.0)) {
            e.hurt(SpellUtil.magic(p), (float) (6 * pw));
        }
        p.setDeltaMovement(look.x * 2.4, Math.max(look.y * 1.4, 0.1), look.z * 2.4);
        p.hurtMarked = true;
        p.fallDistance = 0;
        SpellUtil.beam(p, SpellUtil.rayLength(p, 12), ParticleTypes.END_ROD, 2);
        SpellUtil.sound(p, SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.8f, 2.0f);
        return true;
    }),
    RADIANT_AEGIS("Radiant Aegis", MagicAttribute.LIGHT, 28, 220, (p, pw) -> {
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
        for (LivingEntity e : SpellUtil.around(p, 5)) {
            if (e instanceof Player) {
                e.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
                e.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0));
            }
        }
        SpellUtil.ring(p.serverLevel(), p.position(), 2.0, ParticleTypes.END_ROD, 20);
        SpellUtil.ring(p.serverLevel(), p.position().add(0, 1.0, 0), 3.5, ParticleTypes.END_ROD, 30);
        SpellUtil.sound(p, SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 0.8f);
        return true;
    }),

    // ---------------------------------------------------------- ANTI-MAGIC
    // Anti-Magic users have no mana: "cost" is converted into hunger exhaustion.
    SUMMON_DEMON_SLAYER("Demon-Slayer Sword", MagicAttribute.ANTI_MAGIC, 8, 20,
            (p, pw) -> SwordSummon.toggle(p, com.clovermagic.ModItems.DEMON_SLAYER_SWORD.get(), 6000)),
    SUMMON_DEMON_DWELLER("Demon-Dweller Sword", MagicAttribute.ANTI_MAGIC, 10, 20,
            (p, pw) -> SwordSummon.toggle(p, com.clovermagic.ModItems.DEMON_DWELLER_SWORD.get(), 6000)),
    SUMMON_DEMON_DESTROYER("Demon-Destroyer Sword", MagicAttribute.ANTI_MAGIC, 12, 20,
            (p, pw) -> SwordSummon.toggle(p, com.clovermagic.ModItems.DEMON_DESTROYER_SWORD.get(), 6000)),
    DEVIL_UNION("Devil Union", MagicAttribute.ANTI_MAGIC, 60, 12000, (p, pw) -> {
        int ticks = 6000; // 5 minutes, like in the anime
        QuestHooks.award(p, "devil_union");
        long now = p.level().getGameTime();
        MagicData md = p.getData(ModAttachments.MAGIC.get());
        md.devilUnionUntil = now + ticks;
        md.flightUntil = Math.max(md.flightUntil, now + ticks);
        md.nullifyUntil = Math.max(md.nullifyUntil, now + 200);
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, 3));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, ticks, 0));
        p.addEffect(new MobEffectInstance(MobEffects.JUMP, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, ticks, 2));
        // marker effect: every client draws the horns/wings model while it lasts
        p.addEffect(new MobEffectInstance(com.clovermagic.ModEffects.DEVIL_UNION, ticks, 0, false, false, true));
        p.heal(8.0f);
        for (LivingEntity e : SpellUtil.around(p, 6)) {
            SpellUtil.push(e, SpellUtil.awayFrom(p, e, 1.2, 0.3));
        }
        DustParticleOptions red = new DustParticleOptions(new Vector3f(0.9f, 0.03f, 0.05f), 1.6f);
        for (double h = 0; h <= 2.0; h += 0.5) {
            SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 2.5 - h * 0.4, red, 28);
            SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 1.5, ParticleTypes.SQUID_INK, 16);
        }
        SpellUtil.sound(p, SoundEvents.ENDER_DRAGON_GROWL, 0.7f, 0.6f);
        SpellUtil.sound(p, SoundEvents.WITHER_SPAWN, 0.5f, 1.4f);
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
        boolean ok = cast.cast(player, power);
        if (ok) {
            try {
                SpellVfx.play(player, this);   // 3D model; must never be able to break a cast
            } catch (RuntimeException ex) {
                com.mojang.logging.LogUtils.getLogger().warn("Spell model failed for {}", this, ex);
            }
        }
        return ok;
    }

    public static List<Spell> forAttribute(MagicAttribute attribute) {
        List<Spell> out = new ArrayList<>();
        for (Spell s : values()) {
            if (s.attribute == attribute) out.add(s);
        }
        return out;
    }
}
