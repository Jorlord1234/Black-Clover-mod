# Clover Magic v0.5 (Black Clover) - NeoForge 1.21.1 + GeckoLib 4.8.x

Built for the "NeoForge 1.21.1" modpack (NeoForge 21.1.252).

## Grimoire Tower
- Rare, tall round stone tower (23 wide, 62 tall) with a huge hollow library hall, ladders, walkways and a dome.
- Altar in the middle: right-click it for the ceremony. Odds: Three-Leaf (most), Four-Leaf about 1 in 50, Five-Leaf about 1 in 1000.
- `/locate structure clovermagic:grimoire_tower` (op) to find one. The Grimoire Altar is also in the creative tab for quick tests.

## Grimoire Mode (HUD)
- **G** toggles Grimoire Mode: the hotbar is replaced by a spell bar, **number keys 1-3 cast**, mana bar above it.
- A small preview of your real hotbar sits on the right. The mouse wheel is locked while the mode is on.
- **K** opens the spell list (cost, unlock level, description).
- Spell slot 1 unlocks at magic level 1, slot 2 at level 3, slot 3 at level 5. Casting gives magic XP.

## Squad armor (netherite level)
- Nine squads, each with a Hood and a Robe: Black Bull, Golden Dawn, Silver Eagle, Crimson Lion, Blue Rose, Coral Peacock, Green Mantis, Purple Orca, Aqua Deer.
- Mana Cloth: 4 White Wool + 1 Mana Crystal (plus shape) gives 2.
- Robe:  M D M / N T N / M M M   (M Mana Cloth, D squad dye, N Netherite Ingot x2, T squad trim)
- Hood:  M D M / M N M           (M Mana Cloth, D squad dye, N Netherite Ingot)
- Dye and trim per squad: Black Bull black + gold ingot, Golden Dawn yellow + gold block, Silver Eagle light gray + feather,
  Crimson Lion red + blaze powder, Blue Rose blue + rose bush, Coral Peacock pink + pink petals, Green Mantis green + vine,
  Purple Orca purple + amethyst shard, Aqua Deer cyan + prismarine crystals.

## Everything else
- Grimoires are bound to you and cannot be crafted. Three/Four-Leaf grimoires crumble when you die.
- Reclaim at any tower altar with a Grimoire Sigil (4 Mana Crystals + 1 Gold Ingot, plus shape).
- Mana Crystal Ore generates underground (Y -64 to 48).
- Test commands (op): `/clover give <3|4|5> <attribute>`, `/clover level <1-10>`, `/clover mana`, `/clover reset`

## v0.4 notes
- **Requires GeckoLib 4.8.x for NeoForge 1.21.1** (add it to the pack).
- Black Bull is the 3D armor prototype (hood, robe, trousers, boots). Other squads are still flat v0.3 hood + robe.
- Config file: `config/clovermagic-common.toml`.
- Crimson Lion Blaze is a real fireball projectile. Anti-Magic no longer grants blanket immunity (Magic Nullify opens a short window).
- See CHANGELOG.md, TESTING.md.

## Known limitations
- Not built or launched by the author. First real build is your GitHub Actions run.
- Tower and ceremony are unchanged from v0.3 (never seen in game by the author), no flying-books animation.
- Only one projectile spell, all other spells are still instant hits.
- Tower spacing and biome list are data files (`data/clovermagic/worldgen/...`), not in the config file.
- Grimoires are only found in the main inventory, not inside backpacks.
- Other textures (grimoires, ores, altar, spell icons) have not been redone yet.
- Squads other than Black Bull still look alike (flat armor).

## v0.5 skill tree notes
- Press **K** with a fire grimoire to open the Fire skill tree. Every element except Anti-Magic now has a tree (Anti-Magic still uses the old level-based spells).
- Respec: one Page of Forgetting resets one branch. Pages are crafted from a Magic Beast Core (dropped by Magic Boars, mages, demons and devils), paper and a Mana Crystal.
- The tree is defined in Java for now (`skill/SkillTrees.java`); moving it to data files is planned once all elements share the template.
- Known limits: plain screen art, placeholder looks for mobs and NPCs, spell bar is a simple row (with pages), sounds are synthesized.

## Outfits
- Robe models and textures are by the project's outfit artist (shared models `robe`, `hood`, `bandana`; one texture per squad).
- Pieces: nine robes (chest) and six hoods (helmet; Black Bull's is a bandana). Craft with Mana Cloth, the squad dye (and trim for robes) and Netherite.

## 3D spell models (v0.6.1)
Every spell has a 3D model, built in code (see `client/vfx`). One effect entity per cast, one renderer. Switch off with `visuals.spellModels`.
Adding a model for a new spell: add a constant with the same name as the spell to `vfx/Fx.java` (anchor, lifetime, range) and write its recipe in one of the `client/vfx/*Fx.java` files.
