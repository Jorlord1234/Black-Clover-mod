package com.clovermagic.client;

import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.StatLogic;
import com.clovermagic.net.SpendStatPayload;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/** Press H: your rank, level and the four stats. One stat point per magic level above 1. */
public class StatsScreen extends Screen {
    private static final int W = 300;

    public StatsScreen() {
        super(Component.literal("Mage Stats"));
    }

    @Override
    protected void init() {
        int x0 = (this.width - W) / 2;
        int y0 = this.height / 2 - 40;
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            this.addRenderableWidget(Button.builder(Component.literal("+"), b -> PacketDistributor.sendToServer(new SpendStatPayload(idx)))
                    .bounds(x0 + W - 24, y0 + i * 30, 20, 20).build());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int x0 = (this.width - W) / 2;
        int y0 = this.height / 2 - 40;
        g.fill(x0 - 12, y0 - 62, x0 + W + 12, y0 + 150, 0xE0100C14);
        g.renderOutline(x0 - 12, y0 - 62, W + 24, 212, 0xFF5A4A8A);
        MagicAttribute attr = ClientMagicState.attr();
        int rgb = attr == null ? 0xC8C0E0 : MagicHud.rgb(attr);
        int level = ClientMagicState.level();
        g.drawCenteredString(this.font, "MAGE STATS", this.width / 2, y0 - 54, 0xFFFFFF);
        g.drawCenteredString(this.font, StatLogic.rank(level) + "  -  magic level " + level, this.width / 2, y0 - 40, 0xFF000000 | rgb);
        g.drawCenteredString(this.font, "Points to spend: " + ClientMagicState.statPointsLeft(), this.width / 2, y0 - 26, 0xFFE68C);
        for (int i = 0; i < 4; i++) {
            int y = y0 + i * 30;
            g.drawString(this.font, StatLogic.NAMES[i] + ": " + ClientMagicState.stats[i] + " / " + StatLogic.MAX_PER_STAT, x0, y, 0xFFFFFF);
            g.drawString(this.font, StatLogic.EFFECTS[i], x0, y + 11, 0xA8A0C0);
        }
        g.drawCenteredString(this.font, "Stats come from magic levels: one point per level above 1.", this.width / 2, y0 + 128, 0x8880A0);
        super.render(g, mouseX, mouseY, partialTick);
    }
}
