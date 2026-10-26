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
        MagicAttribute attr = d.leaves == 5 ? MagicAttribute.ANTI_MAGIC : MagicAttribute.fromId(d.attribute);
        if (attr != null && d.leaves > 0) {
            java.util.List<Spell> spells = SkillLogic.spellsFor(attr, d);
            for (int i = 0; i < cds.length && i < spells.size(); i++) {
                cds[i] = (int) Math.max(0L, d.cooldownEnd(spells.get(i)) - now);
            }
        }
        com.clovermagic.skill.SkillTree tree = SkillLogic.treeFor(d);
        int points = tree == null ? 0 : SkillLogic.points(d, tree);
        PacketDistributor.sendToPlayer(sp, new com.clovermagic.net.StatsPayload(d.stats[0], d.stats[1], d.stats[2], d.stats[3], d.squad == null ? "" : d.squad, d.squadPoints));
        PacketDistributor.sendToPlayer(sp, new SyncPayload(d.attribute, d.leaves, d.mana,
                d.maxMana(), d.xp, has, cds,
                new java.util.ArrayList<>(d.unlocked), com.clovermagic.Config.MAX_LEVEL.get(), points));
    }
}
