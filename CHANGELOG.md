# Changelog

## v0.9.1 (NOT yet built or tested in Minecraft)
- Merged the artist's art pass (v0.8.1: skins, 3D swords, the new Magic Boar model, the Devil Union suit with glow and slim arms, cloth shading on armor, new spell effect models)
  into v0.9.0. Hobgoblin stays removed. The 60 spells added in v0.9.0 still have no 3D model entry, so they use particles.

## v0.9.0 (NOT yet built or tested in Minecraft)

### Everything that said "coming later" now exists
- All 71 locked skill nodes are real now: **60 new spells** and 11 passives, in every tree (up to 17 spells per element).
  Fire: Timed Scattershot, Vanishing Fireball, Profound Spiral, Crimson Eruption, Mana Zone, Salamander's Breath (+ Multi-circle, Lion Mastery, Two-step Reinforcement).
  Water: Point-Blank Sea Dragon's Roar, Sea God's Hammer, Sea God's Shield, Surging Waves, Aqua Javelin, Sea Serpent's Bellow, Undine's Tide.
  Wind: Tornado Fang, Spirit Storm, Heavenly Wind Ark (flight), Slicing Wind Emperor, Piercing Tornado Needle, Wind Blades Shower, Killing Kakara Blade, Sylph's Tempest.
  Earth: Avalanche, Mountain Breaker, Sinking Ground, Rock Cage, Earth Prison, Stone Wall, Living Fortress, Gnome's Quake.
  Ice: Ice Spears, Absolute Zero, Frozen Chains, Glacier Wall, Frozen World, Crystal Mirror, Eternal Winter.
  Lightning: Thunder Rain, Judgement Bolt, Paralyzing Field, Arc Cage, Thunder Prison.
  Light: Light Spear, Sunburst, Blessing, Revival Light, Light Barrier, Halo, Divine Shield.
  Darkness: Black Moon, Shadow Step, Night Veil, Dark Cloaked Iai Slash, Death Thrust.
  Anti-Magic: Black Divider, Black Meteorite, Black Form, Black Asta, Magic Nullify, Deflect, Magic Zero.
  Spirit branches: Spirit Art per spirit, Spirit Power (+15% damage) and Spirit Absorption (-25% mana cost).
- Walls and cages are made of temporary blocks that vanish after a while (and are all removed when the server stops).
- Up to 24 spells per element can be cast now (was 12).
- New icons for all 60 spells. They have no 3D model yet: the model artist can add them to `vfx/Fx.java`; without an entry a spell just uses particles.

### Removed
- **Hobgoblin** (it was not from the series). Its camp is now the **Midnight Sun Camp** (two mages and a boar).

### Added
- **Magic Broom** (3 sticks + 3 wheat + 1 Mana Crystal): right-click to sit on it and fly. Mouse to steer, W forward (up and down with your view), A/D slide, S slow, sneak to get off. Hit it or sneak + right-click to pick it up.
- **Demon** (Majin): a huge, slow, very strong monster that slams the ground when you are close. Spawns rarely in dark forests and swamps. Drops Magic Beast Cores, Mana Crystals and sometimes a Devil's Contract.
- **Devils**: Megicula (curses you from a distance), Lucifugus (throws wither skulls) and Zagred (the devil lord: both, calls Midnight Sun Mages, boss bar). Spawn with `/summon clovermagic:devil ~ ~ ~ {DevilType:"zagred"}`.
- **Structures**: Devil's Gate (portal ruin with Megicula and Lucifugus) and Devil's Throne (Zagred and two demons, 2 chests; very rare).
- **Quest book without any extra mod**: the advancement screen (key L) has a "Clover Magic" tab with 21 quests that give items and experience and show a toast. The FTB Quests chapter now uses the same advancements and only gives experience (so nothing is given twice).

## v0.8.1 (NOT yet built or tested in Minecraft)

