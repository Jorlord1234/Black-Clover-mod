package com.clovermagic.magic;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/** Short descriptions shown in the spell list screen (K). */
public final class SpellInfo {
    private static final Map<Spell, String> DESC = new EnumMap<>(Spell.class);

    static {
        DESC.put(Spell.FLAME_BULLET, "Fires a fast bullet of flame at the first target in line. Sets it on fire.");
        DESC.put(Spell.CRIMSON_LION_BLAZE, "Hurls a heavy fireball that flies until it hits something, then bursts and burns everything close to it.");
        DESC.put(Spell.FIRE_RING, "Rings of flame burst out around you, burning every enemy close to you.");
        DESC.put(Spell.WATER_BALL, "A jet of water that hits everything in a line, pushes it back and puts out fires.");
        DESC.put(Spell.WATER_VEIL, "Wraps you in water: damage resistance, water breathing, and no more burning.");
        DESC.put(Spell.SEA_DRAGON_WAVE, "A wave erupts around you and throws every nearby enemy away from you.");
        DESC.put(Spell.WIND_BLADE, "A cutting blade of wind that slices everything in a line ahead of you.");
        DESC.put(Spell.WIND_DASH, "Rides the wind to dash forward. You take no fall damage from the dash.");
        DESC.put(Spell.WHIRLWIND, "A whirlwind lifts nearby enemies into the air and lets you fall slowly.");
        DESC.put(Spell.EARTH_SPIKE, "Rock spikes burst from the ground along a line, hurting and launching enemies.");
        DESC.put(Spell.STONE_ARMOR, "Covers you in stone: strong resistance and extra hearts, but you move slower.");
        DESC.put(Spell.QUAKE, "Shakes the ground around you, hurting and slowing every nearby enemy.");
        DESC.put(Spell.FROST_LANCE, "A lance of ice pierces everything in a line and slows it down.");
        DESC.put(Spell.ICE_SHELL, "An armor of ice: resistance, extra hearts and fire immunity for a while.");
        DESC.put(Spell.FROZEN_RING, "Freezes everything around you in a wide ring, hurting and almost stopping it.");
        DESC.put(Spell.THUNDER_BOLT, "Calls down a lightning bolt on your target, or on the spot you look at.");
        DESC.put(Spell.LIGHTNING_SPEED, "Lightning runs through your body: great speed and jump for a while.");
        DESC.put(Spell.THUNDER_CHAIN, "Lightning leaps from your target to up to four more enemies close to it.");
        DESC.put(Spell.BLACK_SLASH, "A black slash through everything in a line. Withers and blinds what it hits.");
        DESC.put(Spell.DARK_SHROUD, "Hides you in darkness: invisible and faster for a while.");
        DESC.put(Spell.ABYSS_DRAIN, "Drains nearby enemies with darkness and gives some of their life to you.");
        DESC.put(Spell.LIGHT_BEAM, "A ray of light along a line. Does double damage to the undead and makes enemies glow.");
        DESC.put(Spell.HEALING, "A warm light heals you, and heals players standing close to you too.");
        DESC.put(Spell.PURIFY, "Washes away harmful effects and gives you night vision.");
        DESC.put(Spell.MAGIC_NULLIFY, "Strips magical effects from enemies around you, pushes them back, and briefly shields you from magic damage.");
        DESC.put(Spell.BLACK_ASTA, "Anti-magic wraps your body: more strength, speed and resistance for a while.");
        DESC.put(Spell.DEMON_DWELLER_SLASH, "A heavy anti-magic slash that cuts through magic effects and surges you forward.");
    }

    public enum Kind { BEAM, AREA, BUFF }

    private static final java.util.Set<Spell> BEAMS = EnumSet.of(Spell.FLAME_BULLET, Spell.CRIMSON_LION_BLAZE,
            Spell.WATER_BALL, Spell.WIND_BLADE, Spell.EARTH_SPIKE, Spell.FROST_LANCE, Spell.THUNDER_BOLT,
            Spell.THUNDER_CHAIN, Spell.BLACK_SLASH, Spell.LIGHT_BEAM, Spell.DEMON_DWELLER_SLASH);
    private static final java.util.Set<Spell> AREAS = EnumSet.of(Spell.FIRE_RING, Spell.SEA_DRAGON_WAVE,
            Spell.WHIRLWIND, Spell.QUAKE, Spell.FROZEN_RING, Spell.ABYSS_DRAIN, Spell.MAGIC_NULLIFY);

    public static Kind kind(Spell s) {
        return BEAMS.contains(s) ? Kind.BEAM : (AREAS.contains(s) ? Kind.AREA : Kind.BUFF);
    }

    private SpellInfo() {
    }

    public static String describe(Spell spell) {
        return DESC.getOrDefault(spell, "");
    }
}
