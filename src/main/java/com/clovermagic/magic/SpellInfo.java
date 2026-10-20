package com.clovermagic.magic;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/** Short descriptions shown in the spell list screen (K). */
public final class SpellInfo {
    private static final Map<Spell, String> DESC = new EnumMap<>(Spell.class);

    static {
        DESC.put(Spell.FLAME_BULLET, "Fires a fast bullet of flame at the first target in line. Sets it on fire.");
        DESC.put(Spell.EXPLODING_FIREBALL, "Hurls a fireball that flies until it hits something, then bursts and burns everything close to it.");
        DESC.put(Spell.EXPLODING_BUCKSHOT, "Fires a fan of five small exploding fireballs.");
        DESC.put(Spell.SPIRAL_FLAME, "Sends a spiral of flame forward at mid range, burning everything in it.");
        DESC.put(Spell.SOL_LINEA, "A small sun fires a beam that pierces everything in its path and leaves targets burning.");
        DESC.put(Spell.LEO_RUGIENS, "A lion's roar of fire in a wide cone in front of you.");
        DESC.put(Spell.LEO_PALMA, "Flame paws grab the target in front of you and hold it in place for 3 seconds.");
        DESC.put(Spell.IGNIS_COLUMNA, "A pillar of flame surrounds you: stronger defense, and enemies close to you are burned.");
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
        DESC.put(Spell.SUMMON_DEMON_SLAYER, "Summons the Demon-Slayer Sword into your hand for 5 minutes. Every hit strips magical effects from the target. Cast again to dismiss it.");
        DESC.put(Spell.SUMMON_DEMON_DWELLER, "Summons the Demon-Dweller Sword for 5 minutes: fast, strips magical effects and drains a little life with every hit.");
        DESC.put(Spell.SUMMON_DEMON_DESTROYER, "Summons the huge Demon-Destroyer Sword for 5 minutes: slow, very heavy hits that strip magical effects.");
        DESC.put(Spell.CALIDUS_BRACHIUM, "A huge fist of flame punches everything right in front of you and throws it back.");
        DESC.put(Spell.CALIDUS_BRACHIUM_BARRAGE, "Fists of flame strike every enemy around you from all sides at once.");
        DESC.put(Spell.SPIRIT_DIVE_FIRE, "Dive into the spirit Salamander for 2 minutes: strength, speed, resistance, regeneration, fire immunity and flight.");
        DESC.put(Spell.SPIRIT_DIVE_WATER, "Dive into the spirit Undine for 2 minutes: strength, speed, resistance, regeneration, water breathing and flight.");
        DESC.put(Spell.SPIRIT_DIVE_WIND, "Dive into the spirit Sylph for 2 minutes: strength, speed, resistance, regeneration, soft landings and flight.");
        DESC.put(Spell.SPIRIT_DIVE_EARTH, "Dive into the spirit Gnome for 2 minutes: strength, speed, resistance, regeneration, many extra hearts and flight.");
        DESC.put(Spell.VALKYRIE_DRESS, "Armor of water for one minute: resistance, strength, speed, extra hearts and water breathing.");
        DESC.put(Spell.TRUE_LIGHTNING, "You become a bolt of lightning and shoot forward, hurting everything on the way.");
        DESC.put(Spell.LIGHTNING_BATTLE_FIEND, "Ultimate lightning magic: for one minute you are very fast and strong, and lightning strikes monsters close to you.");
        DESC.put(Spell.AVIDYA_SLASH, "Yami's slash: dark magic flies from your blade in a long line.");
        DESC.put(Spell.DIMENSION_SLASH, "A huge slash that cuts through everything in a very long line and rips away good effects.");
        DESC.put(Spell.BLACK_HOLE, "A small black hole pulls nearby enemies towards it and hurts them.");
        DESC.put(Spell.DEVIL_UNION, "Asta and Liebe become one: huge strength, speed, resistance and extra hearts for 5 minutes, with devil horns and wings of darkness. Magic cannot hurt you for the first 10 seconds. You can fly. Lasts 5 minutes.");
        DESC.put(Spell.SEA_DRAGON_ROAR, "A roaring dragon of water crashes through a long line, hitting hard and throwing everything back.");
        DESC.put(Spell.CLIMBING_COELACANTH, "A rising stream of water lifts every enemy in front of you into the air.");
        DESC.put(Spell.SEA_DRAGON_NEST, "A dome of whirlpools: you and nearby players get resistance, you get extra hearts, and enemies are pushed out.");
        DESC.put(Spell.GALE_WHITE_BOW, "A bow of wind fires a volley of arrows along a very long line.");
        DESC.put(Spell.THIEF_WIND_CLOUD, "A wind cloud lifts you high into the air; you drift down slowly and move faster.");
        DESC.put(Spell.SWIFT_WHITE_HAWK, "A hawk of wind: you dash forward and cut everything you pass through.");
        DESC.put(Spell.BOULDER_TOSS, "Hurls a heavy boulder along a line, hitting hard and knocking enemies back.");
        DESC.put(Spell.STONE_BIND, "Rock grips the target in front of you and almost stops it for 4 seconds.");
        DESC.put(Spell.EARTHEN_SHIELD, "A shell of earth: many extra hearts for you, some for players near you, and a little resistance.");
        DESC.put(Spell.HAILSTORM, "A wide storm of hail freezes everything in a broad line in front of you.");
        DESC.put(Spell.ICE_PRISON, "Locks the target in ice: frozen solid and unable to move for 4 seconds.");
        DESC.put(Spell.FROST_BARRIER, "A barrier of frost: extra hearts for you, and nearby enemies are frozen and pushed away.");
        DESC.put(Spell.STATIC_SHOCK, "A quick shock that stuns the target in front of you for a moment.");
        DESC.put(Spell.SPARK_BARRAGE, "Sparks leap to the five nearest enemies around you at once.");
        DESC.put(Spell.OVERCHARGE, "Lightning charges your body: extra strength and speed for a while.");
        DESC.put(Spell.DARK_PULSE, "A cone of darkness hits everything in front of you, withering and pushing it back.");
        DESC.put(Spell.SHADOW_BIND, "Shadows hold the target in front of you in place and blind it with darkness.");
        DESC.put(Spell.DREAD_MARK, "Marks a target with dread: weakened, withering and glowing for 10 seconds.");
        DESC.put(Spell.PRISM_BURST, "A burst of light in front of you hurts enemies (undead most), blinds them and makes them glow.");
        DESC.put(Spell.LIGHT_SPEED_DASH, "You move at the speed of light and strike everything along the way.");
        DESC.put(Spell.RADIANT_AEGIS, "A shield of light: extra hearts and resistance for you and players near you.");
    }

