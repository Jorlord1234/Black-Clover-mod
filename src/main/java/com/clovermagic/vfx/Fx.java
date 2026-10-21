package com.clovermagic.vfx;

/**
 * Every spell visual. Constants named like a {@code Spell} are that spell's 3D model; the last two belong to the fireball
 * projectile. Common code (no client classes): the server uses anchor, life and range to place the effect entity,
 * the client uses the ordinal to pick the model recipe.
 */
public enum Fx {
    // fire
    FLAME_BULLET(Anchor.BEAM, 10, 24),
    EXPLODING_FIREBALL(Anchor.BEAM, 10, 3),
    EXPLODING_BUCKSHOT(Anchor.BEAM, 10, 3),
    SPIRAL_FLAME(Anchor.BEAM, 16, 7),
    SOL_LINEA(Anchor.BEAM, 12, 24),
    LEO_RUGIENS(Anchor.CONE, 18, 9),
    LEO_PALMA(Anchor.TARGET_FOLLOW, 24, 14),
    IGNIS_COLUMNA(Anchor.SELF_FOLLOW, 36, 3.5),
    CALIDUS_BRACHIUM(Anchor.BEAM, 12, 6),
    CALIDUS_BRACHIUM_BARRAGE(Anchor.SELF, 20, 8),
    SPIRIT_DIVE_FIRE(Anchor.SELF_FOLLOW, 2400, 1),
    // water
    WATER_BALL(Anchor.BEAM, 12, 14),
    WATER_VEIL(Anchor.SELF_FOLLOW, 200, 1.2),
    SEA_DRAGON_WAVE(Anchor.SELF, 22, 6),
    SEA_DRAGON_ROAR(Anchor.BEAM, 18, 20),
    CLIMBING_COELACANTH(Anchor.BEAM, 16, 12),
    SEA_DRAGON_NEST(Anchor.SELF_FOLLOW, 160, 5),
    VALKYRIE_DRESS(Anchor.SELF_FOLLOW, 1200, 1),
    SPIRIT_DIVE_WATER(Anchor.SELF_FOLLOW, 2400, 1),
    // wind
    WIND_BLADE(Anchor.BEAM, 10, 10),
    WIND_DASH(Anchor.SELF_FOLLOW, 12, 2),
    WHIRLWIND(Anchor.SELF_FOLLOW, 40, 5),
    GALE_WHITE_BOW(Anchor.BEAM, 14, 24),
    THIEF_WIND_CLOUD(Anchor.SELF_FOLLOW, 80, 2),
    SWIFT_WHITE_HAWK(Anchor.BEAM, 14, 10),
    SPIRIT_DIVE_WIND(Anchor.SELF_FOLLOW, 2400, 1),
    // earth
    EARTH_SPIKE(Anchor.GROUND_LINE, 26, 6),
    STONE_ARMOR(Anchor.SELF_FOLLOW, 400, 1),
    QUAKE(Anchor.SELF, 26, 5),
    BOULDER_TOSS(Anchor.BEAM, 16, 16),
    STONE_BIND(Anchor.TARGET_FOLLOW, 80, 12),
    EARTHEN_SHIELD(Anchor.SELF_FOLLOW, 200, 4),
    SPIRIT_DIVE_EARTH(Anchor.SELF_FOLLOW, 2400, 1),
    // ice
    FROST_LANCE(Anchor.BEAM, 14, 14),
    ICE_SHELL(Anchor.SELF_FOLLOW, 300, 1),
    FROZEN_RING(Anchor.SELF, 34, 6),
    HAILSTORM(Anchor.BEAM, 30, 12),
    ICE_PRISON(Anchor.TARGET_FOLLOW, 80, 14),
    FROST_BARRIER(Anchor.SELF_FOLLOW, 160, 4),
    // lightning
    THUNDER_BOLT(Anchor.STRIKE, 20, 40),
    LIGHTNING_SPEED(Anchor.SELF_FOLLOW, 300, 1),
    THUNDER_CHAIN(Anchor.CUSTOM, 8, 0),
    STATIC_SHOCK(Anchor.CUSTOM, 10, 0),
    SPARK_BARRAGE(Anchor.CUSTOM, 8, 0),
    OVERCHARGE(Anchor.SELF_FOLLOW, 300, 1),
    TRUE_LIGHTNING(Anchor.BEAM, 10, 14),
    LIGHTNING_BATTLE_FIEND(Anchor.SELF_FOLLOW, 1200, 1),
    // darkness
    BLACK_SLASH(Anchor.BEAM, 10, 8),
    DARK_SHROUD(Anchor.SELF_FOLLOW, 36, 1),
    ABYSS_DRAIN(Anchor.SELF, 36, 6),
    DARK_PULSE(Anchor.CONE, 22, 8),
    SHADOW_BIND(Anchor.TARGET_FOLLOW, 80, 12),
    DREAD_MARK(Anchor.TARGET_FOLLOW, 200, 16),
    AVIDYA_SLASH(Anchor.BEAM, 12, 14),
    DIMENSION_SLASH(Anchor.BEAM, 16, 30),
    BLACK_HOLE(Anchor.BEAM, 50, 6),
    // light
    LIGHT_BEAM(Anchor.BEAM, 16, 16),
    HEALING(Anchor.SELF_FOLLOW, 40, 4),
    PURIFY(Anchor.SELF_FOLLOW, 36, 1.5),
    PRISM_BURST(Anchor.CONE, 18, 9),
    LIGHT_SPEED_DASH(Anchor.BEAM, 14, 12),
    RADIANT_AEGIS(Anchor.SELF_FOLLOW, 200, 5),
    // anti-magic
    SUMMON_DEMON_SLAYER(Anchor.SELF_FOLLOW, 30, 1),
    SUMMON_DEMON_DWELLER(Anchor.SELF_FOLLOW, 30, 1),
    SUMMON_DEMON_DESTROYER(Anchor.SELF_FOLLOW, 34, 1),
    DEVIL_UNION(Anchor.SELF, 40, 6),
    // fireball projectile (drawn by the projectile renderer / spawned by it)
    FIREBALL_PROJECTILE(Anchor.CUSTOM, 1, 1),
    FIREBALL_BURST(Anchor.CUSTOM, 14, 3);

    /** How the server places the effect entity when a spell is cast. */
    public enum Anchor {
        /** Starts at the caster's hand and runs along the look ray; len = distance to the first block (max range). */
        BEAM,
        /** Like BEAM but len is always the full range (area in front of the caster). */
        CONE,
        /** At the caster's feet, stays where it was cast; len = range. */
        SELF,
        /** At the caster's feet and follows the caster. */
        SELF_FOLLOW,
        /** On the first target in the look line and follows it. */
        TARGET_FOLLOW,
        /** Horizontal line along the ground in front of the caster; ground heights are packed into aux. */
        GROUND_LINE,
        /** At the point a Thunder Bolt hits. */
        STRIKE,
        /** Spawned by other code (projectile, lightning arcs). */
        CUSTOM
    }

    public final Anchor anchor;
    public final int life;
    public final double range;

    Fx(Anchor anchor, int life, double range) {
        this.anchor = anchor;
        this.life = life;
        this.range = range;
    }

    private static final Fx[] ALL = values();

    public static Fx byId(int id) {
        return id >= 0 && id < ALL.length ? ALL[id] : null;
    }
}
