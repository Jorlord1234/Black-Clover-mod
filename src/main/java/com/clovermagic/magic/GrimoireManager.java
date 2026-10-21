package com.clovermagic.magic;

import java.util.UUID;

import com.clovermagic.ModAttachments;
import com.clovermagic.ModComponents;
import com.clovermagic.ModItems;
import com.clovermagic.item.GrimoireItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

/** Creating, finding and validating owner-bound grimoires. */
public final class GrimoireManager {
    private GrimoireManager() {
    }

    public static ItemStack create(ServerPlayer sp, MagicData data) {
        Item item = switch (data.leaves) {
            case 4 -> ModItems.FOUR_LEAF_GRIMOIRE.get();
            case 5 -> ModItems.FIVE_LEAF_GRIMOIRE.get();
            default -> ModItems.GRIMOIRE.get();
        };
        ItemStack stack = new ItemStack(item);
        stack.set(ModComponents.GRIMOIRE_DATA.get(),
                new GrimoireData(sp.getUUID(), sp.getGameProfile().getName(), data.attribute, data.generation));
        MagicAttribute attr = MagicAttribute.fromId(data.attribute);
        if (attr != null && data.leaves != 5) {
            // picks the cover color via the item model overrides
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(attr.modelIndex));
        }
        return stack;
    }

    /** Hands the player a fresh grimoire; this invalidates any older copy. */
    public static void issue(ServerPlayer sp, MagicData data) {
        data.generation++;
        ItemStack stack = create(sp, data);
        sp.getInventory().placeItemBackInInventory(stack);
    }

    public static boolean isValid(ItemStack stack, ServerPlayer sp, MagicData data) {
        if (stack.isEmpty() || !(stack.getItem() instanceof GrimoireItem)) return false;
        GrimoireData gd = stack.get(ModComponents.GRIMOIRE_DATA.get());
        if (gd == null) return false;
        UUID owner = gd.owner();
        return owner.equals(sp.getUUID()) && gd.generation() == data.generation;
    }

    /** The player's valid grimoire anywhere in their inventory, or EMPTY. */
    public static ItemStack find(ServerPlayer sp, MagicData data) {
        ItemStack inInventory = findInInventory(sp, data);
        return inInventory.isEmpty() ? CuriosCompat.find(sp, data) : inInventory;
    }

    private static ItemStack findInInventory(ServerPlayer sp, MagicData data) {
        Inventory inv = sp.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (isValid(s, sp, data)) return s;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack find(ServerPlayer sp) {
        return find(sp, sp.getData(ModAttachments.MAGIC.get()));
    }
}