### Art pass: everything that is not a spell
- **Story characters** (Asta, Julius, Father Orsi) and the **Midnight Sun Mage**: new full 64x64 skins painted on the real player-skin layout
  (all faces, shaded, with overlay layers): Asta with spiky black hair, red eyes, white vest, Black Bull robe and a white bull emblem on the cape;
  Julius with silver hair, a gold-trimmed cream robe, hat band, teal sash and a white cape; Orsi with grey hair and beard, a black cassock, white collar and a gold cross;
  the mage with a dark hood, glowing red eyes, a black mask and an orange sun emblem.
- **Devil Union suit**: the whole body is painted now (it only had a few patches before): armoured black plates, spiked red chest star, red spine, arm slashes,
  silver wristband, long red shin strips, raised plates on the overlay layer. New `devil_union_suit_slim.png` for slim arms (the layer picks it from the player's skin type),
  and `devil_union_suit(_slim)_glow.png`: the red markings are drawn again at full brightness so they glow.
- **Devil Union gear texture** (horns, hair, shoulder plates, tail, wings): black horn bone with ridge rings, glowing red crown, red star on the shoulder plates,
  bones with red joints and a torn crimson wing membrane (transparent ragged edge).
- **Magic Boar**: its own model now (`MagicBoarModel`) instead of the vanilla pig: shoulder hump, mane spikes, curved tusks, pointed ears, tail.
  It walks, sniffs when idle, lowers its head and bristles when it charges. New fur texture with glowing red eyes, ivory tusks and orange scar marks.
- **Yultim Spider**: new texture on the vanilla spider layout (dark purple chitin, plates, red hourglass on the back, banded legs).
- **Anti-Magic swords**: real 3D held models (about 100 small boxes each) with 64x64 textures, following the reference chart:
  Demon-Slayer (wide pointed blade, long crossguard with ball ends), Demon-Dweller (long double-edged blade with an ornate white centre),
  Demon-Destroyer (cleaver with a cross guard and scribbled pattern). They use the normal sword hand pose and show as 3D in the inventory.
  The old 16x16 icons were replaced (same file names).
- **Robes, hoods, bandana** (all squads): cloth shading added on every face (folds, top light, hem shadow, edge darkening, weave). Colours and emblems are unchanged.
- Not touched: the Hobgoblin (being removed), grimoire and other item icons, block textures, GUI.

## v0.8.0 (NOT yet built or tested in Minecraft)

### Added: more content from the anime and manga
- **Magic Boar** (the beastly boars the Golden Dawn is sent to defeat in Saussy): fast and hard-hitting, drops pork and sometimes a Magic Beast Core. Spawns in the overworld.
- **Midnight Sun Mage** (hostile mages in the style of the Eye of the Midnight Sun): keeps its distance and throws fire; drops Mana Crystals. Spawns in the overworld and guards the dungeon.
- **Yultim Dungeon** (the dungeon with the giant spider from the trainee mission): a ruined gate on the surface (forests, plains, taiga, meadow, savanna) with a long staircase down to a boss chamber.
  - **Boss:** the Giant Spider of the Yultim Dungeon, a boss with a boss bar. Its bite slows you. It drops 3 Magic Beast Cores, Mana Crystals, cobwebs and, with a 25% chance, a Devil's Contract.
  - **Chamber:** two Midnight Sun Mages and two chests (Mana Crystals, Mana Potions, Page of Forgetting). Find it with `/locate structure clovermagic:yultim_dungeon`.
- **Mana Potion**: drink it to get half of your mana back. Craft 2 from 2 glass bottles, 2 Mana Crystals and 1 Magic Beast Core (a new use for the Core).
- Spawn eggs for the new monsters; 3 new quest advancements (boar_hunter, mage_slayer, yultim_slayer) and 3 new quests in the FTB Quests chapter (19 quests now).
- Placeholder looks again (vanilla pig and spider models with new textures, the player model for the mage): for the model artist to replace.

### Notes
- The Hobgoblin is an invented magic beast, not from the series (the series has beasts like the giant spider and the boars, and demons). It can be renamed or replaced.

## v0.7.0 (NOT yet built or tested in Minecraft; includes the 3D spell models of v0.6.1)

### Fixed
- **`/clover level 20` now works**: the command only accepted 1 to 10 and did not update the client. The HUD and grimoire screen also stopped at level 10; they use the real maximum now.

### Changed
- **Devil Union looks like the anime** (new model): big ram horns that curl around the ears, spiky black hair, a red thorn crown, spiked shoulder plates, a tail,
  large ragged bat wings, and a black armored suit with red markings drawn over your skin (head stays visible). Flight and the 5 minutes stay.

### Added
- **Mobs**: the Hobgoblin (first Magic Beast: strong melee, 25% Magic Beast Core and 40% Mana Crystal drops, spawns in the overworld, spawn egg in the creative tab).
- **Story characters (NPCs)**: Asta, Julius Novachrono (the Wizard King) and Father Orsi. Right-click to talk. They cannot be hurt. Julius makes you a Magic Knight at magic level 15.
  Placeholder skins on the player model. Spawn with `/summon clovermagic:magic_npc ~ ~ ~ {NpcType:"asta"}` (asta, julius, orsi).
- **Structures**: Clover Church (Father Orsi, chest), Hobgoblin Camp (2 hobgoblins, chest with a Magic Beast Core), Black Bull Hideout (Asta), Royal Pavilion (Julius).
- **Quests**: 16 hidden advancements (got_grimoire, level_5/10/15/20, four_leaf, five_leaf, spirit_bond, devil_union, magic_knight, talked_*, hobgoblin_slayer, second_grimoire, devil_mark)
  and a ready FTB Quests chapter in `extras/ftbquests/` (see the README there).
- **Devil Deals** (at the altar, each costs 2 hearts of maximum health for ever): Devil's Contract: Second Grimoire (a second grimoire of another element: sneak + right-click a grimoire to switch;
  it keeps its own level and skill tree) and Devil's Contract: Fifth Leaf (+50% mana, +25% spell power). Recipes: nether star, 4 mana crystals, 2 magic beast cores, plus a book or a wither skeleton skull.
