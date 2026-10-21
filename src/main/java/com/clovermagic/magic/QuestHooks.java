package com.clovermagic.magic;

import com.clovermagic.CloverMagic;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Quest hooks: gives hidden advancements (data/clovermagic/advancement/*.json, trigger "impossible") that FTB Quests can use
 * as "Advancement" tasks. They are also visible in the vanilla advancement data, so any other quest mod or datapack can use them.
 */
public final class QuestHooks {
    private QuestHooks() {
    }

    public static void award(ServerPlayer sp, String id) {
        if (sp.getServer() == null) return;
        AdvancementHolder adv = sp.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, id));
        if (adv == null) return;
        var progress = sp.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) return;
        for (String criterion : adv.value().criteria().keySet()) {
            sp.getAdvancements().award(adv, criterion);
        }
    }

    /** Awards level_5, level_10, level_15 and level_20 for every threshold the player has reached. */
    public static void awardLevels(ServerPlayer sp, int level) {
        for (int t : new int[] {5, 10, 15, 20}) {
            if (level >= t) award(sp, "level_" + t);
        }
    }
}
