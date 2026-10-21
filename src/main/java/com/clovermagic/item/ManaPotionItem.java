package com.clovermagic.item;

import com.clovermagic.ModAttachments;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.MagicSync;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Drink it to get half of your maximum mana back. Made from a Magic Beast Core and Mana Crystals. */
public class ManaPotionItem extends Item {
    public ManaPotionItem(Properties props) {
        super(props);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof ServerPlayer sp) {
            MagicData data = sp.getData(ModAttachments.MAGIC.get());
            if (data.leaves > 0) {
                data.mana = Math.min(data.maxMana(), data.mana + data.maxMana() / 2);
                MagicSync.send(sp);
            }
            if (!sp.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (stack.isEmpty()) return bottle;
                sp.getInventory().placeItemBackInInventory(bottle);
            }
        }
        return stack;
    }
}