- **Curios slot** "Grimoire" (only when Curios is installed; the mod does not need Curios to build or run). A grimoire in that slot works like one in the inventory.

## v0.6.1 (NOT yet built or tested in Minecraft)

### Added: 3D models for every spell (66 of 66)
- Every spell now has its own 3D model instead of only particles. Examples: Leo Rugiens roars a flaming lion head, Leo Palma drops a flaming paw,
  Sea Dragon's Roar is a water dragon head on a jet, Sea Dragon's Nest coils a dragon around you, Gale White Bow is a bow of wind with a spiral arrow,
  Swift White Hawk is a flapping hawk, Earth Spike erupts stone spikes that follow the ground, Stone Armor plates your body, Frost Lance, Ice Prison,
  Thunder Bolt shockwave, Radiant Aegis dome, Prism Burst rainbow rays, Black Hole, Dimension Slash tears a rift, Valkyrie Dress, Spirit Dive wings
  (one look per element), the three sword summons, Calidus Brachium fist and more. The Exploding Fireball is now a real 3D fireball with a burst.
- Models are drawn by one small effect entity (`SpellVfxEntity`, no damage, no collision) and one renderer. No model files needed:
  the shapes are built in code (`client/vfx`), stone and ice use the vanilla block textures, everything else is tinted vertex colours.
