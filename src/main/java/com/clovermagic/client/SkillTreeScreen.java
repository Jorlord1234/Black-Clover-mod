package com.clovermagic.client;

import java.util.ArrayList;
import java.util.List;

import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.Spell;
import com.clovermagic.net.RespecPayload;
import com.clovermagic.net.UnlockPayload;
import com.clovermagic.skill.NodeType;
import com.clovermagic.skill.SkillBranch;
import com.clovermagic.skill.SkillNode;
import com.clovermagic.skill.SkillPoints;
import com.clovermagic.skill.SkillTree;
import com.clovermagic.skill.SkillTrees;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The skill tree screen (K). Left-drag empty space (or right-drag) to pan, mouse wheel to zoom,
 * click an available node to unlock it, click "Reset" on a branch banner to reset that branch.
 * This is the functional version; the ornate frames and glows from the approved design come in the art pass.
 */
public class SkillTreeScreen extends Screen {
    private static final int DESIGN_W = 650;
    private static final int DESIGN_H = 450;
    private enum State { ON, AVAILABLE, NEED_POINTS, LOCKED, SOON }

    private final SkillTree tree;
    private double panX;
    private double panY;
    private double zoom = 1.0;
    private SkillNode hovered;

    public SkillTreeScreen(SkillTree tree) {
        super(Component.literal(tree.title));
        this.tree = tree;
    }

