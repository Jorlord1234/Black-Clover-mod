package com.clovermagic.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** All skill trees. Fire is first; the other elements follow the same template. */
public final class SkillTrees {
    public static final SkillTree FIRE = buildFire();
    public static final SkillTree WATER = buildWater();
    public static final SkillTree WIND = buildWind();
    public static final SkillTree EARTH = buildEarth();
    public static final SkillTree ICE = buildIce();
    public static final SkillTree LIGHTNING = buildLightning();
    public static final SkillTree LIGHT = buildLight();
    public static final SkillTree DARKNESS = buildDarkness();
    public static final SkillTree ANTI_MAGIC = buildAntiMagic();

    private SkillTrees() {
    }

    /** Null when the element has no tree yet (it then uses the old level-based spell unlocks). */
    public static SkillTree forAttribute(String attributeId) {
        if (attributeId == null) return null;
        return switch (attributeId) {
            case "fire" -> FIRE;
            case "water" -> WATER;
            case "wind" -> WIND;
            case "earth" -> EARTH;
            case "ice" -> ICE;
            case "lightning" -> LIGHTNING;
            case "light" -> LIGHT;
            case "darkness" -> DARKNESS;
            case "anti_magic" -> ANTI_MAGIC;
            default -> null;
        };
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
        if (b != null && b.locked() && !unlocked.contains(SPIRIT_BOND)) return false;
        for (String r : n.requires()) {
            if (!isUnlocked(tree, unlocked, r)) return false;
        }
        return true;
    }

