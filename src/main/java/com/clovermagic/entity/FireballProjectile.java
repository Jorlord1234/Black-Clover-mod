package com.clovermagic.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
    private static final double BLAST_RADIUS = 3.0;

    private int life = 0;
    private float damage = 8.0f;

    public FireballProjectile(EntityType<? extends FireballProjectile> type, Level level) {
        super(type, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
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

        if (directHit != null && directHit != owner) {
            directHit.hurt(source, this.damage);
            directHit.setRemainingFireTicks(100);
        }
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(BLAST_RADIUS),
                t -> t != owner && t != directHit && t.isAlive())) {
            double d = target.distanceTo(this);
            if (d <= BLAST_RADIUS) {
                float amount = (float) (this.damage * 0.6 * (1.0 - d / (BLAST_RADIUS + 0.5)));
                target.hurt(source, Math.max(1.0f, amount));
                target.setRemainingFireTicks(80);
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
        level.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 60, 1.2, 0.8, 1.2, 0.08);
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 1.2f, 0.6f);
        this.discard();
    }
}
