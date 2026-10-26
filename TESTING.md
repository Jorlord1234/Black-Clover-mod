# What to check in game

## v0.15 changes (nothing below has been run by the author)
L. Hage Church (`/locate structure clovermagic:clover_church`): sits on the ground inside its yard wall, door in the south gable, Orsi near the altar, the Mission Board just inside the door, the chest in the orphanage wing (kitchen corner), beds placed correctly, potatoes growing on farmland next to the water channel (they must not pop off), bell in the belfry (ladder in the steeple), trees do not decay.
M. Grimoire Tower (`/locate structure clovermagic:grimoire_tower`): the stair leads up the mound to the open arch (it must connect to the floor with no gap), the Grimoire Altar stands on the gold block in the middle of the hall and the ceremony still works, ladder up the north wall to all four galleries, nothing floats, the aging processor does not break the bookshelves.

## v0.14 changes (nothing below has been run by the author)
J. Find a Black Bull Hideout (`/locate structure clovermagic:black_bull_hideout`). It must sit on the ground (the foundation goes 4 deep), the gate faces south, and you can walk in through the gate, between the houses through the doorways, and climb the ladder in the back of the great hall up through the stack. Check the tower ladder too.
K. Inside: Asta in the great hall, the Mission Board near the gate, the chest in the kitchen (right house; 2 Mana Crystals, 8 cooked beef, 3 iron ingots), furnace, barrel and crafting table, targets in the left house. No block floats, no mob is stuck in a wall, no leaf block decays (they are marked persistent).

## v0.13 changes (nothing below has been run by the author)
G. Anti-Magic: unlock the Demon-Slasher node (level 7). Cast it: a katana appears in your hand with the new sprite, the summon circle shows a slim blade. Cast again: it disappears. Summoning one sword removes any other.
H. All four swords: look at each in your hand in first and third person: sizes should be Slayer (huge), Destroyer and Slasher (medium) and Dweller (standard). Check the inventory icon of each.
I. Skill tree: the SWORDS branch is longer by one node. Check the screen still fits and Black Divider can only be unlocked after the Slasher.

## v0.12 changes (nothing below has been run by the author)
A. Cooldowns: cast a spell, unlock another node or swap grimoires: the first spell's cooldown must NOT reset. Log out and in during a cooldown: it must still be running.
B. Cast a spell, then try a different slot within the same 4 ticks (use a macro or modified client): the second cast must be ignored. Normal play must feel unchanged.
C. Walls and cages (`/clover give 3 earth`, Stone Wall, Rock Cage): they vanish on time. Cast one, kill the server process (not a clean stop), restart, wait: the blocks must be removed after the world loads. Cast one and walk far away so the chunk unloads: nothing may lag or load the chunk, the blocks are removed when you come back.
D. With a claim mod (FTB Chunks etc.): cast a wall inside someone else's claim, it must be refused.
E. Config: `spellsHurtPlayers=false` (two players: spell damage to the other player is cancelled), `spellsPlaceBlocks=false` (walls do nothing), `disabledDimensions=["minecraft:the_end"]` (casting there says "Magic does not work here"), `maxSpellModelsNearby` (spam casts: models stop appearing past the cap, spells still work).
F. World load: the new `extra` data in the save (squad, squad points, cooldowns). A world saved by v0.11 loses squad and squad points (first build never shipped, so this should not matter).

## v0.5 skill tree (nothing below has been run by the author)
1. `/clover give 3 fire`, `/clover level 5` -> you should have 6 skill points. Press **K**: the Fire tree screen opens.
2. Hover nodes: tooltip shows name, type, cost, level and prerequisites (green v / red x), description and canon note.
3. Click Exploding Fireball (2 points). Points drop to 4. Press **G**, then **2**: a fireball flies, hits, bursts, never hurts you.
4. Unlock Blast Radius (needs the fireball): the blast is visibly wider. Unlock Spiral Flame at level 3, Sol Linea at level 3, and so on.
5. Locked/not implemented nodes (Timed Scattershot, Vanishing Fireball, Spirit branch...) cannot be unlocked and say so.
6. `/give @s clovermagic:page_of_forgetting`, click **Reset** on a branch banner: the page is used, points come back, bar updates.
   Without a page (survival) it must refuse. In creative it is free.
7. Burn Damage / Long Burn / Fire Affinity: fire hits do more damage / targets burn longer. Ember Skin (`/clover unlock ember_skin`): with 20%+ mana,
   standing in lava or fire does no damage; below 20% mana it does.
8. Log out and back in: unlocked nodes and points persist. `config/clovermagic-common.toml`: change `maxLevel`, restart, check the cap.
9. Another element (e.g. `/clover give 3 water`): K opens the old spell list, water spells still work.

