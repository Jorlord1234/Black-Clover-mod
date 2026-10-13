package com.clovermagic;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common config (config/clovermagic-common.toml). Everything balance-related lives here. */
public final class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue FOUR_LEAF_ONE_IN;
    public static final ModConfigSpec.IntValue FIVE_LEAF_ONE_IN;
    public static final ModConfigSpec.IntValue BASE_MANA;
    public static final ModConfigSpec.IntValue MANA_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue FOUR_LEAF_MANA_MULTIPLIER;
    public static final ModConfigSpec.IntValue MANA_REGEN_PER_SECOND;
    public static final ModConfigSpec.DoubleValue XP_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue POWER_PER_LEVEL;
    public static final ModConfigSpec.IntValue NULLIFY_WINDOW_TICKS;

    static {
        B.push("ceremony");
        FOUR_LEAF_ONE_IN = B.comment("A Four-Leaf Grimoire is chosen about 1 in this many ceremonies.")
                .defineInRange("fourLeafOneIn", 50, 2, 1_000_000);
        FIVE_LEAF_ONE_IN = B.comment("A Five-Leaf Grimoire is chosen about 1 in this many ceremonies.")
                .defineInRange("fiveLeafOneIn", 1000, 2, 10_000_000);
        B.pop();

        B.push("mana");
        BASE_MANA = B.comment("Mana at magic level 0 (each level adds manaPerLevel).")
                .defineInRange("baseMana", 80, 10, 100_000);
        MANA_PER_LEVEL = B.defineInRange("manaPerLevel", 12, 0, 10_000);
        FOUR_LEAF_MANA_MULTIPLIER = B.comment("Mana multiplier for the Four-Leaf Grimoire.")
                .defineInRange("fourLeafManaMultiplier", 1.5, 1.0, 100.0);
        MANA_REGEN_PER_SECOND = B.comment("Base mana per second (+1 per 3 magic levels).")
                .defineInRange("regenPerSecond", 2, 0, 1000);
        B.pop();

        B.push("progression");
        XP_MULTIPLIER = B.comment("Multiplies the magic XP gained by casting. 0 disables progression.")
                .defineInRange("xpMultiplier", 1.0, 0.0, 100.0);
        POWER_PER_LEVEL = B.comment("Spell power gained per magic level (0.04 = +4% per level).")
                .defineInRange("powerPerLevel", 0.04, 0.0, 10.0);
        B.pop();

        B.push("antiMagic");
        NULLIFY_WINDOW_TICKS = B.comment("How long Magic Nullify protects an Anti-Magic user from magic damage (20 ticks = 1 second).")
                .defineInRange("nullifyWindowTicks", 60, 0, 1200);
        B.pop();
    }

    public static final ModConfigSpec SPEC = B.build();

    private Config() {
    }
}
