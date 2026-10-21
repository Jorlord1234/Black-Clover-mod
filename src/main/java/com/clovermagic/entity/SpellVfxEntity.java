package com.clovermagic.entity;

import com.clovermagic.ModEntities;
import com.clovermagic.vfx.Fx;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * A purely visual entity: it does no damage and has no collision. The server spawns one when a spell is cast; every nearby
 * client draws that spell's 3D model for its lifetime (see client/SpellVfxRenderer). It can follow another entity
 * (caster or target); the renderer reads the followed entity's position every frame so the model never lags.
 */
public class SpellVfxEntity extends Entity {
    private static final EntityDataAccessor<Integer> FX_ID = SynchedEntityData.defineId(SpellVfxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FOLLOW = SynchedEntityData.defineId(SpellVfxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(SpellVfxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> LEN = SynchedEntityData.defineId(SpellVfxEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> AUX_LO = SynchedEntityData.defineId(SpellVfxEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AUX_HI = SynchedEntityData.defineId(SpellVfxEntity.class, EntityDataSerializers.INT);

    public SpellVfxEntity(EntityType<? extends SpellVfxEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /** Spawns an effect on the server. yaw/pitch use the player convention (pitch positive = looking down). */
    public static void spawn(ServerLevel level, Fx fx, double x, double y, double z, float yaw, float pitch, float len,
                             int followId, long aux) {
        SpellVfxEntity e = new SpellVfxEntity(ModEntities.SPELL_VFX.get(), level);
        e.moveTo(x, y, z, yaw, pitch);
        e.entityData.set(FX_ID, fx.ordinal());
        e.entityData.set(FOLLOW, followId);
        e.entityData.set(LIFE, fx.life);
        e.entityData.set(LEN, len);
        e.entityData.set(AUX_LO, (int) (aux & 0xFFFFFFFFL));
        e.entityData.set(AUX_HI, (int) (aux >>> 32));
        level.addFreshEntity(e);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FX_ID, -1);
        builder.define(FOLLOW, -1);
        builder.define(LIFE, 0);
        builder.define(LEN, 1.0f);
        builder.define(AUX_LO, 0);
        builder.define(AUX_HI, 0);
    }

    public Fx getFx() {
        return Fx.byId(this.entityData.get(FX_ID));
    }

    public int getFollowId() {
        return this.entityData.get(FOLLOW);
    }

    public int getLife() {
        return this.entityData.get(LIFE);
    }

    public float getLen() {
        return this.entityData.get(LEN);
    }

    public long getAux() {
        return ((long) this.entityData.get(AUX_HI) << 32) | (this.entityData.get(AUX_LO) & 0xFFFFFFFFL);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        if (this.tickCount >= this.getLife()) {
            this.discard();
            return;
        }
        int follow = this.getFollowId();
        if (follow >= 0) {
            Entity target = this.level().getEntity(follow);
            if (target == null || !target.isAlive()) {
                this.discard();
                return;
            }
            this.setPos(target.getX(), target.getY(), target.getZ());
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
