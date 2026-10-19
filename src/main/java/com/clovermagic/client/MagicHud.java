package com.clovermagic.client;

import java.util.List;

import com.clovermagic.CloverMagic;
import com.clovermagic.ModItems;
import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.Spell;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

/**
 * Grimoire Mode HUD (inspired by Cursed Fate's combat mode):
 * the hotbar is replaced by a spell bar, number keys cast, with a mana bar above it
 * and a small preview of your real hotbar on the right.
 */
public class MagicHud implements LayeredDraw.Layer {
    private static final int SLOT = 26;
    private static final int GAP = 3;

    public static ResourceLocation icon(Spell spell) {
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID,
                "textures/gui/spell/" + spell.name().toLowerCase() + ".png");
    }

    public static int rgb(MagicAttribute a) {
        return switch (a) {
            case FIRE -> 0xFF6E3C;
            case WATER -> 0x468CFF;
            case WIND -> 0x6EDC96;
            case EARTH -> 0xBE8C50;
            case ICE -> 0xAAE6FF;
            case LIGHTNING -> 0xFFDC3C;
            case LIGHT -> 0xFFF5C8;
            case DARKNESS -> 0xBE64FF;
            case ANTI_MAGIC -> 0x9696A0;
        };
    }

    private static ItemStack grimoireStack() {
        Item item = switch (ClientMagicState.leaves) {
            case 4 -> ModItems.FOUR_LEAF_GRIMOIRE.get();
            case 5 -> ModItems.FIVE_LEAF_GRIMOIRE.get();
            default -> ModItems.GRIMOIRE.get();
        };
        ItemStack stack = new ItemStack(item);
        MagicAttribute a = MagicAttribute.fromId(ClientMagicState.attribute);
        if (a != null && ClientMagicState.leaves != 5) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(a.modelIndex));
        }
        return stack;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (!ClientMagicState.mode || !ClientMagicState.hasGrimoire || mc.player == null || mc.options.hideGui) return;
        MagicAttribute attr = ClientMagicState.attr();
        if (attr == null) return;

        Font font = mc.font;
        int w = g.guiWidth();
        int h = g.guiHeight();
        int color = 0xFF000000 | rgb(attr);
        List<Spell> spells = ClientMagicState.spells();
        int level = ClientMagicState.level();
        int n = spells.size();

        // ---- spell bar (replaces the hotbar)
        int barW = n * SLOT + (n - 1) * GAP;
        int x0 = (w - barW) / 2;
        int y0 = h - SLOT - 4;
        for (int i = 0; i < n; i++) {
            Spell spell = spells.get(i);
            int x = x0 + i * (SLOT + GAP);
            boolean locked = false;
            int border = locked ? 0xFF555555 : color;

            g.fill(x - 1, y0 - 1, x + SLOT + 1, y0 + SLOT + 1, border);
            g.fill(x, y0, x + SLOT, y0 + SLOT, 0xE0101018);
            g.blit(icon(spell), x + 5, y0 + 5, 0f, 0f, 16, 16, 16, 16);

            float cd = locked ? 0f : ClientMagicState.cooldownFraction(i, spell);
            if (cd > 0f) {
                int ch = Math.round(SLOT * cd);
                g.fill(x, y0 + SLOT - ch, x + SLOT, y0 + SLOT, 0xA0FFFFFF);
            }
            if (locked) {
                g.fill(x, y0, x + SLOT, y0 + SLOT, 0xC0000000);
                g.drawString(font, "Lv" + spell.levelNeeded(), x + 3, y0 + SLOT / 2 - 4, 0xFFAAAAAA);
            }
            g.drawString(font, String.valueOf(i + 1), x + 3, y0 + 2, locked ? 0xFF777777 : 0xFFFFFFFF);
        }

        // ---- resource bar above the spell bar
        int bw = Math.max(barW, 110);
        int bx = (w - bw) / 2;
        int by = y0 - 16;
        g.fill(bx - 1, by - 1, bx + bw + 1, by + 7, 0xFF000000);
        g.fill(bx, by, bx + bw, by + 6, 0xFF1A1A26);
        String label;
        if (ClientMagicState.leaves == 5) {
            float food = mc.player.getFoodData().getFoodLevel() / 20f;
            g.fill(bx, by, bx + Math.round(bw * Math.min(1f, food)), by + 6, 0xFFE0903A);
            label = "Anti-Magic  (hunger)";
        } else {
            int max = ClientMagicState.maxMana;
            float frac = max <= 0 ? 0f : Math.min(1f, (float) ClientMagicState.mana / max);
            g.fill(bx, by, bx + Math.round(bw * frac), by + 6, 0xFF3C8CFF);
            label = ClientMagicState.mana + " / " + max;
        }
        g.drawCenteredString(font, label, w / 2, by - 11, 0xFFFFFFFF);

        // ---- grimoire icon, attribute and level on the left of the bar
        int ix = bx - 24;
        g.renderItem(grimoireStack(), ix, by - 8);
        g.drawString(font, attr.displayName, bx, by - 21, color);
        String lvText = "Lv " + level;
        g.drawString(font, lvText, bx + bw - font.width(lvText), by - 21, 0xFFFFD84A);
        // magic xp progress
        int cur = MagicData.xpForLevel(level);
        int next = level >= 10 ? cur : MagicData.xpForLevel(level + 1);
        float xpFrac = next <= cur ? 1f : Math.min(1f, (float) (ClientMagicState.xp - cur) / (next - cur));
        g.fill(bx, by + 8, bx + bw, by + 10, 0xFF000000);
        g.fill(bx, by + 8, bx + Math.round(bw * xpFrac), by + 10, 0xFFFFD84A);

        // ---- small preview of the real hotbar on the right
        Player p = mc.player;
        int px = w - 24;
        int py = h / 2 - 9 * 10;
        for (int i = 0; i < 9; i++) {
            int y = py + i * 20;
            boolean sel = p.getInventory().selected == i;
            g.fill(px - 2, y - 2, px + 18, y + 18, sel ? 0xFFFFFFFF : 0x90000000);
            g.fill(px - 1, y - 1, px + 17, y + 17, 0xC0101018);
            ItemStack s = p.getInventory().getItem(i);
            g.renderItem(s, px, y);
            g.renderItemDecorations(font, s, px, y);
        }
    }
}
