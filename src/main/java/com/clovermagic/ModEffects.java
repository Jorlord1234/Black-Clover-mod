package com.clovermagic;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Marker effects. The Devil Union effect does nothing by itself: its presence is what makes every player's client
 * draw the horns and wings model (effects are synced to everybody who can see the player, so no extra packet is needed).
 */
public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, CloverMagic.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> DEVIL_UNION = EFFECTS.register("devil_union",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xB0101A) {
            });

    private ModEffects() {
    }
}
