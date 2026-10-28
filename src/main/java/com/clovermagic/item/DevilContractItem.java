package com.clovermagic.item;

import com.clovermagic.magic.DevilDeals;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Using a Devil's Contract anywhere offers the two deals (second grimoire / fifth leaf). */
public class DevilContractItem extends Item {
    public DevilContractItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer sp) DevilDeals.offer(sp);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
