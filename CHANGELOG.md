# Changelog

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
