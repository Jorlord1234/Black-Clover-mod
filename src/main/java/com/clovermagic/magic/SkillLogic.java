package com.clovermagic.magic;

import java.util.ArrayList;
import java.util.List;

import com.clovermagic.ModAttachments;
import com.clovermagic.ModItems;
import com.clovermagic.skill.NodeType;
import com.clovermagic.skill.SkillBranch;
import com.clovermagic.skill.SkillNode;
import com.clovermagic.skill.SkillPoints;
import com.clovermagic.skill.SkillTree;
import com.clovermagic.skill.SkillTrees;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Server-side rules for unlocking and resetting skill tree nodes. Every request from a client is validated here. */
public final class SkillLogic {
    private SkillLogic() {
    }

    /** The player's skill tree, or null (no grimoire, Anti-Magic, or an element without a tree yet). */
    public static SkillTree treeFor(MagicData d) {
        if (d.leaves == 0) return null;
        return SkillTrees.forAttribute(d.attribute);
    }

    /** Spells the player can cast, in slot order. Tree elements use the tree, others the old level unlocks. */
    public static List<Spell> spellsFor(MagicAttribute attr, MagicData d) {
        SkillTree tree = SkillTrees.forAttribute(attr.id);
        List<Spell> out = new ArrayList<>();
        if (tree != null) {
            for (String name : SkillTrees.unlockedSpellNames(tree, d.unlocked)) {
                try {
                    out.add(Spell.valueOf(name));
                } catch (IllegalArgumentException ignored) {
                    // unknown spell name in a tree: skip it instead of crashing
                }
            }
            return out;
        }
        int level = d.level();
        for (Spell s : Spell.forAttribute(attr)) {
            if (s.levelNeeded() <= level) out.add(s);
        }
        return out;
    }

    public static int points(MagicData d, SkillTree tree) {
        return SkillPoints.available(tree, d.unlocked, d.level());
    }

    public static void tryUnlock(ServerPlayer sp, String nodeId) {
        MagicData d = sp.getData(ModAttachments.MAGIC.get());
        SkillTree tree = treeFor(d);
        if (tree == null) return;
        SkillNode node = tree.get(nodeId);
        if (node == null) return;
        if (!node.implemented()) {
            sp.displayClientMessage(Component.literal(node.name() + " is coming in a later version.").withStyle(ChatFormatting.GRAY), true);
            return;
        }
        if (!SkillTrees.prerequisitesMet(tree, d.unlocked, node, d.level())) {
            sp.displayClientMessage(Component.literal("You can't unlock " + node.name() + " yet.").withStyle(ChatFormatting.RED), true);
            return;
        }
        if (points(d, tree) < node.cost()) {
            sp.displayClientMessage(Component.literal("Not enough skill points (" + node.cost() + " needed).").withStyle(ChatFormatting.RED), true);
            return;
        }
        d.unlocked.add(node.id());
        sp.displayClientMessage(Component.literal("Unlocked " + node.name()).withStyle(ChatFormatting.GOLD), true);
        MagicSync.send(sp);
    }

    /** Resets one branch of the tree. Costs one Page of Forgetting (free in creative). */
    public static void tryRespec(ServerPlayer sp, String branchId) {
        MagicData d = sp.getData(ModAttachments.MAGIC.get());
        SkillTree tree = treeFor(d);
        if (tree == null) return;
        SkillBranch branch = tree.branch(branchId);
        if (branch == null) return;

        List<String> toRemove = new ArrayList<>();
        int refund = 0;
        for (SkillNode n : tree.nodesOfBranch(branchId)) {
            if (n.type() != NodeType.ROOT && d.unlocked.contains(n.id())) {
                toRemove.add(n.id());
                refund += n.cost();
            }
        }
        if (toRemove.isEmpty()) {
            sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.nothing_to_reset_in_this_branch_9b57", "Nothing to reset in this branch.").withStyle(ChatFormatting.GRAY), true);
            return;
        }
        if (!sp.getAbilities().instabuild) {
            Inventory inv = sp.getInventory();
            int slot = -1;
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack s = inv.getItem(i);
                if (s.is(ModItems.PAGE_OF_FORGETTING.get())) {
                    slot = i;
                    break;
                }
            }
            if (slot < 0) {
                sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.you_need_a_page_of_forgetting_5eea", "You need a Page of Forgetting.").withStyle(ChatFormatting.RED), true);
                return;
            }
            inv.getItem(slot).shrink(1);
        }
        d.unlocked.removeAll(toRemove);
        sp.sendSystemMessage(Component.literal(branch.name() + " reset: " + refund + " skill points refunded.").withStyle(ChatFormatting.GOLD));
        MagicSync.send(sp);
    }
}
