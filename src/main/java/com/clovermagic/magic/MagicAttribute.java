package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;

public enum MagicAttribute {
    // id, name, color, ceremony weight, item model index (custom_model_data)
    FIRE("fire", "Fire", ChatFormatting.RED, 16, 1),
    WATER("water", "Water", ChatFormatting.BLUE, 16, 2),
    WIND("wind", "Wind", ChatFormatting.GREEN, 16, 3),
    EARTH("earth", "Earth", ChatFormatting.GOLD, 16, 4),
    ICE("ice", "Ice", ChatFormatting.AQUA, 12, 5),
    LIGHTNING("lightning", "Lightning", ChatFormatting.YELLOW, 12, 6),
    LIGHT("light", "Light", ChatFormatting.WHITE, 6, 7),
    DARKNESS("darkness", "Darkness", ChatFormatting.DARK_PURPLE, 6, 8),
    /** Only for the Five-Leaf Grimoire (never rolled normally). */
    ANTI_MAGIC("anti_magic", "Anti-Magic", ChatFormatting.DARK_GRAY, 0, 0);

    public final String id;
    public final String displayName;
    public final ChatFormatting color;
    public final int weight;
    public final int modelIndex;

    MagicAttribute(String id, String displayName, ChatFormatting color, int weight, int modelIndex) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.weight = weight;
        this.modelIndex = modelIndex;
    }

    /** Weighted random attribute; Light and Darkness are the rare ones. */
    public static MagicAttribute roll(RandomSource random) {
        int total = 0;
        for (MagicAttribute a : values()) total += a.weight;
        int n = random.nextInt(total);
        for (MagicAttribute a : values()) {
            n -= a.weight;
            if (n < 0) return a;
        }
        return FIRE;
    }

    /** Returns null for "none" / unknown ids. */
    public static MagicAttribute fromId(String id) {
        for (MagicAttribute a : values()) {
            if (a.id.equals(id)) return a;
        }
        return null;
    }

    public static List<String> ids() {
        List<String> out = new ArrayList<>();
        for (MagicAttribute a : values()) out.add(a.id);
        return out;
    }
}
