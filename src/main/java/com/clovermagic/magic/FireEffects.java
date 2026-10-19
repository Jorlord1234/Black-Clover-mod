package com.clovermagic.magic;

import com.clovermagic.ModAttachments;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/** Fire skill tree passives and upgrades, applied to every fire spell through these helpers. */
public final class FireEffects {
    private FireEffects() {
    }

    private static boolean has(ServerPlayer p, String node) {
        return p.getData(ModAttachments.MAGIC.get()).unlocked.contains(node);
    }

    /** Damage multiplier for a fire hit on this target. */
    public static double multiplier(ServerPlayer p, LivingEntity target) {
        double m = 1.0;
        if (has(p, "burn_damage")) m *= 1.10;
        if (target.isInWaterOrRain()) m *= 0.7;          // fire is weak against water
        if (has(p, "fire_affinity") && target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) m *= 1.25;
        return m;
    }

    public static boolean hurt(ServerPlayer p, LivingEntity target, DamageSource source, float amount) {
        return target.hurt(source, (float) (amount * multiplier(p, target)));
    }

    public static void ignite(ServerPlayer p, LivingEntity target, int ticks) {
        int t = has(p, "long_burn") ? (int) (ticks * 1.5) : ticks;
        target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), t));
    }

    /** Fireball blast radius: base, plus 1.5 with the Blast Radius upgrade. */
    public static double blastRadius(ServerPlayer p, double base) {
        return has(p, "blast_radius") ? base + 1.5 : base;
    }

    /** Spiral Flame range: base, plus 4 with the Spiral Range upgrade. */
    public static double spiralRange(ServerPlayer p, double base) {
        return has(p, "spiral_range") ? base + 4 : base;
    }
}
