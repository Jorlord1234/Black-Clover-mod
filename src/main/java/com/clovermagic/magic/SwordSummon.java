package com.clovermagic.magic;

import com.clovermagic.item.SummonedSwordItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Anti-Magic spells summon a sword into the main hand. Casting again with the same sword out dismisses it. */
public final class SwordSummon {
    private SwordSummon() {
    }

    public static boolean toggle(ServerPlayer p, Item sword, int ticks) {
        ItemStack held = p.getMainHandItem();
        if (held.is(sword)) {
            held.setCount(0);
            SpellUtil.sound(p, SoundEvents.BEACON_DEACTIVATE, 0.8f, 1.6f);
            return true;
        }
        // only one summoned sword at a time
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() instanceof SummonedSwordItem) inv.setItem(i, ItemStack.EMPTY);
        }
        ItemStack stack = new ItemStack(sword);
        CompoundTag tag = new CompoundTag();
        tag.putLong("expire", p.level().getGameTime() + ticks);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        ItemStack old = p.getMainHandItem();
        if (!old.isEmpty()) {
            ItemStack copy = old.copy();
            if (!inv.add(copy)) p.drop(copy, false);
        }
        p.setItemInHand(InteractionHand.MAIN_HAND, stack);
        p.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, p.getX(), p.getY() + 1.2, p.getZ(), 30, 0.4, 0.5, 0.4, 0.05);
        SpellUtil.sound(p, SoundEvents.WITHER_SHOOT, 0.6f, 1.6f);
        return true;
    }
}