    /** Pseudo node id saved in the unlocked set once a spirit has bonded with the player (opens the Spirit branch). */
    public static final String SPIRIT_BOND = "spirit_bond";

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
                new SkillBranch("lioness", "LIONESS", "Mereoleona style", false, 390),
                new SkillBranch("spirit", "SPIRIT", "Salamander - needs a spirit bond", true, 490));
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

        // lioness (Mereoleona)
        n.add(node("calidus_brachium", "Calidus Brachium", NodeType.SPELL, "lioness", 2, 4, List.of(), "CALIDUS_BRACHIUM", true,
                "A huge fist of flame punches everything right in front of you.", "Anime: Mereoleona's Calidus Brachium.", 250, 390));
        n.add(node("calidus_barrage", "Calidus Brachium Barrage", NodeType.SPELL, "lioness", 4, 9, List.of("calidus_brachium"), "CALIDUS_BRACHIUM_BARRAGE", true,
                "Fists of flame strike every enemy around you from all sides.", "Anime: Mereoleona's Calidus Brachium Barrage.", 335, 390));
        n.add(node("two_step_reinforcement", "Two-step Reinforcement", NodeType.UPGRADE, "lioness", 3, 11, List.of("calidus_barrage"), null, false,
                "Reinforces both arms: your fists hit much harder.", "Anime: Mereoleona's reinforcement (from the games).", 420, 390));
        n.add(node("mana_zone", "Mana Zone", NodeType.KEYSTONE, "lioness", 6, 15, List.of("two_step_reinforcement"), null, false,
                "Mereoleona's mana zone: you sense and strike everything around you.", "Anime: Mereoleona's Mana Zone.", 505, 390));

        // spirit (opens when Salamander bonds with the player)
        n.add(node("spirit_contract", "Spirit Contract", NodeType.PASSIVE, "spirit", 3, 11, List.of(), null, true,
                "A contract with the fire spirit Salamander: all your spells deal 15% more damage.", "Anime: Salamander is fire's spirit.", 250, 490));
        n.add(node("spirit_dive", "Spirit Dive", NodeType.SPELL, "spirit", 4, 12, List.of("spirit_contract"), "SPIRIT_DIVE_FIRE", true,
                "Fuse with Salamander for 2 minutes: strong buffs, fire immunity and flight.", "Anime: Spirit Dive fuses a mage with their spirit.", 335, 490));
        n.add(node("salamander_breath", "Salamander Breath", NodeType.SPELL, "spirit", 4, 15, List.of("spirit_dive"), null, false,
                "Spirit magic.", "Anime: Salamander's Breath.", 420, 490));
        n.add(node("spirit_power", "Spirit Power", NodeType.UPGRADE, "spirit", 2, 18, List.of("salamander_breath"), null, false,
                "Spirit magic.", "Gameplay: original.", 505, 490));
        n.add(node("spirit_absorption", "Spirit Absorption", NodeType.KEYSTONE, "spirit", 6, 20, List.of("spirit_power"), null, false,
                "Spirit magic.", "Gameplay: original.", 590, 490));
        return new SkillTree("fire", "FIRE MAGIC", ROOT, branches, n);
    }

    // ------------------------------------------------------------------------------------------------
    // Shared template for every element except Fire: Passives column + 3 branches (+ a locked Spirit branch
    // for the elements that have a spirit in the anime). Nodes of a branch form a chain (each needs the one before).
    // Generic passive ids end in _focus (+10% power), _flow (-10% mana cost), _haste (-10% cooldowns),
    // _skin (element specific, see TreeEffects). Every implemented UPGRADE node gives +20% power to the
    // spells of its own branch (see TreeEffects.powerMult).
    // ------------------------------------------------------------------------------------------------
    private record Def(String id, String name, NodeType type, int cost, int level, String spell, boolean impl,
                       String desc, String canon) {
    }

    private static Def sp(String id, String name, int cost, int level, String spell, String desc, String canon) {
        return new Def(id, name, NodeType.SPELL, cost, level, spell, true, desc, canon);
    }

    private static Def up(String id, String name, int level) {
        return new Def(id, name, NodeType.UPGRADE, 1, level, null, true,
                "The spells of this branch deal 20% more damage (and healing).", "Gameplay: original.");
    }

    private static Def ks(String id, String name, int cost, int level, String spell, String desc, String canon) {
        return new Def(id, name, NodeType.KEYSTONE, cost, level, spell, true, desc, canon);
    }

    private static Def soon(String id, String name, NodeType type, int cost, int level, String desc, String canon) {
        return new Def(id, name, type, cost, level, null, false, desc, canon);
    }

    private static Def ps(String id, String name, int cost, int level, String desc) {
        return new Def(id, name, NodeType.PASSIVE, cost, level, null, true, desc, "Gameplay: original.");
    }

    private static void chain(List<SkillNode> out, String branch, int x0, int y0, int dx, int dy, Def... defs) {
        String prev = null;
        for (int i = 0; i < defs.length; i++) {
            Def d = defs[i];
            out.add(new SkillNode(d.id(), d.name(), d.type(), branch, d.cost(), d.level(),
                    prev == null ? List.of() : List.of(prev), d.spell(), d.impl(), d.desc(), d.canon(),
                    x0 + dx * i, y0 + dy * i));
            prev = d.id();
        }
    }

    private static final int LANE_A = 90;
    private static final int LANE_B = 190;
    private static final int LANE_C = 290;
    private static final int LANE_S = 390;

    private static void spirit(List<SkillNode> out, String el, String spiritName, int laneY) {
        String a = "Anime: " + spiritName + " is the spirit of this element.";
        chain(out, "spirit", 250, laneY, 85, 0,
                new Def("spirit_contract", "Spirit Contract", NodeType.PASSIVE, 3, 11, null, true,
                        "A contract with the spirit " + spiritName + ": all your spells deal 15% more damage.", a),
                sp("spirit_dive", "Spirit Dive", 4, 12, "SPIRIT_DIVE_" + el.toUpperCase(),
                        "Fuse with " + spiritName + " for 2 minutes: strong buffs and flight.", "Anime: Spirit Dive fuses a mage with their spirit."),
                soon("spirit_art", "Spirit Art", NodeType.SPELL, 4, 15, "Spirit magic.", a),
                soon("spirit_power", "Spirit Power", NodeType.UPGRADE, 2, 18, "Spirit magic.", "Gameplay: original."),
                soon("spirit_absorption", "Spirit Absorption", NodeType.KEYSTONE, 6, 20, "Spirit magic.", "Gameplay: original."));
    }

    private static SkillTree build(String el, String title, String rootName, String rootSpell, String rootDesc, String rootCanon,
                                   Def[] passives, SkillBranch[] lanes, Def[] a, Def[] b, Def[] c, String spiritName) {
        List<SkillBranch> branches = new ArrayList<>();
        branches.add(new SkillBranch("passive", "PASSIVES", "Mastery", false, 0));
        for (SkillBranch l : lanes) branches.add(l);
        if (spiritName != null) {
            branches.add(new SkillBranch("spirit", "SPIRIT", spiritName + " - needs a spirit bond", true, LANE_S));
        }
        List<SkillNode> n = new ArrayList<>();
        String root = el + "_magic";
        n.add(node(root, rootName, NodeType.ROOT, "root", 0, 1, List.of(), rootSpell, true, rootDesc, rootCanon, 125, 245));
        chain(n, "passive", 40, 140, 0, 50, passives);
        chain(n, lanes[0].id(), 250, LANE_A, 85, 0, a);
        chain(n, lanes[1].id(), 250, LANE_B, 85, 0, b);
        chain(n, lanes[2].id(), 250, LANE_C, 85, 0, c);
        if (spiritName != null) spirit(n, el, spiritName, LANE_S);
        return new SkillTree(el, title, root, branches, n);
    }

    private static Def[] passives(String el, String focus, String flow, String haste, String skin, String skinDesc) {
        return new Def[] {
                ps(el + "_focus", focus, 1, 1, "Your " + el + " spells deal 10% more damage."),
                ps(el + "_flow", flow, 1, 2, "Your spells cost 10% less mana."),
                ps(el + "_haste", haste, 2, 4, "Your spells cool down 10% faster."),
                ps(el + "_skin", skin, 3, 10, skinDesc)};
    }

    private static SkillTree buildWater() {
        return build("water", "WATER MAGIC", "Water Magic", "WATER_BALL",
                "The start of every water mage. Grants the Water Ball spell.", "Anime: Noelle's Sea Dragon's Waterball.",
                passives("water", "Tidal Focus", "Calm Waters", "Swift Current", "Gill Skin",
                        "While you have at least 20% mana you cannot drown."),
                new SkillBranch[] {
                        new SkillBranch("dragon", "SEA DRAGON", "Noelle style", false, LANE_A),
                        new SkillBranch("bastion", "BASTION", "Defense", false, LANE_B),
                        new SkillBranch("tide", "TIDE", "Control", false, LANE_C)},
                new Def[] {
                        sp("sea_dragon_wave", "Sea Dragon's Wave", 2, 2, "SEA_DRAGON_WAVE",
                                "A wave erupts around you and throws every nearby enemy away.", "Anime: Water Magic in the style of Noelle's Sea Dragon spells."),
                        up("dragon_force", "Dragon Force", 3),
                        sp("sea_dragon_roar", "Sea Dragon's Roar", 3, 5, "SEA_DRAGON_ROAR",
                                "A roaring dragon of water crashes through a long line.", "Anime: Noelle's Sea Dragon's Roar."),
                        soon("point_blank_roar", "Point-Blank Sea Dragon's Roar", NodeType.SPELL, 4, 8,
                                "A close-range roar that hits much harder.", "Anime: Noelle's Point-Blank Sea Dragon's Roar."),
                        soon("sea_gods_hammer", "Sea God's Hammer", NodeType.KEYSTONE, 5, 12,
                                "Keystone of the Sea Dragon branch.", "Anime: the name exists, its details still need research.")},
                new Def[] {
                        sp("water_veil", "Water Veil", 2, 2, "WATER_VEIL",
                                "Wraps you in water: resistance, water breathing, no more burning.", "Gameplay: original."),
                        up("bastion_depth", "Deep Calm", 3),
                        sp("sea_dragon_nest", "Sea Dragon's Nest", 3, 5, "SEA_DRAGON_NEST",
                                "A dome of whirlpools protects you and your friends.", "Anime: Noelle's Sea Dragon's Nest."),
                        ks("valkyrie_dress", "Valkyrie Dress", 4, 8, "VALKYRIE_DRESS",
                                "Armor made of water for one minute.", "Anime: Noelle's Valkyrie Dress."),
                        soon("sea_gods_shield", "Sea God's Shield", NodeType.KEYSTONE, 6, 14,
                                "A stronger water shield.", "Anime: a Water Magic defensive spell.")},
                new Def[] {
                        sp("climbing_coelacanth", "Climbing Coelacanth", 2, 3, "CLIMBING_COELACANTH",
                                "A rising stream of water lifts enemies into the air.", "Anime: a Water Magic spell."),
                        up("tide_rising", "Rising Tide", 4),
                        soon("surging_waves", "Surging Waves", NodeType.SPELL, 3, 7,
                                "Waves that roll over a wide area.", "Anime: a Water Magic spell."),
                        soon("aqua_javelin", "Aqua Javelin", NodeType.SPELL, 4, 10,
                                "A lance of water.", "Anime: a Water Magic spell."),
                        soon("sea_serpents_bellow", "Sea Serpent's Bellow", NodeType.KEYSTONE, 5, 15,
                                "Keystone of the Tide branch.", "Anime: the name exists, its details still need research.")},
                "Undine");
    }

    private static SkillTree buildWind() {
        return build("wind", "WIND MAGIC", "Wind Magic", "WIND_BLADE",
                "The start of every wind mage. Grants the Wind Blade spell.", "Anime: based on Yuno's wind blades.",
                passives("wind", "Gale Focus", "Light Breath", "Tailwind", "Featherfall",
                        "While you have at least 20% mana you take no fall damage."),
                new SkillBranch[] {
                        new SkillBranch("storm", "STORM", "Yuno style", false, LANE_A),
                        new SkillBranch("flight", "FLIGHT", "Mobility", false, LANE_B),
                        new SkillBranch("hawk", "HAWK", "Strike", false, LANE_C)},
                new Def[] {
                        sp("whirlwind", "Whirlwind", 2, 2, "WHIRLWIND",
                                "Lifts nearby enemies into the air and lets you fall slowly.", "Gameplay: original, inspired by Towering Tornado."),
                        up("storm_force", "Storm Force", 3),
                        sp("gale_white_bow", "Gale White Bow", 3, 5, "GALE_WHITE_BOW",
                                "A bow of wind fires a volley along a very long line.", "Anime: Yuno's Gale White Bow."),
                        soon("tornado_fang", "Tornado Fang", NodeType.SPELL, 4, 8,
                                "A tornado that bites through enemies.", "Anime: Yuno's Tornado Fang."),
                        soon("spirit_storm", "Spirit Storm", NodeType.KEYSTONE, 6, 14,
                                "Keystone of the Storm branch.", "Anime: Yuno's Spirit Storm.")},
                new Def[] {
                        sp("wind_dash", "Wind Dash", 2, 2, "WIND_DASH",
                                "Rides the wind forward. No fall damage from the dash.", "Gameplay: original."),
                        up("flight_force", "Quick Wings", 3),
                        sp("thief_wind_cloud", "Thief's Wind Cloud", 3, 5, "THIEF_WIND_CLOUD",
                                "A wind cloud lifts you high and lets you drift down fast and light.", "Anime: Thief's Wind Cloud."),
                        soon("heavenly_wind_ark", "Heavenly Wind Ark", NodeType.SPELL, 4, 8,
                                "A ship of wind that carries you.", "Anime: Yuno's Heavenly Wind Ark."),
                        soon("slicing_wind_emperor", "Slicing Wind Emperor", NodeType.KEYSTONE, 5, 12,
                                "Keystone of the Flight branch.", "Anime: Yuno's Slicing Wind Emperor.")},
                new Def[] {
                        sp("swift_white_hawk", "Swift White Hawk", 2, 3, "SWIFT_WHITE_HAWK",
                                "A hawk of wind: dash forward and cut everything on the way.", "Anime: Yuno's Swift White Hawk."),
                        up("hawk_talons", "Keen Talons", 4),
                        soon("piercing_tornado_needle", "Piercing Tornado Needle", NodeType.SPELL, 3, 7,
                                "A needle of wind that pierces armor.", "Anime: Yuno's Piercing Tornado Needle."),
                        soon("wind_blades_shower", "Wind Blades Shower", NodeType.SPELL, 4, 10,
                                "Many wind blades at once.", "Anime: Yuno's Wind Blades Shower."),
                        soon("killing_kakara_blade", "Killing Kakara Blade", NodeType.KEYSTONE, 5, 15,
                                "Keystone of the Hawk branch.", "Anime: Killing Kakara Blade.")},
                "Sylph");
    }

    private static SkillTree buildEarth() {
        return build("earth", "EARTH MAGIC", "Earth Magic", "EARTH_SPIKE",
                "The start of every earth mage. Grants the Earth Spike spell.", "Gameplay: original starter spell.",
                passives("earth", "Bedrock Focus", "Steady Mind", "Shifting Ground", "Stone Skin",
                        "While you have at least 20% mana you take 15% less damage."),
                new SkillBranch[] {
                        new SkillBranch("crush", "CRUSH", "Offense", false, LANE_A),
                        new SkillBranch("bind", "BIND", "Control", false, LANE_B),
                        new SkillBranch("fortress", "FORTRESS", "Defense", false, LANE_C)},
                new Def[] {
                        sp("quake", "Quake", 2, 2, "QUAKE",
                                "Shakes the ground: hurts and slows every enemy around you.", "Gameplay: original."),
                        up("crush_heavy", "Heavy Hands", 3),
                        sp("boulder_toss", "Boulder Toss", 3, 5, "BOULDER_TOSS",
                                "Hurls a heavy boulder along a line.", "Gameplay: original."),
                        soon("avalanche", "Avalanche", NodeType.SPELL, 4, 8, "A wall of rocks rolls over a wide area.", "Gameplay: original."),
                        soon("mountain_breaker", "Mountain Breaker", NodeType.KEYSTONE, 6, 14, "Keystone of the Crush branch.", "Gameplay: original.")},
                new Def[] {
                        sp("stone_bind", "Stone Bind", 2, 3, "STONE_BIND",
                                "Rock grips a target and almost stops it.", "Gameplay: original."),
                        up("bind_grip", "Tight Grip", 4),
                        soon("sinking_ground", "Sinking Ground", NodeType.SPELL, 3, 7, "The ground softens under enemies.", "Gameplay: original."),
                        soon("rock_cage", "Rock Cage", NodeType.SPELL, 4, 10, "Traps a target in a cage of stone.", "Gameplay: original."),
                        soon("earth_prison", "Earth Prison", NodeType.KEYSTONE, 5, 15, "Keystone of the Bind branch.", "Gameplay: original.")},
                new Def[] {
                        sp("stone_armor", "Stone Armor", 2, 2, "STONE_ARMOR",
                                "Strong resistance and extra hearts, but you move slower.", "Gameplay: original."),
                        up("fortress_thick", "Thick Stone", 3),
                        sp("earthen_shield", "Earthen Shield", 3, 5, "EARTHEN_SHIELD",
                                "A shell of earth: many extra hearts for you and some for friends.", "Gameplay: original."),
                        soon("stone_wall", "Stone Wall", NodeType.SPELL, 4, 8, "Raises a wall of stone.", "Gameplay: original."),
                        soon("living_fortress", "Living Fortress", NodeType.KEYSTONE, 6, 14, "Keystone of the Fortress branch.", "Gameplay: original.")},
                "Gnome");
    }

    private static SkillTree buildIce() {
        return build("ice", "ICE MAGIC", "Ice Magic", "FROST_LANCE",
                "The start of every ice mage. Grants the Frost Lance spell.", "Gameplay: original starter spell.",
                passives("ice", "Frost Focus", "Cold Mind", "Quick Freeze", "Winter Skin",
                        "While you have at least 20% mana the cold cannot hurt you."),
                new SkillBranch[] {
                        new SkillBranch("frost", "FROST", "Offense", false, LANE_A),
                        new SkillBranch("prison", "PRISON", "Control", false, LANE_B),
                        new SkillBranch("bulwark", "BULWARK", "Defense", false, LANE_C)},
                new Def[] {
                        sp("frozen_ring", "Frozen Ring", 2, 2, "FROZEN_RING",
                                "Freezes everything around you in a wide ring.", "Gameplay: original."),
                        up("frost_bite", "Biting Cold", 3),
                        sp("hailstorm", "Hailstorm", 3, 5, "HAILSTORM",
                                "A wide storm of hail freezes everything in front of you.", "Gameplay: original."),
                        soon("ice_spears", "Ice Spears", NodeType.SPELL, 4, 8, "A volley of ice spears.", "Gameplay: original."),
                        soon("absolute_zero", "Absolute Zero", NodeType.KEYSTONE, 6, 14, "Keystone of the Frost branch.", "Gameplay: original.")},
                new Def[] {
                        sp("ice_prison", "Ice Prison", 2, 3, "ICE_PRISON",
                                "Locks a target in ice.", "Gameplay: original."),
                        up("prison_deep", "Deep Freeze", 4),
                        soon("frozen_chains", "Frozen Chains", NodeType.SPELL, 3, 7, "Chains of ice bind several enemies.", "Gameplay: original."),
                        soon("glacier_wall", "Glacier Wall", NodeType.SPELL, 4, 10, "Raises a wall of ice.", "Gameplay: original."),
                        soon("frozen_world", "Frozen World", NodeType.KEYSTONE, 5, 15, "Keystone of the Prison branch.", "Gameplay: original.")},
                new Def[] {
                        sp("ice_shell", "Ice Shell", 2, 2, "ICE_SHELL",
                                "Resistance, extra hearts and fire immunity for a while.", "Gameplay: original."),
                        up("bulwark_thick", "Thick Ice", 3),
                        sp("frost_barrier", "Frost Barrier", 3, 5, "FROST_BARRIER",
                                "Extra hearts for you; nearby enemies are frozen and pushed away.", "Gameplay: original."),
                        soon("crystal_mirror", "Crystal Mirror", NodeType.SPELL, 4, 8, "Ice that turns attacks aside.", "Gameplay: original."),
                        soon("eternal_winter", "Eternal Winter", NodeType.KEYSTONE, 6, 14, "Keystone of the Bulwark branch.", "Gameplay: original.")},
                null);
    }

    private static SkillTree buildLightning() {
        return build("lightning", "LIGHTNING MAGIC", "Lightning Magic", "THUNDER_BOLT",
                "The start of every lightning mage. Grants the Thunder Bolt spell.", "Gameplay: original starter spell.",
                passives("lightning", "Storm Focus", "Fast Circuits", "Static Flow", "Static Body",
                        "While you have at least 20% mana lightning cannot hurt you."),
                new SkillBranch[] {
                        new SkillBranch("storm", "STORM", "Offense", false, LANE_A),
                        new SkillBranch("shock", "SHOCK", "Control", false, LANE_B),
                        new SkillBranch("charge", "CHARGE", "Speed", false, LANE_C)},
                new Def[] {
                        sp("thunder_chain", "Thunder Chain", 2, 2, "THUNDER_CHAIN",
                                "Lightning leaps from your target to more enemies close to it.", "Gameplay: original."),
                        up("storm_surge", "Storm Surge", 3),
                        sp("spark_barrage", "Spark Barrage", 3, 5, "SPARK_BARRAGE",
                                "Sparks leap to the five nearest enemies.", "Gameplay: original."),
                        soon("thunder_rain", "Thunder Rain", NodeType.SPELL, 4, 8, "Bolts rain down over an area.", "Gameplay: original."),
                        soon("judgement_bolt", "Judgement Bolt", NodeType.KEYSTONE, 6, 14, "Keystone of the Storm branch.", "Gameplay: original.")},
                new Def[] {
                        sp("static_shock", "Static Shock", 2, 3, "STATIC_SHOCK",
                                "A quick shock that stuns a target.", "Gameplay: original."),
                        up("shock_voltage", "High Voltage", 4),
                        soon("paralyzing_field", "Paralyzing Field", NodeType.SPELL, 3, 7, "A field that stuns everything inside.", "Gameplay: original."),
                        soon("arc_cage", "Arc Cage", NodeType.SPELL, 4, 10, "A cage of electric arcs.", "Gameplay: original."),
                        soon("thunder_prison", "Thunder Prison", NodeType.KEYSTONE, 5, 15, "Keystone of the Shock branch.", "Gameplay: original.")},
                new Def[] {
                        sp("lightning_speed", "Lightning Speed", 2, 2, "LIGHTNING_SPEED",
                                "Great speed and jump for a while.", "Gameplay: original."),
                        up("charge_current", "Strong Current", 3),
                        sp("overcharge", "Overcharge", 3, 5, "OVERCHARGE",
                                "Extra strength and speed for a while.", "Gameplay: original."),
                        sp("true_lightning", "True Lightning Magic", 4, 8, "TRUE_LIGHTNING",
                                "Become a bolt of lightning and shoot forward.", "Anime: Luck's True Lightning Magic."),
                        ks("lightning_battle_fiend", "Lightning Battle Fiend", 6, 14, "LIGHTNING_BATTLE_FIEND",
                                "Ultimate lightning magic: speed, strength and lightning around you for one minute.",
                                "Anime: Ultimate Lightning Magic: Lightning Battle Fiend.")},
                null);
    }

    private static SkillTree buildLight() {
        return build("light", "LIGHT MAGIC", "Light Magic", "LIGHT_BEAM",
                "The start of every light mage. Grants the Holy Ray spell.", "Anime: Light Magic is a rare attribute.",
                passives("light", "Dawn Focus", "Clear Mind", "Quick Glow", "Radiance",
                        "While you have at least 20% mana you slowly regain health."),
                new SkillBranch[] {
                        new SkillBranch("radiance", "RADIANCE", "Offense", false, LANE_A),
                        new SkillBranch("sanctuary", "SANCTUARY", "Support", false, LANE_B),
                        new SkillBranch("aegis", "AEGIS", "Defense", false, LANE_C)},
                new Def[] {
                        sp("prism_burst", "Prism Burst", 2, 2, "PRISM_BURST",
                                "A burst of light in front of you hurts, blinds and reveals enemies.", "Gameplay: original."),
                        up("radiance_bright", "Brighter Light", 3),
                        sp("light_speed_dash", "Light Speed Dash", 3, 5, "LIGHT_SPEED_DASH",
                                "Move at the speed of light and strike everything on the way.", "Anime: Light Magic users move and attack at light speed."),
                        soon("light_spear", "Light Spear", NodeType.SPELL, 4, 8, "A spear of light.", "Gameplay: original."),
                        soon("sunburst", "Sunburst", NodeType.KEYSTONE, 6, 14, "Keystone of the Radiance branch.", "Gameplay: original.")},
                new Def[] {
                        sp("healing", "Healing Light", 2, 2, "HEALING",
                                "A warm light heals you and players close to you.", "Gameplay: original."),
                        up("sanctuary_warm", "Warm Glow", 3),
                        sp("purify", "Purify", 3, 5, "PURIFY",
                                "Washes away harmful effects and gives night vision.", "Gameplay: original."),
                        soon("blessing", "Blessing", NodeType.SPELL, 4, 8, "A lasting blessing for you and your friends.", "Gameplay: original."),
                        soon("revival_light", "Revival Light", NodeType.KEYSTONE, 6, 14, "Keystone of the Sanctuary branch.", "Gameplay: original.")},
                new Def[] {
                        sp("radiant_aegis", "Radiant Aegis", 2, 3, "RADIANT_AEGIS",
                                "A shield of light for you and your friends.", "Gameplay: original."),
                        up("aegis_strong", "Strong Light", 4),
                        soon("light_barrier", "Light Barrier", NodeType.SPELL, 3, 7, "A wall of light.", "Gameplay: original."),
                        soon("halo", "Halo", NodeType.SPELL, 4, 10, "A halo that guards you.", "Gameplay: original."),
                        soon("divine_shield", "Divine Shield", NodeType.KEYSTONE, 5, 15, "Keystone of the Aegis branch.", "Gameplay: original.")},
                null);
    }

    private static SkillTree buildDarkness() {
        return build("darkness", "DARKNESS MAGIC", "Darkness Magic", "BLACK_SLASH",
                "The start of every dark mage. Grants the Black Slash spell.", "Gameplay: original starter spell.",
                passives("darkness", "Dusk Focus", "Quiet Mind", "Creeping Dark", "Dark Sight",
                        "While you have at least 20% mana you see in the dark."),
                new SkillBranch[] {
                        new SkillBranch("void", "VOID", "Offense", false, LANE_A),
                        new SkillBranch("shadow", "SHADOW", "Stealth and control", false, LANE_B),
                        new SkillBranch("blade", "BLADE", "Yami style", false, LANE_C)},
                new Def[] {
                        sp("abyss_drain", "Abyss Drain", 2, 2, "ABYSS_DRAIN",
                                "Drains nearby enemies and gives some of their life to you.", "Gameplay: original."),
                        up("void_deeper", "Deeper Void", 3),
                        sp("dark_pulse", "Dark Pulse", 3, 5, "DARK_PULSE",
                                "A cone of darkness hits everything in front of you.", "Gameplay: original."),
                        sp("black_hole", "Black Hole", 4, 8, "BLACK_HOLE",
                                "A small black hole pulls enemies in and hurts them.", "Anime: Yami's Black Hole."),
                        soon("black_moon", "Black Moon", NodeType.KEYSTONE, 6, 14,
                                "A black moon that erases magic around it.", "Anime: Yami's Black Moon.")},
                new Def[] {
                        sp("dark_shroud", "Dark Shroud", 2, 2, "DARK_SHROUD",
                                "Hides you in darkness: invisible and faster.", "Gameplay: original."),
                        up("shadow_deep", "Deep Shadow", 3),
                        sp("shadow_bind", "Shadow Bind", 3, 5, "SHADOW_BIND",
                                "Shadows hold a target in place and blind it.", "Anime: inspired by Yami's Dark Binding."),
                        soon("shadow_step", "Shadow Step", NodeType.SPELL, 4, 8, "Step from shadow to shadow.", "Gameplay: original."),
                        soon("night_veil", "Night Veil", NodeType.KEYSTONE, 5, 12, "Keystone of the Shadow branch.", "Gameplay: original.")},
                new Def[] {
                        sp("avidya_slash", "Dark Cloaked Avidya Slash", 2, 3, "AVIDYA_SLASH",
                                "Dark magic flies from your blade in a long line.", "Anime: Yami's Dark Cloaked Avidya Slash."),
                        up("blade_keen", "Keen Edge", 4),
                        sp("dimension_slash", "Dark Cloaked Dimension Slash", 4, 7, "DIMENSION_SLASH",
                                "A huge slash through everything in a very long line.", "Anime: Yami's Dark Cloaked Dimension Slash."),
                        soon("iai_slash", "Dark Cloaked Iai Slash", NodeType.SPELL, 4, 10, "A lightning-fast draw slash.", "Anime: Yami's Dark Cloaked Iai Slash."),
                        soon("death_thrust", "Death Thrust", NodeType.KEYSTONE, 6, 15, "A thrust that destroys what it hits.", "Anime: Yami's Death Thrust.")},
                null);
    }

    private static SkillTree buildAntiMagic() {
        return build("anti_magic", "ANTI-MAGIC", "Anti-Magic", "SUMMON_DEMON_SLAYER",
                "The start of every anti-magic user. Summons the Demon-Slayer Sword.", "Anime: Asta's Demon-Slayer Sword.",
                passives("anti_magic", "Resolve", "Endurance", "Rhythm", "Anti-Magic Skin",
                        "Magic damage hurts you 40% less."),
                new SkillBranch[] {
                        new SkillBranch("swords", "SWORDS", "The five swords", false, LANE_A),
                        new SkillBranch("devil", "DEVIL", "Asta and Liebe", false, LANE_B),
                        new SkillBranch("zero", "ZERO", "Cancel magic", false, LANE_C)},
                new Def[] {
                        sp("demon_dweller_sword", "Demon-Dweller Sword", 2, 2, "SUMMON_DEMON_DWELLER",
                                "Summons the Demon-Dweller Sword: strips magic and drains life.", "Anime: Asta's Demon-Dweller Sword."),
                        up("sword_edge", "Sharp Edge", 3),
                        sp("demon_destroyer_sword", "Demon-Destroyer Sword", 3, 5, "SUMMON_DEMON_DESTROYER",
                                "Summons the huge Demon-Destroyer Sword.", "Anime: Asta's Demon-Destroyer Sword."),
                        soon("black_divider", "Black Divider", NodeType.SPELL, 4, 9, "A slash of pure anti-magic.", "Anime: Asta's Black Divider."),
                        soon("black_meteorite", "Black Meteorite", NodeType.KEYSTONE, 6, 14, "Keystone of the Swords branch.", "Anime: Asta's Black Meteorite.")},
                new Def[] {
                        sp("devil_union", "Devil Union", 5, 7, "DEVIL_UNION",
                                "Fuse with the devil Liebe for 5 minutes: huge power and flight.", "Anime: Asta and Liebe's Devil Union."),
                        up("devil_bond", "Liebe's Bond", 9),
                        soon("black_form", "Black Form", NodeType.SPELL, 4, 10, "Asta's black form.", "Anime: Asta's Black Form."),
                        soon("black_asta", "Black Asta", NodeType.KEYSTONE, 6, 15, "Keystone of the Devil branch.", "Anime: Black Asta.")},
                new Def[] {
                        soon("nullify", "Magic Nullify", NodeType.SPELL, 3, 5, "Cancels the magic around you.", "Anime: anti-magic cancels spells."),
                        soon("deflect", "Deflect", NodeType.SPELL, 4, 8, "Turns a spell aside.", "Gameplay: original."),
                        soon("magic_zero", "Magic Zero", NodeType.KEYSTONE, 6, 14, "Keystone of the Zero branch.", "Gameplay: original.")},
                null);
    }
}
