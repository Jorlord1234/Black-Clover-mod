# What to check in game

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
