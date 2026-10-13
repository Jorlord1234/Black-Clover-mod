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

    public static final DeferredItem<Item> MANA_CLOTH = ITEMS.registerItem("mana_cloth",
            props -> new Item(props), new Item.Properties());

    /** Netherite-level squad armor: hood/circlet, robe, trousers and boots for each squad (GeckoLib 3D models). */
    public static final Map<Squad, DeferredItem<Item>> HOODS = new EnumMap<>(Squad.class);
    public static final Map<Squad, DeferredItem<Item>> ROBES = new EnumMap<>(Squad.class);
    public static final Map<Squad, DeferredItem<Item>> TROUSERS = new EnumMap<>(Squad.class);
    public static final Map<Squad, DeferredItem<Item>> BOOTS = new EnumMap<>(Squad.class);

    private static DeferredItem<Item> piece(Squad squad, String suffix, ArmorItem.Type type) {
        return ITEMS.registerItem(squad.id + "_" + suffix,
                props -> squad.has3dModel()
                        ? new SquadArmorItem(ModArmorMaterials.BY_SQUAD.get(squad), type, props, squad)
                        : new ArmorItem(ModArmorMaterials.BY_SQUAD.get(squad), type, props),
                new Item.Properties().fireResistant().durability(type.getDurability(37)));
    }

    static {
        for (Squad squad : Squad.values()) {
            HOODS.put(squad, piece(squad, "hood", ArmorItem.Type.HELMET));
            ROBES.put(squad, piece(squad, "robe", ArmorItem.Type.CHESTPLATE));
            if (squad.has3dModel()) {
                TROUSERS.put(squad, piece(squad, "trousers", ArmorItem.Type.LEGGINGS));
                BOOTS.put(squad, piece(squad, "boots", ArmorItem.Type.BOOTS));
            }
        }
    }

    private ModItems() {
    }
}