    @Override
    protected void init() {
        this.zoom = Math.max(0.55, Math.min(1.0, (this.height - 80) / (double) (DESIGN_H + 30)));
        this.panX = 0;
        this.panY = 0;
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

    // ------------------------------------------------------------------ coordinates
    private int sx(int x) {
        return (int) Math.round(this.width / 2.0 + (x - DESIGN_W / 2.0) * zoom + panX);
    }

    private int sy(int y) {
        return (int) Math.round(this.height / 2.0 + 8 + (y - DESIGN_H / 2.0) * zoom + panY);
    }

    private static int half(SkillNode n) {
        return switch (n.type()) {
            case KEYSTONE -> 17;
            case ROOT -> 16;
            case SPELL -> 13;
            default -> 9;
        };
    }

    private State stateOf(SkillNode n) {
        if (SkillTrees.isUnlocked(tree, ClientMagicState.unlocked, n.id())) return State.ON;
        SkillBranch b = tree.branch(n.branch());
        if (!n.implemented() || (b != null && b.locked())) return State.SOON;
        if (!SkillTrees.prerequisitesMet(tree, ClientMagicState.unlocked, n, ClientMagicState.level())) return State.LOCKED;
        return ClientMagicState.points >= n.cost() ? State.AVAILABLE : State.NEED_POINTS;
    }

    // ------------------------------------------------------------------ drawing
    private void hline(GuiGraphics g, int x0, int x1, int y, int color) {
        g.fill(Math.min(x0, x1), y - 1, Math.max(x0, x1) + 1, y + 1, color);
    }

    private void vline(GuiGraphics g, int x, int y0, int y1, int color) {
        g.fill(x - 1, Math.min(y0, y1), x + 1, Math.max(y0, y1) + 1, color);
    }

    private void link(GuiGraphics g, SkillNode from, SkillNode to) {
        boolean fromOn = SkillTrees.isUnlocked(tree, ClientMagicState.unlocked, from.id());
        boolean toOn = SkillTrees.isUnlocked(tree, ClientMagicState.unlocked, to.id());
        int color = (fromOn && toOn) ? 0xFFFF8A32 : (fromOn ? 0xFFCFC8B4 : 0xFF4E4A58);
        int ax = sx(from.x()), ay = sy(from.y()), bx = sx(to.x()), by = sy(to.y());
        int mid = (ax + bx) / 2;
        hline(g, ax, mid, ay, color);
        vline(g, mid, ay, by, color);
        hline(g, mid, bx, by, color);
    }

    private static int borderColor(State s, NodeType t) {
        return switch (s) {
            case ON -> 0xFFE8BC3E;
            case AVAILABLE -> 0xFFFFFFFF;
            case NEED_POINTS -> 0xFFB8B4A8;
            case LOCKED -> 0xFF5A5664;
            case SOON -> 0xFF3E3C48;
        };
    }

    private void drawNode(GuiGraphics g, SkillNode n, boolean isHovered) {
        State st = stateOf(n);
        int cx = sx(n.x()), cy = sy(n.y()), h = half(n);
        if (st == State.ON) {
            g.fill(cx - h - 3, cy - h - 3, cx + h + 3, cy + h + 3, 0x66FF7A30);
        }
        int border = borderColor(st, n.type());
        g.fill(cx - h, cy - h, cx + h, cy + h, border);
        g.fill(cx - h + 2, cy - h + 2, cx + h - 2, cy + h - 2, 0xFF18121C);
        if (n.type() == NodeType.KEYSTONE) {
            g.fill(cx - h + 3, cy - h + 3, cx + h - 3, cy + h - 3, border);
            g.fill(cx - h + 4, cy - h + 4, cx + h - 4, cy + h - 4, 0xFF18121C);
        }
        if (n.spellName() != null) {
            try {
                Spell spell = Spell.valueOf(n.spellName());
                g.blit(MagicHud.icon(spell), cx - 8, cy - 8, 0f, 0f, 16, 16, 16, 16);
            } catch (IllegalArgumentException ignored) {
                // no such spell: draw nothing
            }
        } else {
            int c = st == State.ON ? 0xFFFFC878 : (st == State.AVAILABLE ? 0xFFFFFFFF : 0xFF7A7686);
            g.fill(cx - 4, cy - 1, cx + 4, cy + 1, c);
            g.fill(cx - 1, cy - 4, cx + 1, cy + 4, c);
        }
        if (st == State.LOCKED || st == State.SOON) {
            g.fill(cx - h + 2, cy - h + 2, cx + h - 2, cy + h - 2, 0x99000000);
        }
        if (isHovered) {
            g.renderOutline(cx - h - 2, cy - h - 2, h * 2 + 4, h * 2 + 4, 0xFFFFE68C);
        }
        // name under the node
        int labelColor = st == State.ON || st == State.AVAILABLE ? 0xFFF4F0E6 : 0xFF8A8696;
        List<FormattedCharSequence> lines = this.font.split(Component.literal(n.name()), Math.max(54, (int) (84 * zoom)));
        int ty = cy + h + 3;
        for (int i = 0; i < lines.size() && i < 3; i++) {
            int w = this.font.width(lines.get(i));
            g.drawString(this.font, lines.get(i), cx - w / 2, ty + i * 9, labelColor);
        }
    }

    private int[] resetButton(SkillBranch b) {
        // x, y, w, h of the Reset button on the banner of this branch
        int x = b.id().equals("passive") ? sx(4) : sx(210);
        int w = b.id().equals("passive") ? 78 : 330;
        int y = sy(b.y() == 0 ? 100 : b.y() - 40);
        return new int[] {x, y, (int) (w * zoom), 12};
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xF0100C14);
        ClientMagicState.level();

        // banners with a reset button each
        for (SkillBranch b : tree.branches) {
            int[] r = resetButton(b);
            int x0 = r[0], y0 = r[1], x1 = r[0] + r[2], y1 = r[1] + r[3];
            g.fill(x0, y0, x1, y1, b.locked() ? 0xFF3A3A48 : 0xFF7A1A12);
            g.fill(x0, y0, x1, y0 + 1, b.locked() ? 0xFF6A6A78 : 0xFFE8BC3E);
            g.drawString(this.font, b.name() + (b.id().equals("passive") ? "" : "  -  " + b.subtitle()), x0 + 4, y0 + 2,
                    b.locked() ? 0xFFA0A0AE : 0xFFFFE9A0);
            if (!b.locked()) {
                String label = "Reset";
                int w = this.font.width(label) + 6;
                boolean over = mouseX >= x1 - w && mouseX <= x1 && mouseY >= y0 && mouseY <= y1;
                g.fill(x1 - w, y0, x1, y1, over ? 0xFFB0301F : 0xFF4A140E);
                g.drawString(this.font, label, x1 - w + 3, y0 + 2, 0xFFFFFFFF);
            }
        }

        // links first (under the nodes)
        for (SkillNode n : tree.nodes) {
            if (n.requires().isEmpty()) {
                if (n.type() != NodeType.ROOT) link(g, tree.get(tree.rootId), n);
            } else {
                for (String r : n.requires()) link(g, tree.get(r), n);
            }
        }

        // nodes + hover detection
        hovered = null;
        for (SkillNode n : tree.nodes) {
            int cx = sx(n.x()), cy = sy(n.y()), h = half(n);
            if (mouseX >= cx - h && mouseX <= cx + h && mouseY >= cy - h && mouseY <= cy + h) hovered = n;
        }
        for (SkillNode n : tree.nodes) drawNode(g, n, n == hovered);

        // header
        g.drawString(this.font, tree.title, 14, 10, 0xFFE8BC3E);
        g.drawString(this.font, "Skill tree", 14, 22, 0xFFB4AAA0);
        String pts = "Skill points: " + ClientMagicState.points;
        g.drawString(this.font, pts, this.width - 14 - this.font.width(pts), 10, 0xFFFFD84A);
        int level = ClientMagicState.level();
        String lv = "Magic level " + level + " / " + ClientMagicState.maxLevel;
        g.drawString(this.font, lv, this.width - 14 - this.font.width(lv), 22, 0xFFEBE1D2);
        int cur = MagicData.xpForLevel(level);
        int next = level >= ClientMagicState.maxLevel ? cur : MagicData.xpForLevel(level + 1);
        float frac = next <= cur ? 1f : Math.min(1f, (float) (ClientMagicState.xp - cur) / (next - cur));
        int bw = 90, bx = this.width - 14 - bw;
        g.fill(bx - 1, 33, bx + bw + 1, 38, 0xFF000000);
        g.fill(bx, 34, bx + Math.round(bw * frac), 37, 0xFFFFD84A);
        g.drawCenteredString(this.font, "Left-drag: move   Wheel: zoom   Click: unlock   K: close", this.width / 2, this.height - 12, 0xFF8A8696);

        if (hovered != null) renderNodeTooltip(g, hovered, mouseX, mouseY);
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void renderNodeTooltip(GuiGraphics g, SkillNode n, int mouseX, int mouseY) {
        State st = stateOf(n);
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(n.name()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        String type = switch (n.type()) {
            case ROOT -> "Root";
            case SPELL -> "Spell";
            case UPGRADE -> "Upgrade";
            case PASSIVE -> "Passive";
            case KEYSTONE -> "Keystone";
        };
        if (n.type() == NodeType.ROOT) {
            lines.add(Component.literal(type + "  -  free").withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add(Component.literal(type + "  -  cost " + n.cost() + (n.cost() == 1 ? " point" : " points")).withStyle(ChatFormatting.YELLOW));
            int level = ClientMagicState.level();
            lines.add(Component.literal((level >= n.minLevel() ? "v " : "x ") + "Magic level " + n.minLevel())
                    .withStyle(level >= n.minLevel() ? ChatFormatting.GREEN : ChatFormatting.RED));
            for (String r : n.requires()) {
                SkillNode rn = tree.get(r);
                boolean ok = SkillTrees.isUnlocked(tree, ClientMagicState.unlocked, r);
                lines.add(Component.literal((ok ? "v " : "x ") + (rn == null ? r : rn.name()))
                        .withStyle(ok ? ChatFormatting.GREEN : ChatFormatting.RED));
            }
        }
        lines.add(Component.literal(" "));
        for (String part : wrap(n.description(), 44)) lines.add(Component.literal(part).withStyle(ChatFormatting.WHITE));
        lines.add(Component.literal(" "));
        lines.add(Component.literal(n.canon()).withStyle(ChatFormatting.DARK_GRAY));
        switch (st) {
            case AVAILABLE -> lines.add(Component.literal("Click to unlock").withStyle(ChatFormatting.GREEN));
            case NEED_POINTS -> lines.add(Component.literal("Not enough skill points").withStyle(ChatFormatting.RED));
            case SOON -> lines.add(Component.literal("Coming in a later version").withStyle(ChatFormatting.GRAY));
            default -> { }
        }
        g.renderComponentTooltip(this.font, lines, mouseX, mouseY);
    }

    private static List<String> wrap(String text, int width) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            if (cur.length() + word.length() + 1 > width && cur.length() > 0) {
                out.add(cur.toString());
                cur.setLength(0);
            }
            if (cur.length() > 0) cur.append(' ');
            cur.append(word);
        }
        if (cur.length() > 0) out.add(cur.toString());
        return out;
    }

    // ------------------------------------------------------------------ input
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            for (SkillBranch b : tree.branches) {
                if (b.locked()) continue;
                int[] r = resetButton(b);
                int w = this.font.width("Reset") + 6;
                int x1 = r[0] + r[2];
                if (mx >= x1 - w && mx <= x1 && my >= r[1] && my <= r[1] + r[3]) {
                    PacketDistributor.sendToServer(new RespecPayload(b.id()));
                    return true;
                }
            }
            if (hovered != null && stateOf(hovered) == State.AVAILABLE) {
                PacketDistributor.sendToServer(new UnlockPayload(hovered.id()));
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (hovered == null || button != 0) {
            panX += dx;
            panY += dy;
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        zoom = Math.max(0.5, Math.min(1.6, zoom + (scrollY > 0 ? 0.1 : -0.1)));
        return true;
    }
}
