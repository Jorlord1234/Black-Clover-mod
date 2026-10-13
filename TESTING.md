# What to check in game (v0.4 prototype)

Nothing below has been run by the author. Please report each line as OK / broken, with `latest.log` for anything that crashes.

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
