package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.client.vfx.Ctx;
import com.clovermagic.client.vfx.Gfx;
import com.clovermagic.client.vfx.SpellModels;
import com.clovermagic.vfx.Fx;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Bridge between the pure geometry in client/vfx and Minecraft rendering. The model is first recorded per material, then
 * written out one material at a time (a vertex consumer must not be reused after another render type was requested).
 */
public final class VfxDraw {
    public static final ResourceLocation GLOW_TEX = ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/spell_fx.png");
    private static final ResourceLocation STONE_TEX = ResourceLocation.withDefaultNamespace("textures/block/stone.png");
    private static final ResourceLocation ICE_TEX = ResourceLocation.withDefaultNamespace("textures/block/blue_ice.png");

    private static final Rec[] RECS = {new Rec(), new Rec(), new Rec()};

    private VfxDraw() {
    }

    /** Growable vertex store (reused every frame, the render thread is the only user). */
    private static final class Rec {
        float[] f = new float[8 * 4096];
        int[] col = new int[4096];
        int n;

        void clear() {
            n = 0;
        }

        void add(float x, float y, float z, float nx, float ny, float nz, float u, float v, int argb) {
            if (n == col.length) {
                f = java.util.Arrays.copyOf(f, f.length * 2);
                col = java.util.Arrays.copyOf(col, col.length * 2);
            }
            int i = n * 8;
            f[i] = x;
            f[i + 1] = y;
            f[i + 2] = z;
            f[i + 3] = nx;
            f[i + 4] = ny;
            f[i + 5] = nz;
            f[i + 6] = u;
            f[i + 7] = v;
            col[n++] = argb;
        }
    }

    public static void draw(Fx fx, Ctx ctx, PoseStack ps, MultiBufferSource buffers, int light) {
        for (Rec r : RECS) r.clear();
        Gfx g = new Gfx((mat, x, y, z, nx, ny, nz, u, v, argb) -> RECS[mat.ordinal()].add(x, y, z, nx, ny, nz, u, v, argb));
        SpellModels.draw(fx, g, ctx);
        PoseStack.Pose pose = ps.last();
        for (Gfx.Mat mat : Gfx.Mat.values()) {
            Rec r = RECS[mat.ordinal()];
            if (r.n == 0) continue;
            RenderType type = switch (mat) {
                case GLOW -> RenderType.entityTranslucentEmissive(GLOW_TEX);
                case STONE -> RenderType.entityCutoutNoCull(STONE_TEX);
                case ICE -> RenderType.entityTranslucent(ICE_TEX);
            };
            int l = mat == Gfx.Mat.GLOW ? LightTexture.FULL_BRIGHT : light;
            VertexConsumer vc = buffers.getBuffer(type);
            for (int i = 0; i < r.n; i++) {
                int k = i * 8;
                int c = r.col[i];
                vc.addVertex(pose, r.f[k], r.f[k + 1], r.f[k + 2])
                        .setColor((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF, (c >>> 24) & 0xFF)
                        .setUv(r.f[k + 6], r.f[k + 7])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(l)
                        .setNormal(pose, r.f[k + 3], r.f[k + 4], r.f[k + 5]);
            }
        }
    }
}
