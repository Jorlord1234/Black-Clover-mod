package com.clovermagic.entity;

import com.clovermagic.ModAttachments;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.QuestHooks;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Talkable story characters: Asta, Julius Novachrono (the Wizard King) and Father Orsi. They cannot be hurt and stay
 * where they are placed. Right-click to talk; talking gives quest advancements (for FTB Quests). The look is a
 * placeholder skin on the player model, to be replaced by the model artist.
 * Spawn one with: /summon clovermagic:magic_npc ~ ~ ~ {NpcType:"asta"}   (asta, julius or orsi)
 */
public class MagicNpcEntity extends PathfinderMob {
    private static final EntityDataAccessor<String> NPC_TYPE =
            SynchedEntityData.defineId(MagicNpcEntity.class, EntityDataSerializers.STRING);

    private long lastTalk = -100;

    public MagicNpcEntity(EntityType<? extends MagicNpcEntity> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(NPC_TYPE, "orsi");
    }

    public String npcType() {
        return this.entityData.get(NPC_TYPE);
    }

    public void setNpcType(String type) {
        this.entityData.set(NPC_TYPE, type);
        this.setCustomName(Component.literal(displayName(type)));
        this.setCustomNameVisible(true);
    }

    public static String displayName(String type) {
        return switch (type) {
            case "asta" -> "Asta";
            case "julius" -> "Julius Novachrono";
            default -> "Father Orsi";
        };
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5, 0.002F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(source, amount);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("NpcType", npcType());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("NpcType")) setNpcType(tag.getString("NpcType"));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide() || !(player instanceof ServerPlayer sp) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        long now = this.level().getGameTime();
        if (now - lastTalk < 20) return InteractionResult.CONSUME;
        lastTalk = now;
        talk(sp);
        return InteractionResult.CONSUME;
    }

    private void say(ServerPlayer sp, String text) {
        sp.sendSystemMessage(Component.literal("<" + displayName(npcType()) + "> ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(text).withStyle(ChatFormatting.WHITE)));
    }

    private void talk(ServerPlayer sp) {
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        String type = npcType();
        switch (type) {
            case "asta" -> {
                String[] lines = {
                        "I'm going to be the Wizard King! That's my promise!",
                        "I don't have any magic, so I just train harder than everybody else!",
                        "Giving up is the one thing I'll never do!",
                        "A grimoire chooses you, but it's your guts that make it strong!"};
                say(sp, lines[this.random.nextInt(lines.length)]);
                QuestHooks.award(sp, "talked_asta");
            }
            case "julius" -> {
                if (data.leaves == 0) {
                    say(sp, "You have no grimoire yet. Find a Grimoire Tower, and come back when a grimoire has chosen you.");
                } else if (data.level() >= 15) {
                    say(sp, "Your magic has grown strong. From today you are a Magic Knight of the Clover Kingdom!");
                    QuestHooks.award(sp, "magic_knight");
                } else {
                    say(sp, "Come back when your magic has reached level 15 (you are level " + data.level()
                            + "), and I will make you a Magic Knight.");
                }
                QuestHooks.award(sp, "talked_julius");
            }
            default -> {
                if (data.leaves == 0) {
                    say(sp, "Welcome, child. When a grimoire chooses you, magic will answer. Look for the old towers.");
                } else {
                    String[] lines = {
                            "A grimoire grows together with its owner. Take good care of yours.",
                            "Some say a four-leaf grimoire holds good luck, and a fifth leaf holds something darker.",
                            "Even the smallest hand can hold great power, if the heart is strong."};
                    say(sp, lines[this.random.nextInt(lines.length)]);
                }
                QuestHooks.award(sp, "talked_orsi");
            }
        }
    }
}
