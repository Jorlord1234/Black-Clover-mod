package com.clovermagic.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * An Anti-Magic sword summoned by a spell: it cannot be dropped, disappears when its time is over, and every hit strips the
 * target's magical effects. The Demon-Dweller also drains a little life.
 */
public class SummonedSwordItem extends SwordItem {
    private final float drain;

    public SummonedSwordItem(Tier tier, Properties props, float drain) {
        super(tier, props);
        this.drain = drain;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide()) return;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        long expire = data == null ? 0 : data.copyTag().getLong("expire");
        if (expire <= 0 || level.getGameTime() > expire) stack.setCount(0);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return false;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            target.removeAllEffects();
            if (drain > 0) attacker.heal(drain);
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