- Buffs (Stone Armor, Ice Shell, Water Veil, Overcharge, Valkyrie Dress, Spirit Dive...) follow you smoothly for as long as the effect lasts.
- Config `visuals.spellModels` (default true) switches the models off if something looks wrong. A failing model can never break a cast.
- Fireball projectile now has a renderer (it had none) and detonation shows a fire burst. Lightning arcs (Thunder Chain, Static Shock, Spark Barrage) are 3D bolts.
- Original designs only. Nothing was copied from other mods (Iron's Spells is All Rights Reserved).

### Notes for the model artist
- To replace a spell model with a Blockbench/GeckoLib one later: keep the Fx entry, change its recipe in `client/vfx/*Fx.java` (or draw your own in `SpellVfxRenderer`).

## v0.6.0 (NOT yet built or tested in Minecraft)

### Fixed
- **Max magic level is 20 again**: an old saved config capped it at 10. The setting is now `maxMagicLevel` (default 20); the old `maxLevel` is ignored.
- **Bandana is visible with a second skin layer** (the band is a little wider than the hat layer).
- **Five-Leaf Grimoire texture** had 8 see-through pixels inside the book: filled.
- **Cutscene**: the circling grimoires are now one of every OTHER element (no more duplicate earth and anti-magic books); the chosen one stays and is revealed.
- New original Mana Cloth and Mana Crystal icons (same idea as the shapes you showed, not copies).

### Changed
- **Devil Union**: lasts 5 minutes (was 30 seconds), you can fly the whole time, 10 minute cooldown. (Model/suit rework is with the model artist.)
- **Anti-Magic now has a skill tree** and its spells are the swords: Demon-Slayer Sword (root), Demon-Dweller Sword, Demon-Destroyer Sword, Devil Union.
  Swords are summoned into your hand for 5 minutes, cannot be dropped, strip magical effects on hit (Dweller also drains life). Cast again to dismiss.
  Anti-Magic Skin passive: magic damage hurts 40% less. Removed: Magic Nullify, Black Asta, Demon-Dweller Slash.
- **Spirits are locked**: only a Four-Leaf fire, water, wind or earth mage from magic level 10 can be chosen by their spirit: every level-up has about 1 in 12 chance
  (config `spiritBondOneIn`). Then the Spirit branch opens: Spirit Contract (+15% damage) and Spirit Dive (2 minutes of buffs and flight).
  Spirits: Salamander (fire), Undine (water), Sylph (wind), Gnome (earth).
- **Fire**: new Lioness branch (Mereoleona): Calidus Brachium, Calidus Brachium Barrage (Two-step Reinforcement and Mana Zone are planned).
- **Water**: Valkyrie Dress (armor of water, 1 minute).
- **Lightning**: True Lightning Magic (dash as a bolt) and Lightning Battle Fiend (Luck's ultimate, 1 minute, zaps nearby monsters).
- **Darkness**: now has Yami's moves: Dark Cloaked Avidya Slash, Dark Cloaked Dimension Slash, Black Hole (Black Moon, Iai Slash and Death Thrust are planned).
- Up to 12 spells per element (was 8).

### Added
- **Page of Rebirth** (1 Page of Forgetting + 4 Mana Crystals + 1 Magic Beast Core): use it on the altar to reroll your grimoire (resets level and skill tree).
- **Grimoire upgrade**: use an enchanted book with Fortune III on the altar with a Three-Leaf Grimoire and it becomes a Four-Leaf.
- `/clover spirit` (test: the spirit bonds with you).
- 15 new spell icons, 3 sword textures, Page of Rebirth texture.

### Not done yet (planned)
- 3D models for spells (with the model artist), mobs, NPCs (Asta, the Wizard King, Father Orsi), more structures, FTB Quests, a Curios slot for the grimoire (needs the Curios mod as a dependency),
  a second grimoire, and Devil Deals (for example a Five-Leaf water grimoire).

## v0.5.4 (NOT yet built or tested in Minecraft)

### Changed
- **Devil Union now has a real 3D model** instead of particles: two curled horns, a glowing red spiked crown and bat-like wings
  (arm + 4 fingers with membrane strips) that flap and fold back when you crouch. Everyone nearby sees it, also on other players.
  It is drawn while the new "Devil Union" status effect lasts (30 seconds, shows up in the effect list with its own icon).
- Particles are now only a light red/black mist around the body.
- New files: `ModEffects`, `client/DevilUnionModel`, `client/DevilUnionLayer`, `textures/entity/devil_union.png`, `textures/mob_effect/devil_union.png`.

## v0.5.3 (NOT yet built or tested in Minecraft)

### Added
- **Devil Union** (Anti-Magic, unlocks at magic level 7, `/clover level 7`): 30 seconds of Strength IV, Speed II, Resistance II, Regeneration,
  Jump Boost and +6 hearts, magic cannot hurt you for the first 10 seconds, enemies around you are pushed away.
  Horns and wings of black and red particles follow you while it lasts. Costs a lot of food, 90 second cooldown. (Particles for now; a real 3D model can come later.)
- **First-grimoire cutscene** (about 8 seconds, click or ESC skips): grimoires circle faster and faster, all but one fade away,
  a flash in the element's color, then the chosen grimoire and its attribute are revealed. Plays at the altar ceremony.
  Preview any version with `/clover cutscene <3|4|5> <attribute>`.
- **Grimoire Tower bricks are now random**: the tower file only has plain stone bricks and a processor turns about 20% into mossy and 20% into cracked,
  different for every tower (`data/clovermagic/worldgen/processor_list/grimoire_tower_aging.json`).

### Changed
- New inventory icons for all robes (a tunic with sleeves, squad colors, trim along the hem), hoods (hood with face opening) and the Black Bull bandana.
- The old title text at the ceremony is gone (the cutscene shows the name instead).
- Network protocol version is now 4 (client and server must both have this version).


## v0.5.2 (screen fix - NOT yet tested in Minecraft)

### Fixed
- The skill tree screen (K) and the grimoire screen (G) looked foggy: the vanilla blurred menu background was drawn on top of them.
  Both screens now draw only their own backdrop.

## v0.5.1 (skill trees for all elements - NOT yet built or tested in Minecraft)

### Added
- **Skill trees for Water, Wind, Earth, Ice, Lightning, Light and Darkness** (K opens it), same layout as Fire: root + 4 passives +
  3 branches of 5 nodes each (Water, Wind and Earth also have a locked Spirit branch). Anti-Magic has no tree yet.
- 21 new spells, each with an icon: Sea Dragon's Roar, Climbing Coelacanth, Sea Dragon's Nest, Gale White Bow, Thief's Wind Cloud,
  Swift White Hawk, Boulder Toss, Stone Bind, Earthen Shield, Hailstorm, Ice Prison, Frost Barrier, Static Shock, Spark Barrage,
  Overcharge, Prism Burst, Light Speed Dash, Radiant Aegis, Dark Pulse, Shadow Bind, Dread Mark.
- Every element has 6 spells (root + 5) and many more nodes marked "soon" (greyed, cannot be unlocked), like Fire's locked ones.
- Passives per element: Focus (+10% damage), Flow (-10% mana), Haste (-10% cooldown) and a Skin (needs 20% mana):
  Water = cannot drown, Wind = no fall damage, Earth = 15% less damage, Ice = cold cannot hurt, Lightning = lightning cannot hurt,
  Light = slow regeneration, Darkness = night vision. Each branch has an upgrade node: +20% damage for the spells of that branch.
- The tree screen now uses the element's color for links and glow.

### Canon
- Names checked against the wiki: Water (Sea Dragon spells, Climbing Coelacanth, Valkyrie Dress, Undine), Wind (Yuno's spells, Sylph).
- Earth, Ice, Lightning, Light and Darkness spell names are ORIGINAL (the wiki pages could not be read); every node says so in its canon line.

## v0.5.0 (skill tree core - NOT yet built or tested in Minecraft)

### Added
- **Fire skill tree** (K opens it for fire users): root + 4 passives + Explosive, Spiral and Lion branches + a locked Spirit branch.
  Points: 1 per magic level, +1 at levels 5, 10, 15, +2 at level 20. Level cap is configurable (default 20, `progression.maxLevel`).
- Fire spells now come from the tree: Flame Bullet (root), Exploding Fireball, Exploding Buckshot, Spiral Flame, Sol Linea, Leo Rugiens,
  Leo Palma, Ignis Columna. Upgrades: Blast Radius, Spiral Range. Passives: Burn Damage, Long Burn, Fire Affinity, Ember Skin.
- Server validates every unlock and reset (level, prerequisites, points, branch lock, Page of Forgetting).
- Page of Forgetting (reset one branch, free in creative) crafted from a Magic Beast Core + paper + Mana Crystal.
  Magic Beast Core is meant to drop from the Hobgoblin, the first Magic Beast mob (the mob itself is not built yet).
- Commands (op): `/clover unlock <node>`, `/clover tree reset`.
- Sync now carries unlocked nodes, available points and the level cap.

- **New squad armor** from the outfit artist: 3D robes for all nine squads, hoods for Golden Dawn, Blue Rose, Coral Peacock, Green Mantis and
  Purple Orca, and the Black Bull bandana. One shared model per piece type, one texture per squad (GeckoLib).
  Trousers and boots are gone (and the hoods for Silver Eagle, Crimson Lion and Aqua Deer, which have no hood in the artist's set).

### Changed
- Old fire spells Crimson Lion Blaze and Flame Ring are gone (not canon names); Exploding Fireball replaces the fireball.
- Elements without a tree yet (water, wind, earth, ice, lightning, light, darkness, Anti-Magic) still use the old level-based spells.
- The skill tree screen is functional but plain: the ornate frames, glows and animations from the approved design come in the art pass.
- The spell bar HUD still shows the simple v0.4 style (to be redone after the tree).

### Armor values (from the code constants, not measured in game)
- Robe 8 armor, hood or bandana 3 armor: 11 armor points in total, toughness 3 per piece, knockback resistance 0.1 per piece.
  A full set is therefore 11 of the 20 a full netherite suit gives, because only two pieces exist now.

### Verified here (not in game)
- Java syntax check of all sources, JSON/model/lang/icon/recipe cross-check, and a plain-Java test of the skill tree data and point rules
  (ids, prerequisites, level order, spell names, locked branch, points at levels 1/5/10/15/20/25).

## v0.4.1 (hotfix, still untested in game)
- Black Bull armor no longer crashes the game when its 3D model file is missing from the jar: it logs an error and falls back to the vanilla armor layer.
  (If the 3D model is missing, your jar was built from an incomplete upload - see TESTING.md, step 0.)

## v0.4.0 (prototype release - NOT yet built or tested in Minecraft)

### Added
- Config file `config/clovermagic-common.toml`: Four-/Five-Leaf odds, base mana, mana per level, Four-Leaf mana multiplier,
  mana regeneration, XP multiplier, spell power per level, Magic Nullify window.
- Black Bull 3D armor prototype (GeckoLib): hood, robe, trousers and boots with their own geometry, texture and icons.
  Needs the GeckoLib mod (NeoForge 1.21.1, 4.8.x) at runtime.
- Real projectile: Crimson Lion Blaze is now a server-spawned fireball entity (straight flight, collision with blocks and entities,
  area burst, 80 tick lifetime, never damages its caster or terrain). `FireballProjectile` is the pattern for future projectile spells.
- Mana maximum is now sent to the client, so the HUD matches the server config in multiplayer.

### Changed
- Magic XP: attack spells only give XP while a living non-player target is within 24 blocks, repeated casts in a short time give less
  (down to 15%), casting in creative gives none, and the whole rate is configurable.
- Anti-Magic: carrying the Five-Leaf Grimoire no longer gives immunity. Casting Magic Nullify opens a short window
  (default 3 seconds) that negates magic, lightning and fireball damage only. Other damage is never affected.
- The other eight squads keep their v0.3 flat hood and robe until the Black Bull prototype is confirmed working.

### Armor values (computed from the code constants, not measured in game)
- Per piece: helmet 3, chestplate 8, leggings 6, boots 3 = 20 armor points; toughness 3 per piece = 12; knockback resistance 0.1 per piece = 0.4.
  These equal vanilla netherite. Only Black Bull has all four pieces so far.
