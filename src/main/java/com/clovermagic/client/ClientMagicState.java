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
        xp = p.xp();
        long now = System.currentTimeMillis();
        for (int i = 0; i < cooldownEndMs.length && i < p.cooldowns().length; i++) {
            cooldownEndMs[i] = now + p.cooldowns()[i] * 50L;
        }
        if (!hasGrimoire) mode = false;
    }

    public static int level() {
        return Math.min(10, 1 + (int) Math.floor(Math.sqrt(xp / 25.0)));
    }

    public static MagicAttribute attr() {
        if (leaves == 5) return MagicAttribute.ANTI_MAGIC;
        return MagicAttribute.fromId(attribute);
    }

    public static List<Spell> spells() {
        MagicAttribute a = attr();
        return a == null ? List.of() : Spell.forAttribute(a);
    }

    /** 0..1 fraction of the cooldown still remaining for a spell slot. */
    public static float cooldownFraction(int slot, Spell spell) {
        if (slot < 0 || slot >= cooldownEndMs.length || spell.cooldown <= 0) return 0f;
        long left = cooldownEndMs[slot] - System.currentTimeMillis();
        if (left <= 0) return 0f;
        return Math.min(1f, left / (spell.cooldown * 50f));
    }
}
