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

        int max = GrimoireItem.maxMana(data.level(), data.leaves);
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

        // Asta's Devil Union: horns and wings of darkness made of particles while it lasts
        if (sp.level().getGameTime() < data.devilUnionUntil && sp.tickCount % 2 == 0) {
            devilAura(sp);
        }

    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
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
                && data.mana * 5 >= GrimoireItem.maxMana(data.level(), data.leaves)) {
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

    /** Black and red particle horns and wings behind the player (Devil Union). */
    private static void devilAura(ServerPlayer sp) {
        net.minecraft.server.level.ServerLevel level = sp.serverLevel();
        double yaw = Math.toRadians(sp.yBodyRot);
        net.minecraft.world.phys.Vec3 back = new net.minecraft.world.phys.Vec3(Math.sin(yaw), 0, -Math.cos(yaw));
        net.minecraft.world.phys.Vec3 right = new net.minecraft.world.phys.Vec3(-Math.cos(yaw), 0, -Math.sin(yaw));
        net.minecraft.core.particles.DustParticleOptions red =
                new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(0.9f, 0.03f, 0.05f), 1.2f);
        net.minecraft.core.particles.DustParticleOptions black =
                new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(0.05f, 0.02f, 0.06f), 1.6f);
        double bx = sp.getX(), by = sp.getY(), bz = sp.getZ();
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 0; i <= 9; i++) {
                double t = i / 9.0;
                double out = 0.3 + t * 2.3;
                double up = 1.35 + Math.sin(t * Math.PI * 0.85) * 1.0 - t * 0.55;
                double behind = 0.3 + t * 0.6;
                double x = bx + right.x * side * out + back.x * behind;
                double y = by + up;
                double z = bz + right.z * side * out + back.z * behind;
                level.sendParticles(i % 3 == 0 ? red : black, x, y, z, 1, 0.03, 0.03, 0.03, 0);
                if (i >= 3 && i % 2 == 1) { // wing fingers hanging down
                    level.sendParticles(black, x, y - 0.5, z, 1, 0.03, 0.12, 0.03, 0);
                    level.sendParticles(red, x, y - 0.9, z, 1, 0.03, 0.05, 0.03, 0);
                }
            }
            // horns: two curved points on each side of the head
            for (int i = 0; i < 4; i++) {
                double t = i / 3.0;
                double x = bx + right.x * side * (0.2 + t * 0.15);
                double z = bz + right.z * side * (0.2 + t * 0.15);
                level.sendParticles(black, x, by + 1.85 + t * 0.4, z, 1, 0.01, 0.01, 0.01, 0);
            }
        }
        level.sendParticles(red, bx, by + 1.0, bz, 2, 0.35, 0.7, 0.35, 0);
    }
}
