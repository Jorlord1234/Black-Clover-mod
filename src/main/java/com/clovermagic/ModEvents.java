package com.clovermagic;

import com.clovermagic.item.GrimoireItem;
import com.clovermagic.magic.GrimoireManager;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.MagicSync;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.clovermagic.magic.TreeEffects;
import com.clovermagic.magic.SpellUtil;
import com.clovermagic.magic.Transform;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ModEvents {
    private ModEvents() {
    }

    /** Mana regeneration and syncing the HUD data to the client. */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer sp)) return;

        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        if (sp.tickCount % 10 == 0) MagicSync.send(sp);
        if (data.leaves == 0) return;

        int max = data.maxMana();
        if (data.mana > max) data.mana = max;
        if (sp.tickCount % 20 == 0 && data.mana < max) {
            data.mana = Math.min(max, data.mana + Config.MANA_REGEN_PER_SECOND.get() + data.level() / 3);
        }

        // element passives that need a tick: Radiance (light) heals slowly, Dark Sight (darkness) gives night vision
        if (sp.tickCount % 40 == 0) {
            if (TreeEffects.skinActive(data, "light") && sp.getHealth() < sp.getMaxHealth()) sp.heal(1.0f);
            if (TreeEffects.skinActive(data, "darkness")) {
                sp.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 320, 0, false, false));
            }
        }

        // flight from Devil Union / Spirit Dive, and Luck's Lightning Battle Fiend zapping nearby monsters
        Transform.updateFlight(sp, data);
        if (sp.level().getGameTime() < data.fiendUntil && sp.tickCount % 8 == 0) {
            for (net.minecraft.world.entity.LivingEntity e : SpellUtil.around(sp, 6)) {
                if (e instanceof net.minecraft.world.entity.monster.Enemy) {
                    e.hurt(SpellUtil.magic(sp), 4.0f);
                    SpellUtil.zap(sp.serverLevel(), sp.getBoundingBox().getCenter(), e.getBoundingBox().getCenter());
                    break;
                }
            }
        }

        // Asta's Devil Union: horns and wings of darkness made of particles while it lasts
        if (sp.level().getGameTime() < data.devilUnionUntil && sp.tickCount % 2 == 0) {
            devilAura(sp);
        }

    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            // flight from a transformation does not survive a relog
            MagicData md = sp.getData(ModAttachments.MAGIC.get());
            if (md.leaves > 0 && !sp.isCreative() && !sp.isSpectator() && sp.getAbilities().mayfly) {
                sp.getAbilities().mayfly = false;
                sp.getAbilities().flying = false;
                sp.onUpdateAbilities();
            }
            MagicSync.send(sp);
        }
    }

    /** A three- or four-leaf grimoire disintegrates when its owner dies (the five-leaf does not). */
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        Inventory inv = sp.getInventory();
        boolean lost = false;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.getItem() instanceof GrimoireItem g && g.leaves() < 5 && GrimoireManager.isValid(s, sp, data)) {
                inv.setItem(i, ItemStack.EMPTY);
                lost = true;
            }
        }
        if (lost) {
            sp.sendSystemMessage(Component.literal(
                    "Your grimoire crumbled to dust. Reclaim it at a Grimoire Tower altar with a Grimoire Sigil.")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * Anti-Magic users are not immune by default. Casting Magic Nullify opens a short window
     * (config: antiMagic.nullifyWindowTicks) in which magic damage is negated.
     */
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        // Ember Skin (fire tree): with at least 20% mana, fire and lava cannot hurt you
        if (data.leaves > 0 && data.leaves != 5 && data.unlocked.contains("ember_skin")
                && event.getSource().is(DamageTypeTags.IS_FIRE)
                && data.mana * 5 >= data.maxMana()) {
            event.setCanceled(true);
            return;
        }
        // element skins (see TreeEffects): water, wind, ice, lightning cancel one damage type; earth takes 15% less
        DamageSource hit = event.getSource();
        if ((hit.is(DamageTypes.DROWN) && TreeEffects.skinActive(data, "water"))
                || (hit.is(DamageTypes.FALL) && TreeEffects.skinActive(data, "wind"))
                || (hit.is(DamageTypes.FREEZE) && TreeEffects.skinActive(data, "ice"))
                || (hit.is(DamageTypes.LIGHTNING_BOLT) && TreeEffects.skinActive(data, "lightning"))) {
            event.setCanceled(true);
            return;
        }
        if (data.leaves == 5 && data.unlocked.contains("anti_magic_skin")
                && (hit.is(DamageTypes.MAGIC) || hit.is(DamageTypes.INDIRECT_MAGIC))) {
            event.setAmount(event.getAmount() * 0.6f);
        }
        if (TreeEffects.skinActive(data, "earth")) {
            event.setAmount(event.getAmount() * 0.85f);
        }
        if (data.leaves != 5 || sp.level().getGameTime() >= data.nullifyUntil) return;
        DamageSource src = event.getSource();
        if (src.is(DamageTypes.MAGIC) || src.is(DamageTypes.INDIRECT_MAGIC)
                || src.is(DamageTypes.LIGHTNING_BOLT) || src.is(DamageTypes.FIREBALL)
                || src.is(DamageTypes.UNATTRIBUTED_FIREBALL)) {
            event.setCanceled(true);
        }
    }

    /** A light red and black mist around the player (Devil Union). The horns and wings are a 3D model, see DevilUnionLayer. */
    private static void devilAura(ServerPlayer sp) {
        net.minecraft.server.level.ServerLevel level = sp.serverLevel();
        net.minecraft.core.particles.DustParticleOptions red =
                new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(0.9f, 0.03f, 0.05f), 1.0f);
        level.sendParticles(red, sp.getX(), sp.getY() + 1.0, sp.getZ(), 2, 0.4, 0.8, 0.4, 0);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SQUID_INK, sp.getX(), sp.getY() + 1.0, sp.getZ(), 1, 0.4, 0.8, 0.4, 0.01);
    }
}
