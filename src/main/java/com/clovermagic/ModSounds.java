package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.magic.MagicAttribute;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Custom sounds (assets/clovermagic/sounds/*.ogg, listed in sounds.json). One cast sound per element. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, CloverMagic.MODID);

    private static Supplier<SoundEvent> reg(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, name)));
    }

    public static final Supplier<SoundEvent> CAST_START = reg("cast_start");
    public static final Supplier<SoundEvent> CAST_FIRE = reg("cast_fire");
    public static final Supplier<SoundEvent> CAST_WATER = reg("cast_water");
    public static final Supplier<SoundEvent> CAST_WIND = reg("cast_wind");
    public static final Supplier<SoundEvent> CAST_EARTH = reg("cast_earth");
    public static final Supplier<SoundEvent> CAST_ICE = reg("cast_ice");
    public static final Supplier<SoundEvent> CAST_LIGHTNING = reg("cast_lightning");
    public static final Supplier<SoundEvent> CAST_LIGHT = reg("cast_light");
    public static final Supplier<SoundEvent> CAST_DARKNESS = reg("cast_darkness");
    public static final Supplier<SoundEvent> CAST_ANTI = reg("cast_anti");
    public static final Supplier<SoundEvent> LEVEL_UP = reg("level_up");
    public static final Supplier<SoundEvent> GRIMOIRE_CHOSEN = reg("grimoire_chosen");
    public static final Supplier<SoundEvent> DEVIL_GROWL = reg("devil_growl");
    public static final Supplier<SoundEvent> DEMON_GROWL = reg("demon_growl");

    private ModSounds() {
    }

    public static SoundEvent forAttribute(MagicAttribute attr) {
        return switch (attr) {
            case FIRE -> CAST_FIRE.get();
            case WATER -> CAST_WATER.get();
            case WIND -> CAST_WIND.get();
            case EARTH -> CAST_EARTH.get();
            case ICE -> CAST_ICE.get();
            case LIGHTNING -> CAST_LIGHTNING.get();
            case LIGHT -> CAST_LIGHT.get();
            case DARKNESS -> CAST_DARKNESS.get();
            case ANTI_MAGIC -> CAST_ANTI.get();
        };
    }
}