## v0.5 outfits
1. Wear a robe and a hood/bandana: model shows, textures are the right squad, no pink/black, follows walking and arm swings, no big clipping.
2. Check all nine robes and the six hoods in first and third person. `/attribute @s minecraft:generic.armor get` with robe + hood: expect 11.
3. Silver Eagle, Crimson Lion and Aqua Deer have a robe only (no hood item) on purpose.

## v0.4 prototype (earlier)

Nothing below has been run by the author. Please report each line as OK / broken, with `latest.log` for anything that crashes.

0. Use the jar from the LAST GitHub Actions run (after all three zips were uploaded). Open the jar with 7-Zip: it must contain
   `assets/clovermagic/textures/item/mana_crystal.png` and `assets/clovermagic/geo/item/armor/black_bull.geo.json`.
   If not, the jar is from an earlier run or the files were uploaded to the wrong folder.
1. Game starts with GeckoLib 4.8.x installed (and refuses with a clear message without it).
2. Creative tab "Clover Magic": Black Bull Hood, Robe, Trousers, Boots show correct icons.
3. Wear all four pieces: model appears, is not invisible or black/pink, follows walking, sneaking and arm swings, no big clipping.
4. Check textures in first and third person, in the inventory screen and on an armor stand.
5. `/attribute @s minecraft:generic.armor get` with the full set on: expect 20. toughness: expect 12. `minecraft:generic.knockback_resistance`: expect 0.4.
6. Recipes show in the recipe book and craft (survival).
7. `/clover give 3 fire`, `/clover level 5`, press G, press 3: the fireball flies, hits entities and blocks, bursts, does not hurt you.
8. Cast attack spells at empty air for a while: XP should barely or not grow. `/clover reset` to retest.
9. `/clover give 5 anti_magic`: carrying the grimoire alone must NOT protect you from a skeleton arrow or a witch potion;
   only the 3 seconds after Magic Nullify protect from magic damage.
10. Edit `config/clovermagic-common.toml` and restart: changed values take effect.
11. Skill trees: `/clover give 3 <water|wind|earth|ice|lightning|light|darkness>`, `/clover level 20`, press K: tree opens in the element's color.
    Unlock a few nodes (or `/clover unlock <node>`); the spell bar should list exactly the unlocked spells; cast each one.
12. Passives: unlock `<element>_focus`, `_flow`, `_haste`; check damage, mana cost and cooldown change a little. Test each `_skin`
    (drowning, fall, freezing, lightning, 15% less damage, regeneration, night vision). Anti-Magic must still have no tree.
13. Check the 21 new spell icons in the HUD and the tree screen (no missing-texture squares).
14. `/clover cutscene 3 fire`, `/clover cutscene 4 water`, `/clover cutscene 5 anti_magic`: cutscene plays, text is readable, click skips after 2 seconds.
    Then the real thing: `/clover reset` and use the altar.
15. `/clover give 5 anti_magic`, `/clover level 7`, cast Devil Union (4th spell): effects, horns and wings appear for 30 seconds, hunger drops fast.
16. Build a new world and find 3 Grimoire Towers: the stone bricks should be a random mix of normal, mossy and cracked, different in each tower.
17. Check the new robe, hood and bandana icons in the inventory.
18. Devil Union model: cast it, look at yourself in third person (F5): horns, crown and wings must appear, wings flap, crouching folds them.
    Check with a second player (or a friend) that they see it too. Effect icon and name "Devil Union" show in the effect list. Check the texture is not purple/black.
19. Level cap: `/clover level 20` must work (delete the old `clovermagic-common.toml` if it still says maxLevel).
20. Anti-Magic: `/clover give 5 anti_magic`, `/clover level 12`, press K: new tree. Cast the sword spells: sword appears in hand, cannot be dropped, vanishes after 5 minutes or when cast again.
21. Devil Union: fly for 5 minutes, flight must stop when it ends (and after a relog). Spirit: `/clover give 4 fire`, `/clover level 12`, `/clover spirit`, K: Spirit branch is open; unlock Contract and Dive; cast Spirit Dive (flight).
22. New spells: Calidus Brachium (+ Barrage), Valkyrie Dress, True Lightning, Lightning Battle Fiend (zaps monsters), Avidya Slash, Dimension Slash, Black Hole.
23. Altar: Page of Rebirth (craft it, right-click the altar: ceremony again). Fortune III book on the altar with a 3-leaf grimoire: becomes 4-leaf.
24. Cutscene: the other books are different elements, none duplicated. Bandana: wear it with a second skin layer (hat layer on): the band must show.
25. Check the new item icons (cloth, crystal, swords, Page of Rebirth) and the 15 new spell icons.
26. 3D spell models (v0.6.1): `/clover give 4 fire`, `/clover level 20`, cast every spell once (creative, `/clover mana` if you have it). Each one must show a 3D shape, not only particles.
    Check: beams start at your hand and end at the first block; buffs follow you without lag when you run; target spells (Leo Palma, Stone Bind, Ice Prison, Shadow Bind, Dread Mark) sit on the target.
    Earth Spike spikes must sit on the ground (also on stairs and slopes). First person: Stone Armor, Water Veil, Ice Shell etc. must not block your view (shells get faint).
    Exploding Fireball flies as a 3D orb with a flame tail and bursts. Thunder Chain / Static Shock / Spark Barrage show 3D bolts. Look from far away (50+ blocks): effects must still draw.
    If anything is wrong set `visuals.spellModels = false` in the config and tell me which spell.
