package com.clovermagic.entity;

import com.clovermagic.ModItems;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A flying magic broom, like the ones the Magic Knights ride. Use the Magic Broom item to summon it and sit on it:
 * steer with the mouse, W to fly forward (up and down by looking up and down), A/D to slide, S to slow down, sneak to get off.
 * Sneak + right-click an empty broom, or hit it, to pick it up again.
 */
public class BroomEntity extends PathfinderMob {
    public BroomEntity(EntityType<? extends BroomEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected boolean canAddPassenger(net.minecraft.world.entity.Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public void travel(Vec3 travelVector) {
        LivingEntity rider = this.getControllingPassenger();
        if (rider != null && this.isVehicle()) {
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(rider.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            float forward = rider.zza;
            float strafe = rider.xxa;
            Vec3 look = rider.getLookAngle();
            double yaw = Math.toRadians(rider.getYRot());
            Vec3 right = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw));
            Vec3 desired = look.scale(forward * (forward > 0 ? 1.1 : 0.4)).add(right.scale(strafe * 0.6));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.85).add(desired.scale(0.15)));
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.fallDistance = 0;
            rider.fallDistance = 0;
        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6));
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive() && !this.isVehicle()) {
            if (!this.level().isClientSide()) pickUp(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        if (!this.level().isClientSide()) player.startRiding(this);
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    private void pickUp(Player player) {
        ItemStack stack = new ItemStack(ModItems.MAGIC_BROOM.get());
        if (!player.getInventory().add(stack)) this.spawnAtLocation(stack);
        this.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && source.getEntity() instanceof Player) {
            this.spawnAtLocation(new ItemStack(ModItems.MAGIC_BROOM.get()));
            this.discard();
            return true;
        }
        return source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
