package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.clovermagic.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Per-player magic state, saved on the player (survives death). */
public class MagicData {
    public static final Codec<MagicData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.optionalFieldOf("attribute", "none").forGetter(d -> d.attribute),
            Codec.INT.optionalFieldOf("leaves", 0).forGetter(d -> d.leaves),
            Codec.INT.optionalFieldOf("mana", 100).forGetter(d -> d.mana),
            Codec.INT.optionalFieldOf("selected", 0).forGetter(d -> d.selected),
            Codec.INT.optionalFieldOf("xp", 0).forGetter(d -> d.xp),
            Codec.INT.optionalFieldOf("generation", 0).forGetter(d -> d.generation),
            Codec.STRING.listOf().optionalFieldOf("unlocked", List.of()).forGetter(d -> new ArrayList<>(d.unlocked))
    ).apply(inst, (a, l, m, s, x, g, u) -> new MagicData(a, l, m, s, x, g, u)));

    /** Attribute id ("none" until the ceremony). */
    public String attribute = "none";
    /** 0 = no grimoire yet, otherwise 3, 4 or 5. */
    public int leaves = 0;
    public int mana = 100;
    public int selected = 0;
    /** Magic experience gained by casting. */
    public int xp = 0;
    /** Grimoire serial: reclaiming bumps it, which invalidates any older copy. */
    public int generation = 0;

    /** Not saved: game tick until which the action bar status is shown. */
    public transient int showUntil = 0;

    /** Skill tree nodes the player has unlocked (ids). */
    public final Set<String> unlocked = new LinkedHashSet<>();

    /** Not saved: anti-farm tracking for magic XP. */
    public transient int recentCasts = 0;
    public transient long lastCastTick = 0;

    /** Not saved: game time until which Magic Nullify protects an Anti-Magic user from magic damage. */
    public transient long nullifyUntil = 0;
    /** Game time until which Asta's Devil Union is active (not saved). */
    public transient long devilUnionUntil = 0;

    /** Not saved: game time at which each spell slot is ready again. */
    public final transient long[] cooldownEnd = new long[8];

    public MagicData() {
    }

    public MagicData(String attribute, int leaves, int mana, int selected, int xp, int generation, List<String> unlocked) {
        this.attribute = attribute;
        this.leaves = leaves;
        this.mana = mana;
        this.selected = selected;
        this.xp = xp;
        this.generation = generation;
        this.unlocked.addAll(unlocked);
    }

    /** Level 1..cap (config, default 20). Level n needs 25 * (n-1)^2 xp in total. */
    public int level() {
        return Math.min(Config.MAX_LEVEL.get(), 1 + (int) Math.floor(Math.sqrt(xp / 25.0)));
    }

    public static int xpForLevel(int level) {
        return 25 * (level - 1) * (level - 1);
    }
}
