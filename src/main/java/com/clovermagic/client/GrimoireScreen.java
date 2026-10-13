package com.clovermagic.client;

import java.util.List;

import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.Spell;
import com.clovermagic.magic.SpellInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Read-only spell list (K): shows each spell, its cost, unlock level and what it does. */
public class GrimoireScreen extends Screen {
    public GrimoireScreen() {
        super(Component.literal("Grimoire"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ClientSetup.INFO.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xC0101018);
        int cx = this.width / 2;
        int panelW = Math.min(300, this.width - 20);
        int left = cx - panelW / 2;
        int y = 20;

        MagicAttribute attr = ClientMagicState.attr();
        if (!ClientMagicState.hasGrimoire || attr == null) {
            g.drawCenteredString(this.font, "You have no grimoire. Visit a Grimoire Tower altar.", cx, y, 0xFFFFFFFF);
            super.render(g, mouseX, mouseY, partialTick);
            return;
        }

        int color = 0xFF000000 | MagicHud.rgb(attr);
        g.drawCenteredString(this.font, attr.displayName + " Magic", cx, y, color);
        y += 14;
        int level = ClientMagicState.level();
        int cur = MagicData.xpForLevel(level);
        int next = level >= 10 ? cur : MagicData.xpForLevel(level + 1);
        String xpText = level >= 10 ? "Magic level 10 (max)"
                : "Magic level " + level + "  -  " + (ClientMagicState.xp - cur) + " / " + (next - cur) + " xp";
        g.drawCenteredString(this.font, xpText, cx, y, 0xFFFFD84A);
        y += 20;

        List<Spell> spells = ClientMagicState.spells();
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            boolean locked = s.levelNeeded() > level;
            g.fill(left - 1, y - 1, left + panelW + 1, y + 47, locked ? 0xFF444444 : color);
            g.fill(left, y, left + panelW, y + 46, 0xF0181822);
            g.blit(MagicHud.icon(s), left + 6, y + 6, 0f, 0f, 16, 16, 16, 16);
            g.drawString(this.font, String.valueOf(i + 1), left + 10, y + 28, 0xFFAAAAAA);
            String head = s.displayName + (locked ? "   (unlocks at level " + s.levelNeeded() + ")"
                    : "   -   cost " + s.cost);
            g.drawString(this.font, head, left + 30, y + 5, locked ? 0xFF888888 : 0xFFFFFFFF);
            List<FormattedCharSequence> lines = this.font.split(Component.literal(SpellInfo.describe(s)), panelW - 40);
            for (int k = 0; k < lines.size() && k < 3; k++) {
                g.drawString(this.font, lines.get(k), left + 30, y + 18 + k * 10, locked ? 0xFF777777 : 0xFFCCCCCC);
            }
            y += 52;
        }
        g.drawCenteredString(this.font, "G: Grimoire Mode   1-" + spells.size() + ": cast   K: close", cx, y + 6, 0xFF999999);
        super.render(g, mouseX, mouseY, partialTick);
    }
}
