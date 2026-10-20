# Changelog

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
