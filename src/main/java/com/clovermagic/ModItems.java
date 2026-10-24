package com.clovermagic;

import java.util.EnumMap;
import java.util.Map;

import com.clovermagic.armor.Squad;
import com.clovermagic.armor.SquadArmorItem;
import com.clovermagic.item.GrimoireItem;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CloverMagic.MODID);

    // Grimoires are never craftable: they only come from the Grimoire Tower altar.
    public static final DeferredItem<Item> GRIMOIRE = ITEMS.registerItem("grimoire",
            props -> new GrimoireItem(props, 3), new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> FOUR_LEAF_GRIMOIRE = ITEMS.registerItem("four_leaf_grimoire",
            props -> new GrimoireItem(props, 4), new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> FIVE_LEAF_GRIMOIRE = ITEMS.registerItem("five_leaf_grimoire",
            props -> new GrimoireItem(props, 5), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    public static final DeferredItem<Item> MANA_CRYSTAL = ITEMS.registerItem("mana_crystal",
            props -> new Item(props), new Item.Properties());

    public static final DeferredItem<Item> GRIMOIRE_SIGIL = ITEMS.registerItem("grimoire_sigil",
            props -> new Item(props), new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> MANA_CRYSTAL_ORE = ITEMS.registerItem("mana_crystal_ore",
            props -> new BlockItem(ModBlocks.MANA_CRYSTAL_ORE.get(), props), new Item.Properties());

    public static final DeferredItem<Item> DEEPSLATE_MANA_CRYSTAL_ORE = ITEMS.registerItem("deepslate_mana_crystal_ore",
            props -> new BlockItem(ModBlocks.DEEPSLATE_MANA_CRYSTAL_ORE.get(), props), new Item.Properties());

    public static final DeferredItem<Item> GRIMOIRE_ALTAR = ITEMS.registerItem("grimoire_altar",
            props -> new BlockItem(ModBlocks.GRIMOIRE_ALTAR.get(), props), new Item.Properties().rarity(Rarity.RARE));

    /** Rare drop of Magic Beasts (Magic Boars, Midnight Sun Mages, demons, devils and bosses drop it). Used to craft the Page of Forgetting. */
    public static final DeferredItem<Item> MAGIC_BEAST_CORE = ITEMS.registerItem("magic_beast_core",
            props -> new Item(props), new Item.Properties().rarity(Rarity.UNCOMMON));

    /** Resets one branch of a skill tree. */
    public static final DeferredItem<Item> PAGE_OF_FORGETTING = ITEMS.registerItem("page_of_forgetting",
            props -> new Item(props), new Item.Properties().rarity(Rarity.RARE).stacksTo(16));

    /** Devil Deals, used on the altar. Each one costs 2 hearts of maximum health, for ever. */
    public static final DeferredItem<Item> DEVIL_CONTRACT_BOOK = ITEMS.registerItem("devil_contract_book",
            props -> new Item(props), new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    public static final DeferredItem<Item> DEVIL_CONTRACT_LEAF = ITEMS.registerItem("devil_contract_leaf",
            props -> new Item(props), new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());

    public static final DeferredItem<Item> MAGIC_BOAR_SPAWN_EGG = ITEMS.registerItem("magic_boar_spawn_egg",
            props -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.MAGIC_BOAR, 0x4A3322, 0xE8DCC0, props),
            new Item.Properties());
    public static final DeferredItem<Item> MIDNIGHT_MAGE_SPAWN_EGG = ITEMS.registerItem("midnight_mage_spawn_egg",
            props -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.MIDNIGHT_MAGE, 0x24202C, 0xE0702A, props),
            new Item.Properties());
    public static final DeferredItem<Item> YULTIM_SPIDER_SPAWN_EGG = ITEMS.registerItem("yultim_spider_spawn_egg",
            props -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.YULTIM_SPIDER, 0x2A1838, 0xC02030, props),
            new Item.Properties());

    public static final DeferredItem<Item> DEMON_SPAWN_EGG = ITEMS.registerItem("demon_spawn_egg",
            props -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.DEMON, 0x5A1010, 0x181010, props),
            new Item.Properties());
    public static final DeferredItem<Item> DEVIL_SPAWN_EGG = ITEMS.registerItem("devil_spawn_egg",
            props -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(ModEntities.DEVIL, 0x701020, 0xE0A020, props),
            new Item.Properties());

    /** A flying broom, like the Magic Knights ride. */
    public static final DeferredItem<Item> MAGIC_BROOM = ITEMS.registerItem("magic_broom",
            props -> new com.clovermagic.item.MagicBroomItem(props), new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> MISSION_BOARD = ITEMS.registerItem("mission_board",
            props -> new BlockItem(ModBlocks.MISSION_BOARD.get(), props), new Item.Properties());
    public static final DeferredItem<Item> MISSION_SCROLL = ITEMS.registerItem("mission_scroll",
            props -> new com.clovermagic.item.MissionScrollItem(props), new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    /** Restores half of your maximum mana. */
    public static final DeferredItem<Item> MANA_POTION = ITEMS.registerItem("mana_potion",
            props -> new com.clovermagic.item.ManaPotionItem(props), new Item.Properties().stacksTo(16));

    /** Resets your grimoire completely: use it on the altar to go through the ceremony again. */
    public static final DeferredItem<Item> PAGE_OF_REBIRTH = ITEMS.registerItem("page_of_rebirth",
            props -> new Item(props), new Item.Properties().rarity(Rarity.EPIC).stacksTo(1));

    /** Anti-Magic swords, only obtainable through the Anti-Magic spells. */
    public static final DeferredItem<Item> DEMON_SLAYER_SWORD = ITEMS.registerItem("demon_slayer_sword",
            props -> new com.clovermagic.item.SummonedSwordItem(net.minecraft.world.item.Tiers.IRON,
                    props.stacksTo(1).fireResistant().rarity(Rarity.EPIC)
                            .attributes(net.minecraft.world.item.SwordItem.createAttributes(net.minecraft.world.item.Tiers.IRON, 6, -2.2f)), 0f),
            new Item.Properties());
    public static final DeferredItem<Item> DEMON_DWELLER_SWORD = ITEMS.registerItem("demon_dweller_sword",
            props -> new com.clovermagic.item.SummonedSwordItem(net.minecraft.world.item.Tiers.IRON,
                    props.stacksTo(1).fireResistant().rarity(Rarity.EPIC)
                            .attributes(net.minecraft.world.item.SwordItem.createAttributes(net.minecraft.world.item.Tiers.IRON, 5, -1.8f)), 2f),
            new Item.Properties());
    public static final DeferredItem<Item> DEMON_DESTROYER_SWORD = ITEMS.registerItem("demon_destroyer_sword",
            props -> new com.clovermagic.item.SummonedSwordItem(net.minecraft.world.item.Tiers.IRON,
                    props.stacksTo(1).fireResistant().rarity(Rarity.EPIC)
                            .attributes(net.minecraft.world.item.SwordItem.createAttributes(net.minecraft.world.item.Tiers.IRON, 9, -2.8f)), 0f),
            new Item.Properties());

    public static final DeferredItem<Item> MANA_CLOTH = ITEMS.registerItem("mana_cloth",
            props -> new Item(props), new Item.Properties());

    /** Netherite-level squad armor (GeckoLib 3D models by the outfit artist): a robe for every squad, plus a hood or bandana for six of them. */
    public static final Map<Squad, DeferredItem<Item>> HOODS = new EnumMap<>(Squad.class);
    public static final Map<Squad, DeferredItem<Item>> ROBES = new EnumMap<>(Squad.class);

    private static DeferredItem<Item> piece(Squad squad, String suffix, ArmorItem.Type type) {
        return ITEMS.registerItem(squad.id + "_" + suffix,
                props -> new SquadArmorItem(ModArmorMaterials.BY_SQUAD.get(squad), type, props, squad),
                new Item.Properties().fireResistant().durability(type.getDurability(37)));
    }

    static {
        for (Squad squad : Squad.values()) {
            ROBES.put(squad, piece(squad, "robe", ArmorItem.Type.CHESTPLATE));
            if (squad.hasHelmet()) {
                HOODS.put(squad, piece(squad, "hood", ArmorItem.Type.HELMET));
            }
        }
    }

    private ModItems() {
    }
}
