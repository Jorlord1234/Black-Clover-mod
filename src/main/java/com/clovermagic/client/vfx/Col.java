package com.clovermagic.client.vfx;

/** ARGB colour helpers (no Minecraft classes, so the geometry can be unit-tested outside the game). */
public final class Col {
    private Col() {
    }

    public static int argb(int a, int r, int g, int b) {
        return (clamp(a) << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }

    /** Opaque colour from 0xRRGGBB. */
    public static int rgb(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    /** Colour from 0xRRGGBB with alpha 0..1. */
    public static int rgba(int rgb, float alpha) {
        return (clamp(Math.round(alpha * 255f)) << 24) | (rgb & 0xFFFFFF);
    }

    /** Multiplies the existing alpha by f. */
    public static int alpha(int c, float f) {
        int a = clamp(Math.round(((c >>> 24) & 0xFF) * f));
        return (a << 24) | (c & 0xFFFFFF);
    }

    public static int lerp(int c0, int c1, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int a = Math.round(((c0 >>> 24) & 0xFF) * (1 - t) + ((c1 >>> 24) & 0xFF) * t);
        int r = Math.round(((c0 >> 16) & 0xFF) * (1 - t) + ((c1 >> 16) & 0xFF) * t);
        int g = Math.round(((c0 >> 8) & 0xFF) * (1 - t) + ((c1 >> 8) & 0xFF) * t);
        int b = Math.round((c0 & 0xFF) * (1 - t) + (c1 & 0xFF) * t);
        return argb(a, r, g, b);
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }
}
