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
    public static final ModConfigSpec.DoubleValue SPELL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.IntValue STAT_CAP;
    public static final ModConfigSpec.BooleanValue ALLOW_DEVIL_DEALS;
    public static final ModConfigSpec.BooleanValue ENABLE_SQUADS;
    public static final ModConfigSpec.BooleanValue SPELLS_HURT_PLAYERS;
    public static final ModConfigSpec.BooleanValue SPELLS_PLACE_BLOCKS;
    public static final ModConfigSpec.ConfigValue<java.util.List<? extends String>> DISABLED_DIMENSIONS;
    public static final ModConfigSpec.IntValue MAX_SPELL_ENTITIES_NEAR;
    public static final ModConfigSpec.IntValue NULLIFY_WINDOW_TICKS;
    public static final ModConfigSpec.IntValue MAX_LEVEL;
    public static final ModConfigSpec.IntValue SPIRIT_BOND_ONE_IN;
    public static final ModConfigSpec.BooleanValue SPELL_MODELS;

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
        MAX_LEVEL = B.comment("Highest magic level. Skill points: 1 per level, +1 at levels 5, 10, 15 and +2 at 20.")
                .defineInRange("maxMagicLevel", 20, 5, 50);
        POWER_PER_LEVEL = B.comment("Spell power gained per magic level (0.04 = +4% per level).")
                .defineInRange("powerPerLevel", 0.04, 0.0, 10.0);
        B.pop();

        B.push("modpack");
        SPELL_DAMAGE_MULTIPLIER = B.comment("Multiplies the damage of every spell. Handy to balance the mod against other mods.")
                .defineInRange("spellDamageMultiplier", 1.0, 0.1, 10.0);
        STAT_CAP = B.comment("Maximum points per stat (Power, Mana, Control, Vitality).")
                .defineInRange("statCap", 20, 1, 100);
        ALLOW_DEVIL_DEALS = B.comment("Set to false to switch off the Devil Deals (second grimoire and fifth leaf).")
                .define("allowDevilDeals", true);
        ENABLE_SQUADS = B.comment("Set to false to switch off squads and their bonuses.")
                .define("enableSquads", true);
        SPELLS_HURT_PLAYERS = B.comment("If false, a player's spells cannot damage other players (vanilla PvP rules still apply on top of this).")
                .define("spellsHurtPlayers", true);
        SPELLS_PLACE_BLOCKS = B.comment("If false, walls, cages and other temporary-block spells do nothing to the world.")
                .define("spellsPlaceBlocks", true);
        DISABLED_DIMENSIONS = B.comment("Dimension ids where spells cannot be cast, e.g. [\"minecraft:the_end\", \"mypack:spawn\"].")
                .defineListAllowEmpty("disabledDimensions", java.util.List.of(), () -> "minecraft:the_end", o -> o instanceof String);
        MAX_SPELL_ENTITIES_NEAR = B.comment("Performance: spell model entities are not spawned if this many already exist within 32 blocks of the caster. 0 = no limit.")
                .defineInRange("maxSpellModelsNearby", 40, 0, 10_000);
        B.pop();

        B.push("spirits");
        SPIRIT_BOND_ONE_IN = B.comment("Four-Leaf (or better) fire, water, wind and earth mages from magic level 10: every level-up has about 1 in this chance that the spirit bonds with you.")
                .defineInRange("spiritBondOneIn", 12, 1, 1_000_000);
        B.pop();

        B.push("visuals");
        SPELL_MODELS = B.comment("Spawn the 3D spell models. Turn off if you suspect them of lag or a render bug (particles stay).")
                .define("spellModels", true);
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
