package com.clovermagic;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clovermagic.armor.Squad;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** One netherite-level armor material per squad (only the texture differs). */
public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, CloverMagic.MODID);

    public static final Map<Squad, DeferredHolder<ArmorMaterial, ArmorMaterial>> BY_SQUAD = new HashMap<>();

    static {
        for (Squad squad : Squad.values()) {
            BY_SQUAD.put(squad, MATERIALS.register(squad.id, () -> {
                EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
                defense.put(ArmorItem.Type.BOOTS, 3);
                defense.put(ArmorItem.Type.LEGGINGS, 6);
                defense.put(ArmorItem.Type.CHESTPLATE, 8);
                defense.put(ArmorItem.Type.HELMET, 3);
                defense.put(ArmorItem.Type.BODY, 11);
                return new ArmorMaterial(defense, 15, SoundEvents.ARMOR_EQUIP_NETHERITE,
                        () -> Ingredient.of(ModItems.MANA_CLOTH.get()),
                        List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, squad.id))),
                        3.0f, 0.1f);
            }));
        }
    }

    private ModArmorMaterials() {
    }
}
