package com.clovermagic.magic;

import com.clovermagic.ModAttachments;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Long transformations: Spirit Dive (every spirit element) and flight handling shared with Devil Union. */
public final class Transform {
    private Transform() {
    }

    /** Spirit Dive: 2 minutes of strength, speed, resistance and regeneration, plus flight and one element bonus. */
    public static boolean spiritDive(ServerPlayer p, ParticleOptions particle, Holder<MobEffect> bonus, int bonusAmp) {
        int ticks = 2400;
        MagicData md = p.getData(ModAttachments.MAGIC.get());
        md.flightUntil = Math.max(md.flightUntil, p.level().getGameTime() + ticks);
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, ticks, 1));
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, ticks, 0));
        p.addEffect(new MobEffectInstance(bonus, ticks, bonusAmp));
        for (double h = 0; h <= 2.0; h += 0.5) {
            SpellUtil.ring(p.serverLevel(), p.position().add(0, h, 0), 2.0, particle, 24);
        }
        SpellUtil.sound(p, SoundEvents.BEACON_ACTIVATE, 1.2f, 0.7f);
        SpellUtil.sound(p, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.2f, 0.6f);
        return true;
    }

    /** Grants or removes creative-style flight depending on MagicData.flightUntil (called every tick). */
    public static void updateFlight(ServerPlayer sp, MagicData md) {
        boolean active = sp.level().getGameTime() < md.flightUntil;
        if (active && !md.flightGranted) {
            md.flightGranted = true;
            if (!sp.getAbilities().mayfly) {
                sp.getAbilities().mayfly = true;
                sp.onUpdateAbilities();
            }
        } else if (!active && md.flightGranted) {
            md.flightGranted = false;
            if (!sp.isCreative() && !sp.isSpectator()) {
                sp.getAbilities().mayfly = false;
                sp.getAbilities().flying = false;
                sp.onUpdateAbilities();
            }
        }
    }
}
