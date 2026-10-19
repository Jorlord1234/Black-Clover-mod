package com.clovermagic.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** All skill trees. Fire is first; the other elements follow the same template. */
public final class SkillTrees {
    public static final SkillTree FIRE = buildFire();

    private SkillTrees() {
    }

    /** Null when the element has no tree yet (it then uses the old level-based spell unlocks). */
    public static SkillTree forAttribute(String attributeId) {
        return "fire".equals(attributeId) ? FIRE : null;
    }

    /** Spell enum names the player can cast, in tree order: the root spell first, then unlocked spell nodes. */
    public static List<String> unlockedSpellNames(SkillTree tree, Set<String> unlocked) {
        List<String> out = new ArrayList<>();
        for (SkillNode n : tree.nodes) {
            if (n.spellName() == null) continue;
            if (n.type() == NodeType.ROOT || unlocked.contains(n.id())) out.add(n.spellName());
        }
        return out;
    }

    public static boolean isUnlocked(SkillTree tree, Set<String> unlocked, String id) {
        SkillNode n = tree.get(id);
        return n != null && (n.type() == NodeType.ROOT || unlocked.contains(id));
    }

    /** True if this node could be unlocked right now except for the point cost. */
    public static boolean prerequisitesMet(SkillTree tree, Set<String> unlocked, SkillNode n, int level) {
        if (!n.implemented() || n.type() == NodeType.ROOT) return false;
        if (unlocked.contains(n.id())) return false;
        if (level < n.minLevel()) return false;
        SkillBranch b = tree.branch(n.branch());
        if (b != null && b.locked()) return false;
        for (String r : n.requires()) {
            if (!isUnlocked(tree, unlocked, r)) return false;
        }
        return true;
    }

    private static SkillNode node(String id, String name, NodeType type, String branch, int cost, int level,
                                  List<String> req, String spell, boolean implemented, String desc, String canon, int x, int y) {
        return new SkillNode(id, name, type, branch, cost, level, req, spell, implemented, desc, canon, x, y);
    }

