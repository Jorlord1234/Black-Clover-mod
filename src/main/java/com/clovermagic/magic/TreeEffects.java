package com.clovermagic.magic;

import com.clovermagic.item.GrimoireItem;
import com.clovermagic.skill.NodeType;
import com.clovermagic.skill.SkillNode;
import com.clovermagic.skill.SkillTree;
import com.clovermagic.skill.SkillTrees;

/**
 * Effects shared by the skill trees of every element except Fire (Fire has its own, see FireEffects).
 * Node id conventions: <element>_focus (+10% power), <element>_flow (-10% mana), <element>_haste (-10% cooldown),
 * <element>_skin (element specific, applied in ModEvents). Every implemented UPGRADE node gives +20% power to the
 * spells of its own branch.
 */
public final class TreeEffects {
    private TreeEffects() {
    }

    private static boolean has(MagicData d, String id) {
        return d.unlocked.contains(id);
    }

    public static double powerMult(MagicData d, MagicAttribute attr, Spell spell) {
        double m = 1.0;
        if (has(d, "spirit_contract")) m *= 1.15;
        if (attr == MagicAttribute.FIRE) return m;
        if (has(d, attr.id + "_focus")) m *= 1.10;
        SkillTree tree = SkillTrees.forAttribute(attr.id);
        if (tree == null) return m;
        SkillNode spellNode = null;
        for (SkillNode n : tree.nodes) {
            if (spell.name().equals(n.spellName())) {
                spellNode = n;
                break;
            }
        }
        if (spellNode == null || "root".equals(spellNode.branch())) return m;
        for (SkillNode n : tree.nodesOfBranch(spellNode.branch())) {
            if (n.type() == NodeType.UPGRADE && n.implemented() && has(d, n.id())) m *= 1.20;
        }
        return m;
    }

    public static double costMult(MagicData d, MagicAttribute attr) {
        return has(d, attr.id + "_flow") ? 0.9 : 1.0;
    }

    public static double cooldownMult(MagicData d, MagicAttribute attr) {
        return has(d, attr.id + "_haste") ? 0.9 : 1.0;
    }

    /** True when the element's _skin passive is unlocked and the player still has at least 20% mana. */
    public static boolean skinActive(MagicData d, String element) {
        return d.leaves > 0 && d.leaves != 5 && element.equals(d.attribute) && has(d, element + "_skin")
                && d.mana * 5 >= d.maxMana();
    }
}
