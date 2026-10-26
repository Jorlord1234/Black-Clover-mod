# What the top anime-power mods do, and what we do about it

Sources: the public CurseForge and Modrinth pages of Jujutsu Kaisen: Cursed Fate (and its addon Unsealed) and DragonMine Z (and its modpacks and addons). Not the code.

## Cursed Fate (Jujutsu Kaisen)
**Right**
- A real RPG spine: a player level, plus mastery of each technique, plus weapon mastery, plus traits and binding vows (risk for reward) and evolving techniques.
- Every technique is animated: custom player animations and a particle/effect system, so a cast looks like the anime.
- Story: named NPCs, quests and structures built around the series.
**Wrong**
- It needs several libraries at once (model, animation, effects, custom renderer), so installing is fragile; effects break when one library is wrong.
- The quest board has been disabled or reworked, so the "go and do things" loop is thin.
- Its progression takes over a modpack, so the community needed an addon (Unsealed) to tame it.

## DragonMine Z (Dragon Ball)
**Right**
- Identity first: you pick a race at the start. Six stats, skills bought with points, transformations with their own mastery.
- Everything is configurable (stat limits, costs, multipliers), and it is addon-friendly.
- A world to explore: structures, NPCs, Dragon Balls and wishes.
**Wrong**
- It stays "work in progress"; balance is left to modpacks and addons.
- Power grows so fast that other mods feel pointless next to it.

## What we do (all in the current version)
| Idea they use | Our version |
| --- | --- |
| Identity from the start | The grimoire ceremony picks your attribute and leaves; **squads** (Black Bull, Golden Dawn...) add a second identity with a bonus that grows with rank |
| Player level + stats | Magic level + **mage stats** (Power, Mana, Control, Vitality, key H) + ranks |
| Mastery per technique | Every grimoire has its own level and skill tree (element mastery); a second grimoire has its own |
| Animated casting | A **casting pose**, a **cast circle with a floating grimoire**, a 3D model for **all 126 spells**, custom **sounds** |
| Easy to use all your spells | **Spell wheel** (key Z) and pages on the spell bar |
| Risk for reward (binding vows) | **Devil Deals**: a second grimoire or a fifth leaf, paid for with 2 hearts each |
| A quest loop | The **quest book** (advancement tab, key L, 23 quests), **Mission Board** missions and **squad missions** |
| Transformations | Devil Union, Spirit Dive, Lightning Battle Fiend, Valkyrie Dress, Black Asta, with flight where it fits |
| Story world | Asta, Julius, Father Orsi; seven structures; boars, mages, demons, devils and two bosses |
| Configurable | `spellDamageMultiplier`, `statCap`, `xpMultiplier`, `allowDevilDeals`, `enableSquads` and more in the config |

## Where we do better on purpose
- **Only one library.** Spell models and effects are plain code, so there is nothing to break there. The only required library is GeckoLib (for the squad robe and hood models). Optional: Curios, FTB Quests.
- **Modpack-friendly.** One multiplier for all spell damage, switches for the big systems.
- **A quest book that works out of the box.**

## Where they are still ahead of us (honest)
- Their animations are fully custom per technique. Ours is one casting pose for every spell.
- Their art is made by artists. Our NPCs and mobs are skins on the player model.
- They have been played for a long time; this mod has not been built or tested yet. The first real playtest will find bugs and balance problems that no checking from my side can find.
