package com.clovermagic.entity;

import com.clovermagic.ModEntities;
import com.clovermagic.magic.FireEffects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable pattern for projectile spells: spawned on the server, moves in a straight line,
 * collides with blocks and entities (never its owner), detonates once, and despawns after a fixed lifetime.
 * It never damages terrain; the blast is a manual area damage so the caster is never hurt by it.
 */
public class FireballProjectile extends Projectile {
    private static final int MAX_LIFE = 80;     // ticks
    private int life = 0;
    private float damage = 8.0f;
    private double blastRadius = 3.0;

    public FireballProjectile(EntityType<? extends FireballProjectile> type, Level level) {
        super(type, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setBlastRadius(double radius) {
        this.blastRadius = radius;
    }

    /** Creates a fireball at the caster's eye, aimed along their look direction plus a yaw offset (not yet added to the world). */
    public static FireballProjectile create(ServerLevel level, ServerPlayer owner, float damage, double blastRadius,
                                            float velocity, float yawOffset) {
        FireballProjectile fb = new FireballProjectile(ModEntities.FIREBALL.get(), level);
        fb.setOwner(owner);
        fb.setDamage(damage);
        fb.setBlastRadius(blastRadius);
        fb.setPos(owner.getX(), owner.getEyeY() - 0.2, owner.getZ());
        fb.shootFromRotation(owner, owner.getXRot(), owner.getYRot() + yawOffset, 0.0f, velocity, 0.0f);
        return fb;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();

        if (!this.level().isClientSide) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hit.getType() != HitResult.Type.MISS) {
                this.onHit(hit);
                if (this.isRemoved()) return;
            }
            if (++this.life > MAX_LIFE) {
                this.discard();
                return;
            }
        }

        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        this.updateRotation();

        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.3,
                        this.getY() + 0.2 + (this.random.nextDouble() - 0.5) * 0.3,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.3, 0.0, 0.0, 0.0);
            }
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.2, this.getZ(), 0.0, 0.01, 0.0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.detonate(result.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.detonate(null);
    }

    private void detonate(Entity directHit) {
        if (!(this.level() instanceof ServerLevel level)) return;
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().indirectMagic(this, owner);

        ServerPlayer caster = owner instanceof ServerPlayer sp ? sp : null;
        if (directHit != null && directHit != owner) {
            if (directHit instanceof LivingEntity living && caster != null) {
                FireEffects.hurt(caster, living, source, this.damage);
                FireEffects.ignite(caster, living, 100);
            } else {
                directHit.hurt(source, this.damage);
                directHit.setRemainingFireTicks(100);
            }
        }
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(this.blastRadius),
                t -> t != owner && t != directHit && t.isAlive())) {
            double d = target.distanceTo(this);
            if (d <= this.blastRadius) {
                float amount = (float) Math.max(1.0, this.damage * 0.6 * (1.0 - d / (this.blastRadius + 0.5)));
                if (caster != null) {
                    FireEffects.hurt(caster, target, source, amount);
                    FireEffects.ignite(caster, target, 80);
                } else {
                    target.hurt(source, amount);
                    target.setRemainingFireTicks(80);
                }
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
        level.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 60, this.blastRadius * 0.4, this.blastRadius * 0.27, this.blastRadius * 0.4, 0.08);
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 1.2f, 0.6f);
        this.discard();
    }
}
