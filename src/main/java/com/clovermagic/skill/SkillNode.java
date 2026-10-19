package com.clovermagic.skill;

import java.util.List;

/**
 * One node of a skill tree. Pure data (no Minecraft classes) so it can be unit-tested.
 * spellName is the name of the Spell enum constant for SPELL/ROOT/KEYSTONE nodes that grant one (or null).
 * x / y are screen positions in the tree's design space (about 650 x 450).
 */
public record SkillNode(String id, String name, NodeType type, String branch, int cost, int minLevel,
                        List<String> requires, String spellName, boolean implemented,
                        String description, String canon, int x, int y) {
}
