package com.clovermagic.client;

import java.util.List;

import com.clovermagic.magic.Spell;
import com.clovermagic.net.CastPayload;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The spell wheel (key Z): all your spells in a circle. Move the mouse towards a spell and click to cast it.
 * Works without Grimoire Mode and has no limit of nine spells.
 */
public class SpellWheelScreen extends Screen {
    public SpellWheelScreen() {
        super(Component.translatableWithFallback("msg.clovermagic.spells_1949", "Spells"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    private int hovered(int mouseX, int mouseY, int n) {
        double dx = mouseX - this.width / 2.0, dy = mouseY - this.height / 2.0;
        if (Math.sqrt(dx * dx + dy * dy) < 28 || n == 0) return -1;
        double ang = Math.atan2(dy, dx) + Math.PI / 2 + Math.PI / n;   // 0 = straight up
        ang = (ang % (Math.PI * 2) + Math.PI * 2) % (Math.PI * 2);
        return (int) (ang / (Math.PI * 2 / n)) % n;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        List<Spell> spells = ClientMagicState.spells();
        int n = spells.size();
        int cx = this.width / 2, cy = this.height / 2;
        int rgb = ClientMagicState.attr() == null ? 0xC8C0E0 : MagicHud.rgb(ClientMagicState.attr());
        g.fill(0, 0, this.width, this.height, 0x90060410);
        int hov = hovered(mouseX, mouseY, n);
        double radius = Math.min(110, 34 + n * 7.5);
        for (int i = 0; i < n; i++) {
            double ang = -Math.PI / 2 + i * Math.PI * 2 / n;
            int x = cx + (int) Math.round(Math.cos(ang) * radius);
            int y = cy + (int) Math.round(Math.sin(ang) * radius);
            boolean sel = i == hov;
            int size = sel ? 26 : 20;
            g.fill(x - size / 2 - 2, y - size / 2 - 2, x + size / 2 + 2, y + size / 2 + 2, sel ? (0xFF000000 | rgb) : 0xFF3A3450);
            g.fill(x - size / 2, y - size / 2, x + size / 2, y + size / 2, 0xFF100C18);
            RenderSystem.enableBlend();
            g.blit(MagicHud.icon(spells.get(i)), x - size / 2, y - size / 2, size, size, 0f, 0f, 16, 16, 16, 16);
            float cd = ClientMagicState.cooldownFraction(i, spells.get(i));
            if (cd > 0) g.fill(x - size / 2, y + size / 2 - (int) (size * cd), x + size / 2, y + size / 2, 0xA0000000);
        }
        g.drawCenteredString(this.font, "SPELL WHEEL", cx, cy - 12, 0xFFFFFF);
        if (hov >= 0) {
            Spell s = spells.get(hov);
            g.drawCenteredString(this.font, s.displayName, cx, cy, 0xFF000000 | rgb);
            g.drawCenteredString(this.font, s.cost + " mana  -  " + Mth.floor(s.cooldown / 20.0) + "s", cx, cy + 12, 0xB8B0D0);
        } else {
            g.drawCenteredString(this.font, "Point at a spell and click", cx, cy + 4, 0x9088B0);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int n = ClientMagicState.spells().size();
        int hov = hovered((int) mx, (int) my, n);
        if (hov >= 0 && button == 0) {
            PacketDistributor.sendToServer(new CastPayload(hov));
            this.onClose();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }
}
