package com.clovermagic.item;

import com.clovermagic.ModEntities;
import com.clovermagic.entity.BroomEntity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Right-click: the broom appears under you and you sit on it. */
public class MagicBroomItem extends Item {
    public MagicBroomItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            BroomEntity broom = ModEntities.BROOM.get().create(level);
            if (broom == null) return InteractionResultHolder.fail(stack);
            broom.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0);
            level.addFreshEntity(broom);
            player.startRiding(broom);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
