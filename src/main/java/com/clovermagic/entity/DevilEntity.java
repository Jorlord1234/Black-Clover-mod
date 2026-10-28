package com.clovermagic.entity;

import com.clovermagic.ModEntities;
import com.clovermagic.ModItems;
import com.clovermagic.magic.QuestHooks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Devils from the underworld, in three kinds: Megicula (curses you from a distance), Lucifugus (throws wither skulls) and
 * Zagred (the devil lord: both, and he calls Midnight Sun Mages when he is hurt). Zagred has a boss bar.
 * Spawn one with: /summon clovermagic:devil ~ ~ ~ {DevilType:"zagred"}   (megicula, lucifugus, zagred)
 */
public class DevilEntity extends Monster implements RangedAttackMob {
    private static final EntityDataAccessor<String> DEVIL_TYPE =
            SynchedEntityData.defineId(DevilEntity.class, EntityDataSerializers.STRING);

    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.translatableWithFallback("msg.clovermagic.zagred_b380", "Zagred"),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6);
    private boolean summoned = false;

    public DevilEntity(EntityType<? extends DevilEntity> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 90.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DEVIL_TYPE, "megicula");
    }

    public String devilType() {
        return this.entityData.get(DEVIL_TYPE);
    }

    public void setDevilType(String type) {
        this.entityData.set(DEVIL_TYPE, type);
        String name = switch (type) {
            case "zagred" -> "Zagred";
            case "lucifugus" -> "Lucifugus";
            default -> "Megicula";
        };
        double hp = switch (type) {
            case "zagred" -> 320.0;
            case "lucifugus" -> 110.0;
            default -> 90.0;
        };
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
        this.setHealth((float) hp);
        this.setCustomName(Component.literal(name));
        this.bossEvent.setName(Component.literal(name));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 50, 18.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        String type = devilType();
        boolean curse = type.equals("megicula") || (type.equals("zagred") && this.tickCount % 100 < 50);
        if (curse) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 1));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            if (this.level() instanceof ServerLevel level) {
                Vec3 from = this.getEyePosition();
                Vec3 to = target.getBoundingBox().getCenter();
                for (double t = 0; t <= 1.0; t += 0.05) {
                    Vec3 pt = from.add(to.subtract(from).scale(t));
                    level.sendParticles(ParticleTypes.SQUID_INK, pt.x, pt.y, pt.z, 1, 0.05, 0.05, 0.05, 0);
                    if (t > 0.9) level.sendParticles(ParticleTypes.SOUL, pt.x, pt.y, pt.z, 2, 0.2, 0.2, 0.2, 0.02);
                }
            }
            this.playSound(SoundEvents.WITHER_AMBIENT, 1.0F, 0.6F);
        } else {
            Vec3 dir = new Vec3(target.getX() - this.getX(), target.getY(0.5) - this.getEyeY(), target.getZ() - this.getZ());
            if (dir.lengthSqr() < 1.0E-4) return;
            WitherSkull skull = new WitherSkull(this.level(), this, dir.normalize().scale(0.2));
            skull.setPos(this.getX(), this.getEyeY() - 0.2, this.getZ());
            this.level().addFreshEntity(skull);
            this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 0.8F);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && devilType().equals("zagred") && !summoned && this.getHealth() < this.getMaxHealth() * 0.5F) {
            summoned = true;
            for (int i = 0; i < 2; i++) {
                var mage = ModEntities.MIDNIGHT_MAGE.get().create(this.level());
                if (mage == null) continue;
                mage.moveTo(this.getX() + (i == 0 ? 3 : -3), this.getY(), this.getZ(), this.getYRot(), 0);
                this.level().addFreshEntity(mage);
            }
            this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.5F, 0.6F);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (devilType().equals("zagred")) this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("DevilType", devilType());
        tag.putBoolean("Summoned", summoned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        float hp = this.getHealth();
        if (tag.contains("DevilType")) {
            setDevilType(tag.getString("DevilType"));
            this.setHealth(hp);
        }
        summoned = tag.getBoolean("Summoned");
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return com.clovermagic.ModSounds.DEVIL_GROWL.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(new ItemStack(ModItems.MAGIC_BEAST_CORE.get(), 2));
        this.spawnAtLocation(new ItemStack(ModItems.MANA_CRYSTAL.get(), 4 + this.random.nextInt(4)));
        if (devilType().equals("zagred")) {
            this.spawnAtLocation(new ItemStack(ModItems.DEVIL_CONTRACT.get(), 2));
            this.spawnAtLocation(new ItemStack(Items.NETHER_STAR));
        } else if (this.random.nextFloat() < 0.3F) {
            this.spawnAtLocation(new ItemStack(ModItems.DEVIL_CONTRACT.get()));
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (source.getEntity() instanceof ServerPlayer sp) {
            QuestHooks.award(sp, "devil_slayer");
            if (devilType().equals("zagred")) QuestHooks.award(sp, "zagred_slayer");
        }
    }
}
