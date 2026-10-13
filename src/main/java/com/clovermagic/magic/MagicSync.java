package com.clovermagic.magic;

import com.clovermagic.ModAttachments;
import com.clovermagic.net.SyncPayload;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/** Sends the player's magic state to their client for the HUD. */
public final class MagicSync {
    private MagicSync() {
    }

    public static void send(ServerPlayer sp) {
        MagicData d = sp.getData(ModAttachments.MAGIC.get());
        boolean has = !GrimoireManager.find(sp, d).isEmpty();
        long now = sp.level().getGameTime();
        int[] cds = new int[SyncPayload.SLOTS];
        for (int i = 0; i < cds.length; i++) {
            cds[i] = (int) Math.max(0L, d.cooldownEnd[i] - now);
        }
        PacketDistributor.sendToPlayer(sp, new SyncPayload(d.attribute, d.leaves, d.mana, com.clovermagic.item.GrimoireItem.maxMana(d.level(), d.leaves), d.xp, has, cds));
    }
}
