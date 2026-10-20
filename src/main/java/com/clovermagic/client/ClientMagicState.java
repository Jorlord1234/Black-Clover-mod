package com.clovermagic.client;

import java.util.List;

import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.Spell;
import com.clovermagic.net.SyncPayload;

/** Client-side copy of the player's magic state (pure data, filled by SyncPayload). */
public final class ClientMagicState {
    public static boolean hasGrimoire;
    public static int leaves;
    public static String attribute = "none";
    public static int mana;
    public static int maxMana = 1;
    public static int maxLevel = 20;
    public static int points;
    public static final java.util.Set<String> unlocked = new java.util.LinkedHashSet<>();
    public static int xp;
    public static final long[] cooldownEndMs = new long[SyncPayload.SLOTS];

    /** Grimoire Mode on/off (toggled with G). */
    public static boolean mode;

    private ClientMagicState() {
    }

    public static void apply(SyncPayload p) {
        hasGrimoire = p.hasGrimoire();
        leaves = p.leaves();
        attribute = p.attribute();
        mana = p.mana();
        maxMana = Math.max(1, p.maxMana());
        maxLevel = Math.max(1, p.maxLevel());
        points = p.points();
        unlocked.clear();
        unlocked.addAll(p.unlocked());
        xp = p.xp();
        long now = System.currentTimeMillis();
        for (int i = 0; i < cooldownEndMs.length && i < p.cooldowns().length; i++) {
            cooldownEndMs[i] = now + p.cooldowns()[i] * 50L;
        }
        if (!hasGrimoire) mode = false;
    }

    public static int level() {
        return Math.min(maxLevel, 1 + (int) Math.floor(Math.sqrt(xp / 25.0)));
    }

    public static MagicAttribute attr() {
        if (leaves == 5) return MagicAttribute.ANTI_MAGIC;
        return MagicAttribute.fromId(attribute);
    }

    /** The skill tree of the current element, or null if it has none yet. */
    public static com.clovermagic.skill.SkillTree tree() {
        if (leaves == 0) return null;
        return com.clovermagic.skill.SkillTrees.forAttribute(attribute);
    }

    /** Spells on the bar: unlocked tree spells, or the old level-unlocked spells for elements without a tree. */
    public static List<Spell> spells() {
        MagicAttribute a = attr();
        if (a == null) return List.of();
        com.clovermagic.skill.SkillTree t = tree();
        java.util.List<Spell> out = new java.util.ArrayList<>();
        if (t != null) {
            for (String name : com.clovermagic.skill.SkillTrees.unlockedSpellNames(t, unlocked)) {
                try { out.add(Spell.valueOf(name)); } catch (IllegalArgumentException ignored) { }
            }
            return out;
        }
        int level = level();
        for (Spell s : Spell.forAttribute(a)) if (s.levelNeeded() <= level) out.add(s);
        return out;
    }

    /** 0..1 fraction of the cooldown still remaining for a spell slot. */
    public static float cooldownFraction(int slot, Spell spell) {
        if (slot < 0 || slot >= cooldownEndMs.length || spell.cooldown <= 0) return 0f;
        long left = cooldownEndMs[slot] - System.currentTimeMillis();
        if (left <= 0) return 0f;
        return Math.min(1f, left / (spell.cooldown * 50f));
    }
}