27. `/clover level 20` must set level 20 and the HUD must show it.
28. Devil Union (cast it, look at yourself in third person): ram horns, spiky hair, crown, shoulder plates, tail, big wings, black suit with red marks. Face must stay visible.
29. Mobs and NPCs: `/summon clovermagic:magic_npc ~ ~ ~ {NpcType:"julius"}`; right-click them (chat lines). `/clover level 15` then talk to Julius.
30. Structures: `/locate structure clovermagic:clover_church` (also midnight_camp, black_bull_hideout, royal_pavilion). Check that NPCs and chests are inside.
31. Devil Deals: craft both contracts (creative: Devil's Contracts are in the Clover Magic tab), right-click the altar with one. Second grimoire: sneak + right-click to switch; hearts drop by 2 per deal.
32. Quests: `/advancement grant @s only clovermagic:level_5` etc. With FTB Quests: copy `extras/ftbquests/clover_magic.snbt` as described in its README.
33. Curios (optional): with Curios installed, the Grimoire slot appears in the Curios screen; a grimoire placed there must still cast.
34. Spawn eggs: Magic Boar (charges you), Midnight Sun Mage (shoots fireballs from a distance), Yultim Spider (huge, boss bar on top, slows you on hit).
35. `/locate structure clovermagic:yultim_dungeon`, walk in through the gate, down the stairs: boss chamber with the spider, 2 mages, 2 chests. Check the boss drops and the boss bar.
36. Craft Mana Potions, drink one: half of your mana comes back, you get the empty bottle back.
37. Skill trees: `/clover give 3 fire`, `/clover level 20`, `/clover skill points`... unlock every node of a tree (K): no node may say "coming later" any more. Cast a few new spells per element; walls and cages must vanish after 10-20 seconds.
38. Broom: craft or take it from the creative tab, right-click: you sit on it. Steer, fly up and down, get off with sneak. Pick it up again.
39. `/summon clovermagic:demon ~ ~ ~`, `/summon clovermagic:devil ~ ~ ~ {DevilType:"megicula"}` (also lucifugus, zagred): check skins, boss bar (Zagred), curse and skulls, Zagred calling mages at half health.
40. `/locate structure clovermagic:devil_gate`, `clovermagic:devil_throne`, `clovermagic:midnight_camp`.
41. Press L: the "Clover Magic" tab is the quest book. `/advancement grant @s only clovermagic:level_5` must give a toast, items and experience.
42. Spell pages: unlock more than 9 spells (`/clover level 20`, unlock a tree). In Grimoire Mode (G) the bar shows 9 spells, the mouse wheel changes the page, keys 1-9 cast the spells of the current page.
43. Cast any spell: a rune circle under you and a floating grimoire beside you, plus a charge sound and an element sound. Check another player sees the circle too.
44. Press H: stats screen. `/clover level 10`, spend points; check max mana, damage, cooldowns and max health change.
45. Craft a Mission Board (or find one in a church), right-click: Mission Scroll. Kill the target monsters (`/summon clovermagic:magic_boar`), watch the action bar, finish: rewards and a toast.
46. Die once with a Devil's Mark or a second grimoire and with stat points in Vitality: the max health must come back after respawning (within 5 seconds).
47. Sounds: all 14 play and are not painfully loud (they are mono, so they get quieter with distance).
48. Squads: `/clover level 10`, take a squad robe in your hand, sneak + right-click Julius: you join. Press H: squad, rank and bonus are shown. Finish missions: points go up; sneak + click the Mission Board for a squad mission.
49. Spell wheel: press Z, point at a spell, click: it casts. Check cooldown shading and the spell name and cost in the middle.
50. Casting pose: cast a spell, both arms go forward for about 0.7 seconds (also visible on other players). If the pose never shows, the arm poses are being overwritten: tell me.
51. Config: set `spellDamageMultiplier` to 2, check damage doubles; set `allowDevilDeals` to false, check the altar refuses contracts.
52. Look at the three NPC skins (Asta, Julius, Orsi). Try dropping a community skin over `npc_asta.png`.
