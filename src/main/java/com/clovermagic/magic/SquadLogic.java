package com.clovermagic.magic;

import java.util.Map;

import com.clovermagic.Config;

/**
 * The nine Magic Knight squads. Join one by holding its robe and sneak + right-clicking Julius Novachrono (magic level 10).
 * Each squad gives one small bonus that grows with your squad rank (rank = 1 + 1 per 6 squad points, max 5, points come from missions).
 */
public final class SquadLogic {
    public enum Kind { POWER, MANA, COOLDOWN, COST, HEALTH, MISSION_XP }

    public record Squad(String id, String name, Kind kind, double amount, String text) {
    }

    public static final Map<String, Squad> SQUADS = new java.util.LinkedHashMap<>();

    private static void add(String id, String name, Kind kind, double amount, String text) {
        SQUADS.put(id, new Squad(id, name, kind, amount, text));
    }

    static {
        add("black_bull", "Black Bull", Kind.POWER, 0.06, "+6% spell damage");
        add("golden_dawn", "Golden Dawn", Kind.MANA, 0.10, "+10% maximum mana");
        add("silver_eagle", "Silver Eagle", Kind.COOLDOWN, 0.06, "-6% cooldowns");
        add("crimson_lion", "Crimson Lion", Kind.HEALTH, 4.0, "+2 hearts");
        add("blue_rose", "Blue Rose", Kind.COST, 0.06, "-6% mana cost");
        add("coral_peacock", "Coral Peacock", Kind.MISSION_XP, 0.20, "+20% mission experience");
        add("green_mantis", "Green Mantis", Kind.MANA, 0.08, "+8% maximum mana");
        add("purple_orca", "Purple Orca", Kind.COST, 0.08, "-8% mana cost");
        add("aqua_deer", "Aqua Deer", Kind.COOLDOWN, 0.05, "-5% cooldowns");
    }

    private SquadLogic() {
    }

    public static int rank(int points) {
        return Math.min(5, 1 + points / 6);
    }

    /** The squad bonus of this kind (already scaled by rank), 0 when you are in no squad or it is another kind. */
    public static double bonus(MagicData d, Kind kind) {
        if (!Config.ENABLE_SQUADS.get() || d.squad == null || d.squad.isEmpty()) return 0.0;
        Squad s = SQUADS.get(d.squad);
        if (s == null || s.kind() != kind) return 0.0;
        return s.amount() * (1.0 + 0.25 * (rank(d.squadPoints) - 1));
    }
}
