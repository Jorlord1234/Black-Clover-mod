package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.clovermagic.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Per-player magic state, saved on the player (survives death). */
public class MagicData {
    /** Squad data and spell cooldowns, nested so the main codec stays within the 16-field limit. */
    private record Extra(String squad, int squadPoints, Map<String, Long> cooldowns) {
        static final Codec<Extra> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.optionalFieldOf("squad", "").forGetter(Extra::squad),
                Codec.INT.optionalFieldOf("squadPoints", 0).forGetter(Extra::squadPoints),
                Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("cooldowns", Map.of()).forGetter(Extra::cooldowns)
        ).apply(i, Extra::new));
    }

    public static final Codec<MagicData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.optionalFieldOf("attribute", "none").forGetter(d -> d.attribute),
            Codec.INT.optionalFieldOf("leaves", 0).forGetter(d -> d.leaves),
            Codec.INT.optionalFieldOf("mana", 100).forGetter(d -> d.mana),
            Codec.INT.optionalFieldOf("selected", 0).forGetter(d -> d.selected),
            Codec.INT.optionalFieldOf("xp", 0).forGetter(d -> d.xp),
            Codec.INT.optionalFieldOf("generation", 0).forGetter(d -> d.generation),
            Codec.STRING.listOf().optionalFieldOf("unlocked", List.of()).forGetter(d -> new ArrayList<>(d.unlocked)),
            Codec.STRING.optionalFieldOf("attribute2", "none").forGetter(d -> d.attribute2),
            Codec.INT.optionalFieldOf("leaves2", 0).forGetter(d -> d.leaves2),
            Codec.INT.optionalFieldOf("xp2", 0).forGetter(d -> d.xp2),
            Codec.INT.optionalFieldOf("generation2", 0).forGetter(d -> d.generation2),
            Codec.STRING.listOf().optionalFieldOf("unlocked2", List.of()).forGetter(d -> new ArrayList<>(d.unlocked2)),
            Codec.BOOL.optionalFieldOf("devilMark", false).forGetter(d -> d.devilMark),
            Codec.INT.listOf().optionalFieldOf("stats", List.of(0, 0, 0, 0)).forGetter(d -> List.of(d.stats[0], d.stats[1], d.stats[2], d.stats[3])),
            Extra.CODEC.optionalFieldOf("extra", new Extra("", 0, Map.of())).forGetter(d -> new Extra(d.squad, d.squadPoints, new HashMap<>(d.cooldowns)))
    ).apply(inst, (a, l, m, s, x, g, u, a2, l2, x2, g2, u2, dm, st, ex) -> {
        MagicData d = new MagicData(a, l, m, s, x, g, u);
        d.attribute2 = a2;
        d.leaves2 = l2;
        d.xp2 = x2;
        d.generation2 = g2;
        d.unlocked2.addAll(u2);
        d.devilMark = dm;
        for (int i = 0; i < 4 && i < st.size(); i++) d.stats[i] = Math.max(0, st.get(i));
        d.squad = ex.squad();
        d.squadPoints = Math.max(0, ex.squadPoints());
        d.cooldowns.putAll(ex.cooldowns());
        return d;
    }));

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

    /** The second grimoire (Devil Deal), waiting in the background: swap with the active one by sneak + right-click. */
    public String attribute2 = "none";
    public int leaves2 = 0;
    public int xp2 = 0;
    public int generation2 = 0;
    public final Set<String> unlocked2 = new LinkedHashSet<>();
    /** The Magic Knight squad (see SquadLogic) and the squad points earned with missions. */
    public String squad = "";
    public int squadPoints = 0;

    /** Stat points spent: power, mana, control, vitality (one point per magic level above 1). */
    public final int[] stats = new int[4];

    public int statPointsLeft() {
        return Math.max(0, level() - 1 - stats[0] - stats[1] - stats[2] - stats[3]);
    }

    /** Devil Deal "Fifth Leaf": more mana and stronger spells. */
    public boolean devilMark = false;

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
    /** Not saved: flight (Devil Union, Spirit Dive) lasts until this game time; flightGranted = we gave mayfly. */
    public transient long flightUntil = 0;
    public transient boolean flightGranted = false;
    /** Not saved: Luck's Lightning Battle Fiend is active until this game time. */
    public transient long fiendUntil = 0;
    /** Not saved: Crystal Mirror / Deflect: half of the damage is reflected to the attacker until this game time. */
    public transient long mirrorUntil = 0;

    /**
     * Saved: spell name -> game time at which it is ready again. Keyed by spell (not by slot) so unlocking nodes,
     * respeccing and swapping grimoires cannot reset or shift cooldowns, and relogging does not clear them.
     */
    public final Map<String, Long> cooldowns = new HashMap<>();

    public long cooldownEnd(Spell spell) {
        return cooldowns.getOrDefault(spell.name(), 0L);
    }

    public void setCooldown(Spell spell, long endTick) {
        cooldowns.put(spell.name(), endTick);
    }

    /** Drops finished cooldowns so the saved map stays small. */
    public void pruneCooldowns(long now) {
        cooldowns.values().removeIf(end -> end <= now);
    }

    /** Not saved: game time of the last cast attempt (global anti-spam). */
    public transient long lastCastAttempt = 0;

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

    /** Highest mana, including the Devil's Mark bonus. */
    public int maxMana() {
        int m = com.clovermagic.item.GrimoireItem.maxMana(level(), leaves);
        m = (int) (m * (1.0 + 0.05 * stats[1] + SquadLogic.bonus(this, SquadLogic.Kind.MANA)));
        return devilMark ? (int) (m * 1.5) : m;
    }

    /** Makes the second grimoire the active one (and the active one the second). */
    public void swapGrimoires() {
        String a = attribute; attribute = attribute2; attribute2 = a;
        int l = leaves; leaves = leaves2; leaves2 = l;
        int x = xp; xp = xp2; xp2 = x;
        int g = generation; generation = generation2; generation2 = g;
        Set<String> u = new LinkedHashSet<>(unlocked);
        unlocked.clear(); unlocked.addAll(unlocked2);
        unlocked2.clear(); unlocked2.addAll(u);
        selected = 0;
        mana = Math.min(Math.max(mana, 0), maxMana());
    }
}
