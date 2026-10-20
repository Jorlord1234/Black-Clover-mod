package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.item.GrimoireItem;
import com.clovermagic.magic.MagicAttribute;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

/**
 * The first-grimoire cutscene (about 8 seconds, any click after 2 seconds or ESC skips it):
 * the screen darkens, grimoires circle faster and faster, all but one fade away, a flash in the element's color,
 * then the chosen grimoire is revealed with its name. Pure GUI drawing, no extra assets besides the item textures.
 */
public class GrimoireCutsceneScreen extends Screen {
    private static final int TOTAL = 170;
    private static final int BOOKS = 7;

    private final MagicAttribute attr;
    private final int leaves;
    private final ResourceLocation chosen;
    private final ResourceLocation[] others = new ResourceLocation[3];
    private int ticks = 0;

    public GrimoireCutsceneScreen(MagicAttribute attr, int leaves) {
        super(Component.literal("Grimoire"));
        this.attr = attr;
        this.leaves = leaves;
        String name = leaves == 5 ? "five_leaf" : (leaves == 4 ? "four_leaf_" : "three_leaf_") + attr.id;
        this.chosen = tex(name);
        this.others[0] = tex("three_leaf");
        this.others[1] = tex("four_leaf");
        this.others[2] = tex("five_leaf");
    }

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/item/" + name + ".png");
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (ticks > 40) {
            this.onClose();
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public void tick() {
        ticks++;
        if (this.minecraft == null) return;
        if (ticks == 2) play(SoundEvents.ENCHANTMENT_TABLE_USE, 0.7f);
        if (ticks == 30) play(SoundEvents.BEACON_ACTIVATE, 1.4f);
        if (ticks == 62) play(SoundEvents.ENCHANTMENT_TABLE_USE, 1.4f);
        if (ticks == 78) {
            play(SoundEvents.PLAYER_LEVELUP, 0.9f);
            play(SoundEvents.AMETHYST_BLOCK_RESONATE, 0.8f);
            if (leaves == 5) play(SoundEvents.WITHER_SPAWN, 1.6f);
        }
        if (ticks >= TOTAL) this.onClose();
    }

    private void play(net.minecraft.sounds.SoundEvent sound, float pitch) {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, 0.8f));
    }

    private static int argb(float alpha, int rgb) {
        int a = Mth.clamp((int) (alpha * 255), 0, 255);
        return (a << 24) | (rgb & 0xFFFFFF);
    }

    private void book(GuiGraphics g, ResourceLocation tex, float cx, float cy, float size, float alpha, float rot) {
        if (alpha <= 0.01f) return;
        g.pose().pushPose();
        g.pose().translate(cx, cy, 0);
        g.pose().mulPose(Axis.ZP.rotationDegrees(rot));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        g.blit(tex, Math.round(-size / 2), Math.round(-size / 2), Math.round(size), Math.round(size), 0f, 0f, 16, 16, 16, 16);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        g.pose().popPose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        float time = ticks + partialTick;
        int rgb = MagicHud.rgb(attr);
        float cx = this.width / 2f, cy = this.height / 2f - 10;

        // dark backdrop: fades in, and out at the end
        float fadeIn = Mth.clamp(time / 15f, 0f, 1f);
        float fadeOut = Mth.clamp((TOTAL - time) / 20f, 0f, 1f);
        float master = Math.min(fadeIn, fadeOut);
        g.fill(0, 0, this.width, this.height, argb(0.92f * master, 0x06040C));

        // slow rotating rays behind everything once the magic gathers
        if (time > 30) {
            float rayA = Mth.clamp((time - 30) / 40f, 0f, 1f) * 0.22f * master;
            g.pose().pushPose();
            g.pose().translate(cx, cy, 0);
            g.pose().mulPose(Axis.ZP.rotationDegrees(time * 1.5f));
            for (int k = 0; k < 12; k++) {
                g.fill(-3, 0, 3, Math.max(this.width, this.height), argb(rayA, rgb));
                g.pose().mulPose(Axis.ZP.rotationDegrees(30f));
            }
            g.pose().popPose();
        }

        // sparkles drifting upward
        for (int i = 0; i < 40; i++) {
            float seed = i * 12.9898f;
            float px = (Mth.frac(Mth.sin(seed) * 43758.5453f)) * this.width;
            float speed = 0.3f + Mth.frac(Mth.sin(seed * 1.7f) * 9631.12f) * 0.8f;
            float py = this.height - ((time * speed * 3f + Mth.frac(Mth.sin(seed * 3.1f) * 2345.6f) * this.height) % this.height);
            float tw = 0.4f + 0.6f * Math.abs(Mth.sin(time * 0.15f + i));
            g.fill((int) px, (int) py, (int) px + 2, (int) py + 2, argb(tw * 0.8f * master * Mth.clamp(time / 40f, 0f, 1f), i % 2 == 0 ? rgb : 0xFFFFFF));
        }

        // the circling grimoires (index 0 is the one that chooses you)
        float gather = Mth.clamp((time - 55f) / 25f, 0f, 1f);           // 55..80: they close in on the center
        float eased = gather * gather * (3 - 2 * gather);
        for (int i = BOOKS - 1; i >= 0; i--) {
            float spin = time * 0.05f * (1f + time / 45f);
            float ang = (float) (i * Math.PI * 2 / BOOKS) + spin;
            float radius = Mth.lerp(eased, 95f + 8f * Mth.sin(time * 0.1f + i), 0f);
            float bx = cx + Mth.cos(ang) * radius * 1.4f;
            float by = cy + Mth.sin(ang) * radius * 0.7f;
            float appear = Mth.clamp((time - 8f - i * 3f) / 14f, 0f, 1f);
            if (i == 0) {
                // the chosen one: orbits, then settles in the center and grows
                float grow = Mth.clamp((time - 78f) / 20f, 0f, 1f);
                float size = Mth.lerp(grow * grow, 40f, 110f);
                float bob = time > 98 ? Mth.sin(time * 0.12f) * 3f : 0f;
                book(g, chosen, bx, by + bob, size, appear * master, 0f);
            } else {
                float fade = 1f - Mth.clamp((time - 62f) / 16f, 0f, 1f);
                book(g, others[i % 3], bx, by, 36f, appear * fade * master, Mth.sin(time * 0.1f + i) * 12f);
            }
        }

        // flash and expanding ring when the grimoire chooses you
        if (time >= 78f && time < 100f) {
            float f = 1f - (time - 78f) / 22f;
            g.fill(0, 0, this.width, this.height, argb(f * 0.8f, rgb));
            g.fill(0, 0, this.width, this.height, argb(f * 0.4f, 0xFFFFFF));
        }
        if (time >= 78f) {
            float r = (time - 78f) * 7f;
            float ra = Mth.clamp(1f - (time - 78f) / 40f, 0f, 1f) * master;
            g.pose().pushPose();
            g.pose().translate(cx, cy, 0);
            for (int k = 0; k < 48; k++) {
                g.pose().pushPose();
                g.pose().mulPose(Axis.ZP.rotationDegrees(k * 7.5f));
                g.fill(Math.round(r), -1, Math.round(r) + 6, 2, argb(ra, rgb));
                g.pose().popPose();
            }
            g.pose().popPose();
        }

        // the reveal: name of the grimoire and the attribute
        if (time >= 96f) {
            float a = Mth.clamp((time - 96f) / 14f, 0f, 1f) * master;
            if (a * 255 >= 5) {
                g.pose().pushPose();
                g.pose().translate(cx, cy + 74, 0);
                g.pose().scale(2.6f, 2.6f, 1f);
                g.drawCenteredString(this.font, GrimoireItem.leafName(leaves), 0, 0, argb(a, 0xFFFFFF));
                g.pose().popPose();
            }
            float a2 = Mth.clamp((time - 108f) / 14f, 0f, 1f) * master;
            if (a2 * 255 >= 5) {
                String line = attr == MagicAttribute.ANTI_MAGIC ? "Anti-Magic" : attr.displayName + " Magic";
                g.pose().pushPose();
                g.pose().translate(cx, cy + 102, 0);
                g.pose().scale(1.8f, 1.8f, 1f);
                g.drawCenteredString(this.font, line, 0, 0, argb(a2, rgb));
                g.pose().popPose();
                if (leaves == 5) {
                    g.drawCenteredString(this.font, "The rarest of all grimoires.", Math.round(cx), Math.round(cy + 126), argb(a2, 0xB8B0C8));
                }
            }
        } else if (time > 12f && time < 78f) {
            float a = Mth.clamp((time - 12f) / 14f, 0f, 1f) * (1f - Mth.clamp((time - 62f) / 14f, 0f, 1f)) * master;
            if (a * 255 >= 5) {
                g.drawCenteredString(this.font, "The grimoires awaken...", Math.round(cx), Math.round(cy + 110), argb(a, 0xD8D0F0));
            }
        }

        if (time > 40f) {
            g.drawCenteredString(this.font, "Click to skip", Math.round(cx), this.height - 14, argb(0.5f * master, 0xFFFFFF));
        }
    }
}
