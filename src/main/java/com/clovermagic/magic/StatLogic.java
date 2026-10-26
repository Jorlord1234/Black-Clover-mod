package com.clovermagic.magic;

import com.clovermagic.ModAttachments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Mage stats (one point per magic level above 1, max 20 per stat):
 * Power +3% spell damage, Mana +5% maximum mana, Control -1.5% mana cost and cooldowns, Vitality +1 heart.
 * Also keeps the permanent health changes (Vitality, Devil Deals) in sync: they are re-applied after death and relogs.
 */
public final class StatLogic {
    public static int maxPerStat() {
        return com.clovermagic.Config.STAT_CAP.get();
    }
    public static final String[] NAMES = {"Power", "Mana", "Control", "Vitality"};
    public static final String[] EFFECTS = {"+3% spell damage per point", "+5% maximum mana per point",
            "-1.5% mana cost and cooldowns per point", "+1 heart of maximum health per point"};

    private StatLogic() {
    }

    public static double powerMult(MagicData d) {
        return 1.0 + 0.03 * d.stats[0] + SquadLogic.bonus(d, SquadLogic.Kind.POWER);
    }

    public static double costMult(MagicData d) {
        return Math.max(0.5, 1.0 - 0.015 * d.stats[2] - SquadLogic.bonus(d, SquadLogic.Kind.COST));
    }

    public static double cooldownMult(MagicData d) {
        return Math.max(0.5, 1.0 - 0.015 * d.stats[2] - SquadLogic.bonus(d, SquadLogic.Kind.COOLDOWN));
    }

    public static void spend(ServerPlayer sp, int index) {
        if (index < 0 || index > 3) return;
        MagicData d = sp.getData(ModAttachments.MAGIC.get());
        if (d.leaves == 0 || d.statPointsLeft() <= 0 || d.stats[index] >= maxPerStat()) return;
        d.stats[index]++;
        apply(sp, d);
        MagicSync.send(sp);
    }

    public static void reset(ServerPlayer sp) {
        MagicData d = sp.getData(ModAttachments.MAGIC.get());
        java.util.Arrays.fill(d.stats, 0);
        apply(sp, d);
        MagicSync.send(sp);
    }

    /** Makes the permanent max-health modifiers match the data (safe to call often). */
    public static void apply(ServerPlayer sp, MagicData d) {
        AttributeInstance health = sp.getAttribute(Attributes.MAX_HEALTH);
        if (health == null) return;
        set(health, "stat_vitality", 2.0 * d.stats[3]);
        set(health, "devil_price_second_grimoire", d.leaves2 > 0 ? -4.0 : 0.0);
        set(health, "devil_price_devil_mark", d.devilMark ? -4.0 : 0.0);
        set(health, "squad_health", SquadLogic.bonus(d, SquadLogic.Kind.HEALTH));
        if (sp.getHealth() > sp.getMaxHealth()) sp.setHealth(sp.getMaxHealth());
    }

    private static void set(AttributeInstance inst, String name, double amount) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("clovermagic", name);
        AttributeModifier cur = inst.getModifier(id);
        if (amount == 0.0) {
            if (cur != null) inst.removeModifier(id);
        } else if (cur == null || cur.amount() != amount) {
            inst.removeModifier(id);
            inst.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    /** Rank titles by magic level. */
    public static String rank(int level) {
        if (level >= 20) return "Captain";
        if (level >= 15) return "Senior Magic Knight";
        if (level >= 10) return "Magic Knight";
        if (level >= 5) return "Junior Mage";
        return "Apprentice";
    }
}
