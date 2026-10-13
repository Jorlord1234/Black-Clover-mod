package com.clovermagic;

import com.clovermagic.item.GrimoireItem;
import com.clovermagic.magic.GrimoireManager;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.MagicSync;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
        if (data.leaves != 5 || sp.level().getGameTime() >= data.nullifyUntil) return;
        DamageSource src = event.getSource();
        if (src.is(DamageTypes.MAGIC) || src.is(DamageTypes.INDIRECT_MAGIC)
                || src.is(DamageTypes.LIGHTNING_BOLT) || src.is(DamageTypes.FIREBALL)
                || src.is(DamageTypes.UNATTRIBUTED_FIREBALL)) {
            event.setCanceled(true);
        }
    }
}
