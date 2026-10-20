package com.clovermagic.magic;

import java.util.List;

import com.clovermagic.Config;
import com.clovermagic.ModAttachments;
import com.clovermagic.item.GrimoireItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Server-side casting: the number keys in Grimoire Mode cast spell slot 0, 1, 2... */
public final class CastHandler {
    private CastHandler() {
    }

    private static boolean hasLivingTargetNearby(ServerPlayer sp) {
        return !sp.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,
                sp.getBoundingBox().inflate(24),
                e -> e != sp && e.isAlive() && !(e instanceof net.minecraft.world.entity.player.Player)).isEmpty();
    }

    public static void handle(ServerPlayer sp, int slot) {
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        ItemStack stack = GrimoireManager.find(sp, data);
        if (stack.isEmpty()) {
            SpellUtil.fail(sp, data.leaves == 0
                    ? "You have no grimoire yet. Visit a Grimoire Tower."
                    : "Your grimoire is lost. Reclaim it at a Grimoire Tower.");
            return;
        }
        GrimoireItem g = (GrimoireItem) stack.getItem();
        int leaves = g.leaves();
        MagicAttribute attr = leaves == 5 ? MagicAttribute.ANTI_MAGIC : MagicAttribute.fromId(data.attribute);
        if (attr == null) return;

        // skill tree elements cast what the player unlocked; other elements still use the old level unlocks
        List<Spell> spells = SkillLogic.spellsFor(attr, data);
        if (slot < 0 || slot >= spells.size() || slot >= data.cooldownEnd.length) return;
        Spell spell = spells.get(slot);
        int level = data.level();

        long now = sp.level().getGameTime();
        if (now < data.cooldownEnd[slot]) return;

        int cost = (int) Math.ceil(spell.cost * GrimoireItem.costMultiplier(leaves) * TreeEffects.costMult(data, attr));
        boolean free = sp.getAbilities().instabuild;
        boolean anti = leaves == 5;
        if (!free) {
            if (anti) {
                if (sp.getFoodData().getFoodLevel() <= 6) {
                    SpellUtil.fail(sp, "Too exhausted");
                    return;
                }
            } else if (data.mana < cost) {
                SpellUtil.fail(sp, "Not enough mana");
                return;
            }
        }

        double power = GrimoireItem.power(leaves) * (1.0 + Config.POWER_PER_LEVEL.get() * (level - 1))
                * TreeEffects.powerMult(data, attr, spell);
        if (!spell.cast(sp, power)) return;

        if (!free) {
            if (anti) {
                sp.causeFoodExhaustion(cost * 0.15f);
            } else {
                data.mana -= cost;
            }
        }
        data.cooldownEnd[slot] = now + Math.max(1, Math.round(spell.cooldown * TreeEffects.cooldownMult(data, attr)));

        // casting grows your magic: more mana, stronger spells, new spells unlock.
        // Anti-farm: attack spells give XP only with a living target nearby (buffs always count),
        // repeated casts in a short time give less, and creative casting gives none.
        int before = data.level();
        if (!free) {
            double xp = cost / 3.0 + 1;
            SpellInfo.Kind kind = SpellInfo.kind(spell);
            if (kind != SpellInfo.Kind.BUFF && !hasLivingTargetNearby(sp)) xp = 0;
            long gap = now - data.lastCastTick;
            data.recentCasts = (int) Math.max(0, data.recentCasts - gap / 100);
            xp *= Math.max(0.15, 1.0 - 0.12 * data.recentCasts);
            data.recentCasts++;
            data.lastCastTick = now;
            data.xp += (int) Math.round(xp * Config.XP_MULTIPLIER.get());
        }
        int after = data.level();
        if (after > before) {
            sp.sendSystemMessage(Component.literal("Your magic grew stronger! Magic level " + after)
                    .withStyle(ChatFormatting.GOLD));
            rollSpirit(sp, data, attr, before, after);
            com.clovermagic.skill.SkillTree tree = SkillLogic.treeFor(data);
            if (tree != null) {
                sp.sendSystemMessage(Component.literal("You have " + SkillLogic.points(data, tree)
                        + " skill points to spend. Press K to open your skill tree.").withStyle(attr.color));
            } else {
                for (Spell s : Spell.forAttribute(attr)) {
                    if (s.levelNeeded() > before && s.levelNeeded() <= after) {
                        sp.sendSystemMessage(Component.literal("New spell unlocked: " + s.displayName)
                                .withStyle(attr.color));
                    }
                }
            }
        }
        MagicSync.send(sp);
    }

    /** From magic level 10, a Four-Leaf fire, water, wind or earth mage may be chosen by their spirit at every level-up. */
    private static void rollSpirit(ServerPlayer sp, MagicData data, MagicAttribute attr, int before, int after) {
        if (data.leaves != 4 || data.unlocked.contains(com.clovermagic.skill.SkillTrees.SPIRIT_BOND)) return;
        if (attr != MagicAttribute.FIRE && attr != MagicAttribute.WATER && attr != MagicAttribute.WIND && attr != MagicAttribute.EARTH) return;
        for (int lvl = before + 1; lvl <= after; lvl++) {
            if (lvl >= 10 && sp.getRandom().nextInt(Config.SPIRIT_BOND_ONE_IN.get()) == 0) {
                bondSpirit(sp, data, attr);
                return;
            }
        }
    }

    public static void bondSpirit(ServerPlayer sp, MagicData data, MagicAttribute attr) {
        data.unlocked.add(com.clovermagic.skill.SkillTrees.SPIRIT_BOND);
        String name = switch (attr) {
            case FIRE -> "Salamander";
            case WATER -> "Undine";
            case WIND -> "Sylph";
            default -> "Gnome";
        };
        sp.sendSystemMessage(Component.literal("The spirit " + name + " has taken a liking to you! The Spirit branch of your skill tree is open.")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        SpellUtil.sound(sp, net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 1.0f, 0.5f);
        SpellUtil.sound(sp, net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_RESONATE, 1.2f, 0.6f);
        sp.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, sp.getX(), sp.getY() + 1, sp.getZ(), 60, 0.6, 1.0, 0.6, 0.1);
    }
}
