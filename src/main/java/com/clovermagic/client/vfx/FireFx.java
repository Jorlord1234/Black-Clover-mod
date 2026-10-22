package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Fire spells (Crimson Lion style: gold core, orange flame, red edge). */
final class FireFx {
    private FireFx() {
    }

    static final int WHITE = Col.rgb(0xFFFFFF), CORE = Col.rgb(0xFFF0A0), YEL = Col.rgb(0xFFC830),
            ORA = Col.rgb(0xFF7A10), RED = Col.rgb(0xD02A0A);

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case FLAME_BULLET -> flameBullet(g, c);
            case EXPLODING_FIREBALL -> muzzle(g, c, 1.0f, 1);
            case EXPLODING_BUCKSHOT -> muzzle(g, c, 0.8f, 5);
            case SPIRAL_FLAME -> spiralFlame(g, c);
            case SOL_LINEA -> solLinea(g, c);
            case LEO_RUGIENS -> leoRugiens(g, c);
            case LEO_PALMA -> leoPalma(g, c);
            case IGNIS_COLUMNA -> ignisColumna(g, c);
            case FIREBALL_PROJECTILE -> {
                g.aim(c);
                Parts.fireOrb(g, 0.5 * c.len, c.t, c.seed, 2.4 * c.len);
            }
            case FIREBALL_BURST -> fireballBurst(g, c);
            case CALIDUS_BRACHIUM -> calidusBrachium(g, c);
            case CALIDUS_BRACHIUM_BARRAGE -> calidusBarrage(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void flameBullet(Gfx g, Ctx c) {
        float travel = Math.min(1f, c.t / 4f);
        double z = 0.2 + (c.len - 0.2) * travel;
        g.alpha = c.fade(0, 5);
        g.aim(c);
        g.rod(0, 0, 0.1, 0, 0, z, 0.03, 0.09, Col.rgba(0xFF8010, 0f), Col.rgba(0xFFC040, 0.8f));
        g.push();
        g.translate(0, 0, z);
        Parts.fireOrb(g, 0.22, c.t, c.seed, 1.3);
        g.pop();
        // impact flash
        if (travel >= 1f) {
            float k = (c.t - 4f) / 6f;
            g.push();
            g.translate(0, 0, c.len);
            g.sphere(0.2 + 0.6 * k, 6, 10, Col.rgba(0xFFB030, 0.7f * (1 - k)), Col.rgba(0xFFE080, 0.7f * (1 - k)));
            g.pop();
        }
    }

    /** Magic circle floating in front of the caster's hand. n>1 adds small satellites (buckshot). */
    private static void muzzle(Gfx g, Ctx c, float size, int n) {
        float k = c.fade(2, 7);
        g.alpha = k;
        g.aim(c);
        g.translate(0, 0, 0.5);
        g.rotX(PI / 2);   // circle plane faces forward
        Parts.circle(g, 0.65 * size * (0.6 + 0.4 * Math.min(1, c.t / 3)), c.t * 0.25, ORA, CORE);
        if (n > 1) {
            for (int i = 0; i < n; i++) {
                double a = PI * 2 * i / n + c.t * 0.3;
                g.push();
                g.translate(Math.cos(a) * 0.9, 0.02, Math.sin(a) * 0.9);
                g.sphere(0.1, 4, 6, ORA, CORE);
                g.pop();
            }
        }
    }

    private static void spiralFlame(Gfx g, Ctx c) {
        float prog = Math.min(1f, c.t / 5f);
        double L = c.len * prog;
        g.alpha = c.fade(0, 6);
        g.aim(c);
        int strands = 3;
        for (int s = 0; s < strands; s++) {
            double off = PI * 2 * s / strands + c.t * 0.5;
            double px = 0, py = 0, pz = 0.3;
            for (double d = 0.6; d <= L; d += 0.3) {
                double r = 0.15 + d * 0.07;
                double a = off + d * 1.9;
                double x = Math.cos(a) * r, y = Math.sin(a) * r;
                float f = (float) (d / Math.max(L, 0.1));
                g.rod(px, py, pz, x, y, d, 0.1 + 0.05 * f, 0.1 + 0.05 * f, Col.lerp(CORE, ORA, f), Col.lerp(CORE, ORA, f));
                px = x;
                py = y;
                pz = d;
            }
        }
        g.frustumZ(L, 0.07, 0.35 + L * 0.03, 8, Col.rgba(0xFFE070, 0.55f), Col.rgba(0xFF5000, 0.12f), 0);
    }

    private static void solLinea(Gfx g, Ctx c) {
        g.alpha = c.fade(1, 6);
        g.aim(c);
        double L = c.len;
        double w = 0.12 + 0.05 * Math.sin(c.t * 1.3);
        g.frustumZ(L, w * 0.9, w * 0.9, 6, WHITE, WHITE, 3);
        g.frustumZ(L, w * 2.2, w * 2.2, 8, Col.rgba(0xFFD040, 0.55f), Col.rgba(0xFFA020, 0.55f), 3);
        g.frustumZ(L, w * 4.0, w * 4.0, 8, Col.rgba(0xFF6010, 0.2f), Col.rgba(0xFF3000, 0.2f), 0);
        // sun rings sliding down the beam
        for (int i = 0; i < 5; i++) {
            double z = ((c.t * 2.5 + i * L / 5) % L);
            g.push();
            g.translate(0, 0, z);
            g.rotX(PI / 2);
            g.torus(w * 4.5, w * 0.35, 14, 4, Col.rgba(0xFFD040, 0.85f));
            g.pop();
        }
        g.push();
        g.rotX(PI / 2);
        Parts.circle(g, 0.6, c.t * 0.3, ORA, CORE);
        g.pop();
        g.push();
        g.translate(0, 0, L);
        g.sphere(0.35, 6, 10, Col.rgba(0xFFFFFF, 0.9f), Col.rgba(0xFFE070, 0.9f));
        g.pop();
    }

    /** Leo Rugiens after the anime: a giant fire beast head with wide-open fanged jaws and a blazing sun of fire in its mouth. */
    private static void leoRugiens(Gfx g, Ctx c) {
        float grow = (float) Parts.ease(c.t / 5.0);
        g.alpha = c.fade(0, 7);
        g.aim(c);
        double L = c.len;
        double zh = 2.2 + 1.4 * grow;
        // the blast pouring out of the mouth
        g.push();
        g.translate(0, -0.15, zh + 1.0);
        double jet = Math.max(0.5, (L - zh) * grow);
        g.frustumZ(jet, 0.5, 1.6, 12, Col.rgba(0xFFF0A0, 0.8f), Col.rgba(0xFF6010, 0.0f), 0);
        g.frustumZ(jet * 0.8, 0.8, 2.3, 12, Col.rgba(0xFFB030, 0.4f), Col.rgba(0xFF4000, 0.0f), 0);
        g.pop();
        g.push();
        g.translate(0, 0, zh);
        g.scale(1.1 + 0.4 * grow);
        fireBeastHead(g, c.t, c.seed, (float) (0.55 + 0.1 * Math.sin(c.t * 0.6)));
        g.pop();
    }

    /** Fire beast head looking toward +Z: orange body, open jaws, rows of fangs, big slit eye, flame mane, sun in the mouth. */
    static void fireBeastHead(Gfx g, float t, long seed, float open) {
        int body = Col.rgba(0xFF8A10, 0.88f), top = Col.rgba(0xFFC030, 0.88f), dark = Col.rgba(0xC02A08, 0.9f);
        g.push();
        g.scale(1.0, 0.9, 1.15);
        g.sphere(0.8, 7, 12, dark, top);
        g.pop();
        // upper jaw / snout
        g.push();
        g.translate(0, 0.08, 0.45);
        g.rotX(-0.12);
        g.scale(1.0, 0.65, 1.0);
        g.frustumZ(1.6, 0.55, 0.32, 8, body, top, 0);
        g.pop();
        // lower jaw dropped open
        g.push();
        g.translate(0, -0.25, 0.25);
        g.rotX(open);
        g.scale(1.0, 0.5, 1.0);
        g.frustumZ(1.35, 0.46, 0.25, 8, dark, body, 0);
        g.pop();
        for (int i = 0; i < 7; i++) {
            for (int s = -1; s <= 1; s += 2) {
                double z = 0.7 + i * 0.2;
                g.push();
                g.translate(s * (0.3 - i * 0.015), -0.05, z);
                g.rotX(PI);
                g.coneY(0.26 + 0.06 * (i % 2), 0.05, 4, Col.rgb(0xFFFFF0), Col.rgb(0xFFF0B0));
                g.pop();
                g.push();
                g.translate(s * (0.26 - i * 0.012), -0.28 - open * 0.8 * (z / 1.4) * 0.5, 0.3 + z * 0.8);
                g.coneY(0.24 + 0.05 * (i % 2), 0.045, 4, Col.rgb(0xFFFFF0), Col.rgb(0xFFF0B0));
                g.pop();
            }
        }
        // eyes
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.5, 0.32, 0.5);
            g.scale(0.55, 0.8, 1.2);
            g.sphere(0.2, 5, 8, Col.rgb(0xFFE070), Col.rgb(0xFFFFB0));
            g.pop();
            g.box(s * 0.5 - 0.02, 0.22, 0.45, s * 0.5 + 0.02, 0.42, 0.7, Col.rgb(0xC02000));
        }
        // blazing sun of fire in the mouth
        g.push();
        g.translate(0, -0.25, 1.15);
        double pulse = 1.0 + 0.06 * Math.sin(t * 0.8);
        g.sphere(0.55 * pulse, 6, 10, Col.rgb(0xFFFFFF), Col.rgb(0xFFF8B0));
        g.sphere(0.8 * pulse, 6, 10, Col.rgba(0xFFD040, 0.55f), Col.rgba(0xFFE880, 0.55f));
        g.pop();
        // flame mane streaming back
        for (int i = 0; i < 14; i++) {
            double a = PI * 2 * i / 14;
            g.push();
            g.translate(Math.cos(a) * 0.55, Math.sin(a) * 0.5, -0.3);
            g.rotX(PI - 0.2);
            g.rotZ(a);
            g.coneZ(0.9 + 0.5 * Rnd.h(seed, i) + 0.15 * Math.sin(t * 0.8 + i), 0.16, 4, Col.rgba(0xFF8A10, 0.85f), Col.rgba(0xC01800, 0f));
            g.pop();
        }
    }



    /** One giant flame hand: palm, four curling fingers, thumb. Fingers point up (+Y) and curl toward -X. */
    private static void flameHand(Gfx g, float t, double curl, long seed) {
        g.push();
        g.scale(1.0, 0.45, 0.9);
        g.sphere(0.72, 6, 10, Col.rgba(0xE03008, 0.9f), Col.rgba(0xFFB030, 0.9f));
        g.pop();
        for (int i = 0; i < 4; i++) {
            double z = -0.42 + i * 0.28;
            double[] segLen = {0.62, 0.5, 0.38};
            g.push();
            g.translate(0.0, 0.2, z);
            for (int k = 0; k < 3; k++) {
                g.rotZ(curl * (0.35 + 0.25 * k) + 0.04 * Math.sin(t * 0.3 + i));
                double w = 0.19 - 0.02 * k;
                g.frustumY(segLen[k], w, w * 0.85, 6, Col.rgba(0xFF6010, 0.88f), Col.rgba(0xFFC040, 0.88f), 0);
                g.push();
                g.translate(0, segLen[k], 0);
                g.sphere(w * 0.9, 4, 6, Col.rgba(0xFFA020, 0.9f), Col.rgba(0xFFD860, 0.9f));
                g.pop();
                g.translate(0, segLen[k], 0);
            }
            Parts.flame(g, 0.55, 0.12, t, i + (int) (seed % 7), Col.rgba(0xFFC040, 0.85f), Col.rgba(0xFF3000, 0f));
            g.pop();
        }
        g.push();
        g.translate(0.55, 0.05, 0.0);
        g.rotZ(-0.9 + curl * 0.3);
        g.rotX(0.0);
        for (int k = 0; k < 2; k++) {
            g.frustumY(0.45, 0.19 - 0.03 * k, 0.16 - 0.03 * k, 6, Col.rgba(0xFF6010, 0.88f), Col.rgba(0xFFC040, 0.88f), 0);
            g.translate(0, 0.45, 0);
            g.rotZ(curl * 0.4);
        }
        g.pop();
    }

    /** Leo Palma after the anime: two huge hands of fire rise from the ground around the target and close around it. */
    private static void leoPalma(Gfx g, Ctx c) {
        g.alpha = c.fade(2, 8);
        double sc = Math.max(0.9, c.fh * 0.55);
        double rise = Parts.ease(c.t / 5.0);
        double curl = Parts.ease((c.t - 1.0) / 6.0) * 1.15;
        double off = c.fw * 0.5 + 1.1 * sc;
        for (int side = 0; side < 2; side++) {
            g.push();
            g.rotY(side == 0 ? 0.0 : PI);
            g.translate(-off, -1.6 * (1 - rise), 0);
            g.rotZ(-0.2);
            g.scale(sc);
            flameHand(g, c.t, curl, c.seed + side);
            g.pop();
        }
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8 + c.t * 0.2;
            g.push();
            g.translate(Math.cos(a) * (off + 0.2), 0, Math.sin(a) * (off + 0.2));
            Parts.flame(g, 1.1 * rise, 0.2, c.t, i, Col.rgba(0xFF9010, 0.85f), Col.rgba(0xC02000, 0f));
            g.pop();
        }
        g.translate(0, 0.05, 0);
        g.ring(c.fw * 0.5, off + 1.0, 22, Col.rgba(0xFFB030, 0.8f), Col.rgba(0xFF4000, 0f));
    }

    private static void ignisColumna(Gfx g, Ctx c) {
        float rise = Math.min(1f, c.t / 6f);
        g.alpha = c.fade(0, 10);
        double H = 3.8 * rise;
        double R = 1.3;
        g.frustumY(H, R * 0.9, R * 0.55, 14, Col.rgba(0xFF7010, 0.6f), Col.rgba(0xFFD040, 0.1f), 0);
        g.frustumY(H * 0.9, R * 0.6, R * 0.35, 12, Col.rgba(0xFFE080, 0.55f), Col.rgba(0xFF9020, 0.05f), 0);
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10 + c.t * 0.35;
            g.push();
            g.translate(Math.cos(a) * R, 0, Math.sin(a) * R);
            Parts.flame(g, 1.6 + 1.2 * Rnd.h(c.seed, i), 0.28, c.t, i, Col.rgba(0xFF8010, 0.9f), Col.rgba(0xC01800, 0f));
            g.pop();
        }
        for (int k = 0; k < 3; k++) {
            g.push();
            g.translate(0, ((c.t * 0.12 + k * 0.33) % 1.0) * H, 0);
            g.torus(R * 0.85, 0.07, 18, 4, Col.rgba(0xFFD060, 0.85f));
            g.pop();
        }
        g.translate(0, 0.04, 0);
        g.ring(R * 0.2, R * 1.8, 24, Col.rgba(0xFFB030, 0.8f), Col.rgba(0xFF3000, 0f));
    }

    private static void fireballBurst(Gfx g, Ctx c) {
        float p = c.p();
        double R = c.len;
        double r = R * (0.25 + 0.85 * Math.sqrt(p));
        float a = 1f - p;
        g.alpha = a;
        g.sphere(r, 7, 12, Col.rgba(0xFF5010, 0.55f), Col.rgba(0xFFC040, 0.55f));
        g.sphere(r * 0.65, 6, 10, Col.rgba(0xFFD060, 0.8f), Col.rgba(0xFFFFFF, 0.8f));
        for (int i = 0; i < 12; i++) {
            double az = PI * 2 * Rnd.h(c.seed, i), el = Math.asin(Rnd.s(c.seed, i + 40) * 0.9);
            double dx = Math.cos(az) * Math.cos(el), dy = Math.sin(el), dz = Math.sin(az) * Math.cos(el);
            g.rod(dx * r * 0.7, dy * r * 0.7, dz * r * 0.7, dx * r * 1.35, dy * r * 1.35, dz * r * 1.35, 0.12 * R * 0.5, 0.01,
                    Col.rgba(0xFFC040, 0.9f), Col.rgba(0xFF3000, 0f));
        }
        g.push();
        g.translate(0, -r * 0.2, 0);
        g.ring(r * 0.8, r * 1.5, 20, Col.rgba(0xFFB030, 0.7f), Col.rgba(0xFF3000, 0f));
        g.pop();
    }

    /** Giant flaming fist, knuckles toward +Z, flaming forearm trailing behind it. */
    private static void flameFist(Gfx g, float t, long seed) {
        g.boxG(-0.34, -0.3, -0.1, 0.34, 0.3, 0.5, Col.rgb(0xFF8A10), Col.rgb(0xFFD860));
        for (int i = 0; i < 4; i++) {
            g.box(-0.3 + i * 0.155, -0.26, 0.5, -0.17 + i * 0.155, 0.26, 0.66, Col.rgb(0xFFF0A0));
        }
        g.box(-0.42, -0.1, 0.0, -0.34, 0.2, 0.4, Col.rgb(0xFFB030));
        g.frustumZ(-2.4, 0.3, 0.46, 10, Col.rgba(0xFF6010, 0.15f), Col.rgba(0xFF9A20, 0.8f), 0);
        g.frustumZ(-2.2, 0.2, 0.32, 8, Col.rgba(0xFFE070, 0.1f), Col.rgba(0xFFF0A0, 0.9f), 0);
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8 + t * 0.2;
            g.push();
            g.translate(Math.cos(a) * 0.42, Math.sin(a) * 0.42, -0.2);
            g.rotX(PI - 0.15);
            g.coneZ(1.2 + 0.7 * Rnd.h(seed, i), 0.13, 4, Col.rgba(0xFF7010, 0.85f), Col.rgba(0xC01800, 0f));
            g.pop();
        }
    }

    private static void calidusBrachium(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 3.0);
        double z = 1.2 + (c.len - 1.2) * f;
        g.alpha = c.fade(0, 5);
        g.aim(c);
        g.push();
        g.translate(0, 0, z);
        flameFist(g, c.t, c.seed);
        g.pop();
        if (c.t > 2.5) {
            float k = (float) Parts.clamp01((c.t - 2.5) / 7.0);
            for (int i = 0; i < 3; i++) {
                g.push();
                g.translate(0, 0, c.len + 0.7 + i * 0.5 * k);
                g.rotX(PI / 2);
                g.torus(0.4 + 1.6 * k + i * 0.25, 0.08, 18, 4, Col.rgba(0xFFC040, 0.85f * (1 - k)));
                g.pop();
            }
            g.push();
            g.translate(0, 0, c.len + 0.7);
            Parts.starburst(g, 10, 1.2 * k + 0.3, 0.08, Col.rgba(0xFFF0A0, 0.9f * (1 - k)), Col.rgba(0xFF4000, 0f), c.t * 0.1);
            g.pop();
        }
    }

    /** Calidus Brachium Barrage after the anime: thick glowing arms of fire radiating in every direction from a blinding core. */
    private static void calidusBarrage(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = c.fade(0, 7);
        double R = c.len;
        double ext = Parts.ease(c.t / 3.0);
        int n = 18;
        double ga = PI * (3 - Math.sqrt(5));
        for (int i = 0; i < n; i++) {
            double y = Math.max(-0.7, Math.min(0.7, 1 - 2.0 * (i + 0.5) / n));
            double rr = Math.sqrt(1 - y * y), th = i * ga + 0.3;
            double dx = Math.cos(th) * rr, dz = Math.sin(th) * rr;
            double len = R * (0.55 + 0.45 * Rnd.h(c.seed, i)) * ext;
            double w = i % 2 == 0 ? 0.36 : 0.29;
            int col = i % 2 == 0 ? Col.rgba(0xFFE020, 0.9f) : Col.rgba(0xFF8A1A, 0.92f);
            g.push();
            g.lookFromTo(0, 1.1, 0, dx, 1.1 + y, dz);
            g.frustumZ(len, w * 0.9, w, 8, col, col, 0);
            g.push();
            g.translate(0, 0, len);
            g.sphere(w, 5, 8, col, col);
            g.pop();
            g.pop();
        }
        double pulse = 1.0 + 0.1 * Math.sin(c.t * 1.2);
        g.push();
        g.translate(0, 1.1, 0);
        g.sphere(0.8 * pulse, 6, 10, Col.rgb(0xFFFFFF), Col.rgb(0xFFFFFF));
        g.sphere(1.3 * pulse, 6, 10, Col.rgba(0xFFE880, 0.5f), Col.rgba(0xFFE880, 0.5f));
        g.pop();
        double r = R * Parts.ease(p * 1.8);
        g.push();
        g.translate(0, 0.08, 0);
        g.torus(r, 0.1, 28, 4, Col.rgba(0xFFD060, 0.8f * (1 - p)));
        g.pop();
    }
}
