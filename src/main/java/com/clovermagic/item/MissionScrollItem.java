package com.clovermagic.item;

import java.util.List;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** A mission from the Mission Board. The progress is stored in the item and shown in its tooltip. */
public class MissionScrollItem extends Item {
    public MissionScrollItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;
        CompoundTag tag = data.copyTag();
        tooltip.add(Component.literal("Defeat " + tag.getInt("need") + " x " + tag.getString("name")
                + " (" + tag.getInt("have") + "/" + tag.getInt("need") + ")").withStyle(net.minecraft.ChatFormatting.GOLD));
        tooltip.add(Component.literal("Reward: " + tag.getInt("crystals") + " Mana Crystals, " + tag.getInt("potions")
                + " Mana Potions, " + tag.getInt("xp") + " experience").withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}