    private static SkillTree buildFire() {
        final String ROOT = "fire_magic";
        List<SkillBranch> branches = List.of(
                new SkillBranch("passive", "PASSIVES", "Fire mastery", false, 0),
                new SkillBranch("explosive", "EXPLOSIVE", "Magna style", false, 90),
                new SkillBranch("spiral", "SPIRAL", "Leopold style", false, 190),
                new SkillBranch("lion", "LION", "Fuegoleon style", false, 290),
                new SkillBranch("spirit", "SPIRIT", "Salamander - unlocks later", true, 390));
        List<SkillNode> n = new ArrayList<>();
        n.add(node(ROOT, "Fire Magic", NodeType.ROOT, "root", 0, 1, List.of(), "FLAME_BULLET", true,
                "The start of every fire mage. Grants the Flame Bullet spell.",
                "Gameplay: original starter spell.", 125, 245));

        // passives (left column)
        n.add(node("burn_damage", "Burn Damage", NodeType.PASSIVE, "passive", 1, 1, List.of(), null, true,
                "Your fire spells deal 10% more damage.", "Gameplay: original.", 40, 140));
        n.add(node("long_burn", "Long Burn", NodeType.PASSIVE, "passive", 1, 1, List.of("burn_damage"), null, true,
                "Targets you ignite burn 50% longer.", "Gameplay: original.", 40, 190));
        n.add(node("fire_affinity", "Fire Affinity", NodeType.PASSIVE, "passive", 2, 4, List.of("long_burn"), null, true,
                "Fire spells deal 25% more damage to ice-natured mobs. Fire is always weaker against targets standing in water.",
                "Anime: fire is strong against ice and weak against water.", 40, 240));
        n.add(node("ember_skin", "Ember Skin", NodeType.PASSIVE, "passive", 3, 10, List.of("fire_affinity"), null, true,
                "While you have at least 20% mana, fire and lava cannot hurt you.", "Gameplay: original.", 40, 290));

        // explosive (Magna)
        n.add(node("exploding_fireball", "Exploding Fireball", NodeType.SPELL, "explosive", 2, 2, List.of(), "EXPLODING_FIREBALL", true,
                "Hurls a fireball that flies until it hits something, then bursts.", "Anime: Magna's Exploding Fireball.", 250, 90));
        n.add(node("blast_radius", "Blast Radius", NodeType.UPGRADE, "explosive", 1, 3, List.of("exploding_fireball"), null, true,
                "The blast of your fireballs is 1.5 blocks wider.", "Gameplay: original.", 335, 90));
        n.add(node("exploding_buckshot", "Exploding Buckshot", NodeType.SPELL, "explosive", 3, 5, List.of("exploding_fireball"), "EXPLODING_BUCKSHOT", true,
                "Fires a fan of five small exploding fireballs.", "Anime: Magna's Exploding Buckshot.", 420, 90));
        n.add(node("timed_scattershot", "Timed Explosion Scattershot", NodeType.SPELL, "explosive", 4, 8, List.of("exploding_buckshot"), null, false,
                "Shots that detonate on a timer, even without a hit.", "Anime: Magna's timed explosive shot.", 505, 90));
        n.add(node("vanishing_fireball", "Vanishing Fireball", NodeType.KEYSTONE, "explosive", 5, 12, List.of("timed_scattershot"), null, false,
                "Keystone of the Explosive branch.", "Anime: the name exists, its details still need research.", 590, 90));

        // spiral (Leopold)
        n.add(node("spiral_flame", "Spiral Flame", NodeType.SPELL, "spiral", 2, 3, List.of(), "SPIRAL_FLAME", true,
                "Sends a spiral of flame forward at mid range, burning everything in it.", "Anime: Leopold's Spiral Flame.", 250, 190));
        n.add(node("spiral_range", "Spiral Range", NodeType.UPGRADE, "spiral", 1, 4, List.of("spiral_flame"), null, true,
                "Spiral Flame reaches 4 blocks further.", "Gameplay: original.", 335, 190));
        n.add(node("profound_spiral", "Profound Spiral Flames", NodeType.SPELL, "spiral", 4, 8, List.of("spiral_range"), null, false,
                "A larger, longer spiral that hits several targets.", "Anime: Leopold's Profound Spiral Flames.", 420, 190));
        n.add(node("multi_circle", "Multi-circle", NodeType.UPGRADE, "spiral", 2, 10, List.of("profound_spiral"), null, false,
                "More circles at once.", "Gameplay: original.", 505, 190));
        n.add(node("crimson_eruption", "Crimson Eruption", NodeType.KEYSTONE, "spiral", 6, 15, List.of("multi_circle"), null, false,
                "A huge rune circle that burns everything inside it.", "Anime: True Fire Magic.", 590, 190));

        // lion (Fuegoleon)
        n.add(node("sol_linea", "Sol Linea", NodeType.SPELL, "lion", 2, 3, List.of(), "SOL_LINEA", true,
                "A small sun fires a beam that pierces everything in its path and leaves targets burning.", "Anime: Fuegoleon Vermillion.", 250, 290));
        n.add(node("leo_rugiens", "Leo Rugiens", NodeType.SPELL, "lion", 3, 6, List.of("sol_linea"), "LEO_RUGIENS", true,
                "A lion's roar of fire in a wide cone in front of you.", "Anime: Fuegoleon's lion spells.", 335, 290));
        n.add(node("leo_palma", "Leo Palma", NodeType.SPELL, "lion", 3, 6, List.of("sol_linea"), "LEO_PALMA", true,
                "Flame paws grab a target and hold it in place for 3 seconds.", "Anime: Leo Palma binds opponents.", 420, 290));
        n.add(node("ignis_columna", "Ignis Columna", NodeType.SPELL, "lion", 4, 9, List.of("leo_palma"), "IGNIS_COLUMNA", true,
                "A pillar of flame surrounds you: stronger defense, and enemies close to you are burned.", "Anime: a defensive fire spell.", 505, 290));
        n.add(node("lion_mastery", "Lion Mastery", NodeType.KEYSTONE, "lion", 5, 14, List.of("ignis_columna"), null, false,
                "Lion spells cool down faster and hit harder.", "Gameplay: original.", 590, 290));

        // spirit (locked until Spirit Magic exists)
        n.add(node("spirit_contract", "Spirit Contract", NodeType.SPELL, "spirit", 4, 12, List.of(), null, false,
                "A contract with the fire spirit Salamander.", "Anime: Salamander is fire's spirit.", 250, 390));
        n.add(node("salamander_breath", "Salamander Breath", NodeType.SPELL, "spirit", 4, 14, List.of("spirit_contract"), null, false,
                "Spirit magic.", "Anime: Salamander's Breath.", 335, 390));
        n.add(node("salamander_claw", "Salamander Claw", NodeType.SPELL, "spirit", 4, 16, List.of("salamander_breath"), null, false,
                "Spirit magic.", "Anime: Salamander's Claw.", 420, 390));
        n.add(node("spirit_power", "Spirit Power", NodeType.UPGRADE, "spirit", 2, 18, List.of("salamander_claw"), null, false,
                "Spirit magic.", "Gameplay: original.", 505, 390));
        n.add(node("spirit_absorption", "Spirit Absorption", NodeType.KEYSTONE, "spirit", 6, 20, List.of("spirit_power"), null, false,
                "Spirit magic.", "Gameplay: original.", 590, 390));
        return new SkillTree("fire", "FIRE MAGIC", ROOT, branches, n);
    }
}
