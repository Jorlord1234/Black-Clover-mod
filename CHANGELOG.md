# Changelog

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
