package com.clovermagic.armor;

/** The nine Magic Knight squads: id, display name, dye and trim used in the recipe. */
public enum Squad {
    BLACK_BULL("black_bull", "Black Bull", "minecraft:black_dye", "minecraft:gold_ingot"),
    GOLDEN_DAWN("golden_dawn", "Golden Dawn", "minecraft:yellow_dye", "minecraft:gold_block"),
    SILVER_EAGLE("silver_eagle", "Silver Eagle", "minecraft:light_gray_dye", "minecraft:feather"),
    CRIMSON_LION("crimson_lion", "Crimson Lion", "minecraft:red_dye", "minecraft:blaze_powder"),
    BLUE_ROSE("blue_rose", "Blue Rose", "minecraft:blue_dye", "minecraft:rose_bush"),
    CORAL_PEACOCK("coral_peacock", "Coral Peacock", "minecraft:pink_dye", "minecraft:pink_petals"),
    GREEN_MANTIS("green_mantis", "Green Mantis", "minecraft:green_dye", "minecraft:vine"),
    PURPLE_ORCA("purple_orca", "Purple Orca", "minecraft:purple_dye", "minecraft:amethyst_shard"),
    AQUA_DEER("aqua_deer", "Aqua Deer", "minecraft:cyan_dye", "minecraft:prismarine_crystals");

    public final String id;
    public final String displayName;
    public final String dye;
    public final String trim;

    /** Squads with a helmet piece: Black Bull a bandana, Golden Dawn, Blue Rose, Coral Peacock, Green Mantis and Purple Orca a hood. */
    public boolean hasHelmet() {
        return this != SILVER_EAGLE && this != CRIMSON_LION && this != AQUA_DEER;
    }

    Squad(String id, String displayName, String dye, String trim) {
        this.id = id;
        this.displayName = displayName;
        this.dye = dye;
        this.trim = trim;
    }
}
