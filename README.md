# Clover Magic (Black Clover) - NeoForge 1.21.1

Built for the "NeoForge 1.21.1" modpack (NeoForge 21.1.252). **Requires GeckoLib 4.8.x** (the squad robes and hoods). Optional: Curios (a "Grimoire" slot), FTB Quests (see `extras/ftbquests/`).

> **Status:** this mod has never been built or launched by its author. Your first GitHub Actions run is the first real build. See `TESTING.md` for what to check.

## The basics
- **Grimoire Tower** (rare structure): right-click the altar for the ceremony. Three-Leaf is the most common, Four-Leaf about 1 in 50, Five-Leaf (Anti-Magic) about 1 in 1000. Odds are in the config. Find one with `/locate structure clovermagic:grimoire_tower`.
- Grimoires are bound to you and cannot be crafted. Three- and Four-Leaf grimoires crumble when you die; reclaim at an altar with a Grimoire Sigil (4 Mana Crystals + 1 Gold Ingot).
- Nine elements: fire, water, wind, earth, ice, lightning, light, darkness and Anti-Magic. 127 spells, each with a 3D model.
- **G** toggles Grimoire Mode (spell bar, number keys 1-9 cast, mouse wheel changes page). **Z** opens the spell wheel. **K** opens the skill tree (spell list for elements without one). **H** opens mage stats. **L** opens the quest book (the "Clover Magic" advancement tab).

## Progression
- Casting gives magic XP (anti-farm rules apply). Max level 20 by default. One skill point per level (more at 5, 10, 15 and 20), one stat point per level above 1.
- Stats (key H): Power, Mana, Control, Vitality, up to 20 each (config `statCap`). `/clover stats reset` refunds them.
- Skill trees per element with spells and passives; a Page of Forgetting resets one branch, a Page of Rebirth rerolls the grimoire.
- Spirits: Four-Leaf fire, water, wind and earth mages from level 10 can be chosen by their spirit.
- **Devil's Contract** (craft it: Nether Star, 4 Mana Crystals, 2 Magic Beast Cores, a Book and a Wither Skeleton Skull). Use it anywhere, then click **Second Grimoire** or **Fifth Leaf** in the chat. Each deal costs 2 hearts of max health, for ever. Devils, the Demon and the Yultim Spider can also drop contracts. In the series a contract gives you a share of a devil's attribute, and a devil can only live in a five-leaf grimoire.
- **Squads** (Black Bull, Golden Dawn, Silver Eagle, Crimson Lion, Blue Rose, Coral Peacock, Green Mantis, Purple Orca, Aqua Deer): hold the robe and sneak + right-click Julius at level 10. Bonus grows with rank. Missions give squad points.
- **Mission Board**: right-click for a Mission Scroll that fits your level (one at a time).

## World
- Mobs: Magic Boar, Midnight Sun Mage, Demon, three Devils (Zagred is a boss), the Giant Spider (boss of the Dungeon, which is not in the series: the real arc's boss is Mars).
- NPCs: Asta (Black Bull Hideout), Father Orsi (Hage Church) and Julius, who has no building: he wanders into the overworld near players now and then (config `juliusWanders`, `juliusSpawnOneIn`; or `/summon clovermagic:magic_npc ~ ~ ~ {NpcType:"julius"}`). Five structures: Black Bull Hideout, Hage Church, Grimoire Tower, Midnight Camp (the Eye of the Midnight Sun) and the Dungeon (from the Dungeon Exploration arc; internal id `yultim_dungeon`).
- Anti-Magic summons four swords: Demon-Slayer (huge blunt cleaver), Demon-Dweller (ornate broadsword with a groove), Demon-Destroyer (stubby, wide shovel-like tip) and Demon-Slasher (a katana, the only one with a real edge).
- Magic Broom (fly), Mana Potion, Mana Crystal ore, nine squads of armor.

## Config (`config/clovermagic-common.toml`)
Ceremony odds, mana, XP and level cap, `spellDamageMultiplier`, `statCap`, `allowDevilDeals`, `enableSquads`, `spellsHurtPlayers`, `spellsPlaceBlocks`, `disabledDimensions`, `maxSpellModelsNearby`, `visuals.spellModels`, spirit chance and the Anti-Magic nullify window. Structure spacing and biomes are datapack files under `data/clovermagic/worldgen/`.

## For addon authors and modpack makers
- `com.clovermagic.api.SpellCastEvent.Pre` (cancelable) and `.Post` are fired on the NeoForge event bus for every cast.
- Temporary-block spells fire `BlockEvent.EntityPlaceEvent` first, so claim and protection mods can veto them.
- Spells and skill trees are still defined in Java (`magic/Spell.java`, `skill/SkillTrees.java`); data-driven spells are planned.

## Known limitations
- Never built or tested (see above).
- NPCs and several mobs are skins on vanilla models; one casting pose for every spell; synthesized sounds.
- Only the pure-text chat messages are translatable so far (`translatableWithFallback`); messages built from several pieces are still English only.
- Grimoires are only found in the main inventory (and the Curios slot), not inside backpacks.
- No in-game guide book and no JEI/EMI pages yet.
- Grimoire art, ore and altar textures, and the GUI are plain.

See `CHANGELOG.md`, `TESTING.md`, `docs/ART_BIBLE.md` and `docs/COMPETITOR_ANALYSIS.md`.
