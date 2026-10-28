# Art bible

**The rule: design outside Minecraft first, translate into Minecraft second.**
For every character, creature and place: (1) write down what it looks like in the series (silhouette, colors, signature details),
(2) pick a palette, (3) only then decide how to build it from Minecraft pieces (skin layout, model parts, blocks).
Palettes are in `docs/art/palettes.png`.

## Using community skins (recommended for the NPCs)
The NPC skins are plain 64x64 PNGs in the standard Minecraft skin layout (wide arms), in
`src/main/resources/assets/clovermagic/textures/entity/`: `npc_asta.png`, `npc_julius.png`, `npc_orsi.png`.
Download an Asta or Julius skin from a skin site, **check the creator's license and credit them**, rename it to the file name above and drop it in.
It works as is, because the NPCs use the normal player model, including the second skin layer (cape, hair, mantle).

## Characters

### Asta
- **In the series:** short, muscular, ash-blond messy hair with one upright strand, green eyes, black headband with the gold Black Bull emblem (red star on the back of the band),
  white tunic with a V collar, dark blue jacket with short sleeves, dark blue torn pants, brown belt, black boots with white toes. Black Bull robe: a short black mantle with gold trim.
- **Minecraft translation:** hair on the head layer with a few spikes on the hat layer, headband as a black row across the head, jacket as dark blue columns on the body, mantle on the second body layer.
- **Status:** skin built from this design (v0.11).

### Julius Novachrono (Wizard King)
- **In the series:** calm, smiling, pale silver hair, white and gold Wizard King robe and cloak; his magic is Time.
- **Minecraft translation:** silver hair that runs down the back of the head on the hat layer, white robe with gold collar and cuffs, a blue time-star on the cloak (second body layer), gold belt buckle.
- **Status:** skin built from this design (v0.11).

### Father Orsi
- **In the series:** the kind priest of the church where Asta and Yuno grew up, a father figure to them. **Exact looks not confirmed: check a reference and adjust.**
- **Current guess:** short dark hair, light stubble, black cassock with a white collar tab and a gold cross.

### Midnight Sun Mage
- **Concept:** a cultist of the Eye of the Midnight Sun: hooded dark robe, glowing orange eyes, a sun mark on the chest.
### Devils (Megicula, Lucifugus, Zagred) and the Demon
- **Concept:** devils are intelligent and elegant (long coats, horns, glowing eyes, each with an accent color: purple, gold, red); a demon is a huge, raw, cracked red-black creature with glowing orange seams.
- **Minecraft translation:** skins on the player model with scale (devils 1.1x, Zagred 1.5x, demon 1.9x). Horns and wings need real 3D parts: the first thing to build with a 3D tool.

## Places
- **Clover Church:** warm stone and spruce, red carpet, gold cross on the altar wall, lanterns.
- **Black Bull Hideout:** a crumbling dark deepslate building, lived in: crafting table, furnace, training targets.
- **Yultim Dungeon:** mossy and cracked stone bricks, cobwebs, soul lanterns, a boss hall.