    public enum Kind { BEAM, AREA, BUFF }

    private static final java.util.Set<Spell> BEAMS = EnumSet.of(Spell.FLAME_BULLET, Spell.EXPLODING_FIREBALL,
            Spell.EXPLODING_BUCKSHOT, Spell.SPIRAL_FLAME, Spell.SOL_LINEA, Spell.LEO_PALMA, Spell.WATER_BALL, Spell.WIND_BLADE, Spell.EARTH_SPIKE, Spell.FROST_LANCE, Spell.THUNDER_BOLT,
            Spell.THUNDER_CHAIN, Spell.BLACK_SLASH, Spell.LIGHT_BEAM,
            Spell.SEA_DRAGON_ROAR, Spell.CLIMBING_COELACANTH, Spell.GALE_WHITE_BOW, Spell.SWIFT_WHITE_HAWK, Spell.BOULDER_TOSS,
            Spell.STONE_BIND, Spell.HAILSTORM, Spell.ICE_PRISON, Spell.STATIC_SHOCK, Spell.SHADOW_BIND, Spell.DREAD_MARK,
            Spell.LIGHT_SPEED_DASH, Spell.CALIDUS_BRACHIUM, Spell.TRUE_LIGHTNING, Spell.AVIDYA_SLASH, Spell.DIMENSION_SLASH);
    private static final java.util.Set<Spell> AREAS = EnumSet.of(Spell.LEO_RUGIENS, Spell.IGNIS_COLUMNA, Spell.SEA_DRAGON_WAVE,
            Spell.WHIRLWIND, Spell.QUAKE, Spell.FROZEN_RING, Spell.ABYSS_DRAIN,
            Spell.FROST_BARRIER, Spell.SPARK_BARRAGE, Spell.DARK_PULSE, Spell.PRISM_BURST, Spell.CALIDUS_BRACHIUM_BARRAGE, Spell.BLACK_HOLE);

    public static Kind kind(Spell s) {
        return BEAMS.contains(s) ? Kind.BEAM : (AREAS.contains(s) ? Kind.AREA : Kind.BUFF);
    }

    private SpellInfo() {
    }

    public static String describe(Spell spell) {
        return DESC.getOrDefault(spell, "");
    }
}
