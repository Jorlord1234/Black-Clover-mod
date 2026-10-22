package com.clovermagic.client.vfx;

import com.clovermagic.client.vfx.Gfx.Mat;
import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/**
 * Spirit Dive: the spirit's regalia around the caster, after Yuno's Spirit of Boreas: a crown of spear blades, two huge flowing
 * cape-wings, a floating halberd with a star head and crescent blade, four-point sparkles and falling light streaks.
 * Same design for all four spirits, recoloured (earth uses real stone for crown and halberd).
 */
final class SpiritFx {
    private SpiritFx() {
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case SPIRIT_DIVE_FIRE -> dive(g, c, 0xFF7A10, 0xFFE070, 0xC02000, false, 0);
            case SPIRIT_DIVE_WATER -> dive(g, c, 0x3FA9F5, 0xE0F8FF, 0x1E5FC0, false, 1);
            case SPIRIT_DIVE_WIND -> dive(g, c, 0xB6F35A, 0xFFFFE0, 0x2FA060, false, 2);
            case SPIRIT_DIVE_EARTH -> dive(g, c, 0xC8A060, 0xFFF0C0, 0x6A4A28, true, 3);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void dive(Gfx g, Ctx c, int main, int bright, int dark, boolean stone, int kind) {
        float fade = c.fade(16, 24);
        g.alpha = fade * (c.firstPerson ? 0.5f : 1f);
        double grow = Parts.ease(c.t / 16.0);
        int cMain = Col.rgba(main, 0.55f), cBright = Col.rgba(bright, 0.95f), cDark = Col.rgba(dark, 0.4f);

        g.push();
        g.rotY(-Math.toRadians(c.bodyYaw));

        // --- two huge flowing cape-wings: fan-shaped sheets rising up and out from the shoulders, scalloped trailing edge
        double[] prof = {1.7, 2.3, 2.8, 3.1, 3.25, 3.0, 2.6, 2.1, 1.6};
        for (int s = -1; s <= 1; s += 2) {
            double flow = Math.sin(c.t * 0.06 + s) * 0.07;
            for (int layer = 0; layer < 2; layer++) {
                double sc = (layer == 0 ? 1.0 : 0.72) * grow;
                int col = layer == 0 ? cMain : cDark;
                double[][] tips = new double[prof.length][];
                for (int i = 0; i < prof.length; i++) {
                    double phi = Math.toRadians(80 - i * 12.5 + (layer == 1 ? 8 : 0)) + flow;
                    double L = prof[i] * sc * (i % 2 == 0 ? 0.92 : 1.0);
                    double u = L * Math.cos(phi), v = L * Math.sin(phi);
                    tips[i] = new double[] {s * (0.2 + u), 1.35 + v * 0.8, -0.28 - 0.3 * (u / 3.3) - layer * 0.1};
                }
                for (int i = 0; i + 1 < prof.length; i++) {
                    g.tri(s * 0.2, 1.35, -0.28, tips[i][0], tips[i][1], tips[i][2], tips[i + 1][0], tips[i + 1][1], tips[i + 1][2],
                            Col.rgba(bright, 0.75f), col, col);
                    if (layer == 0) {
                        g.rod(tips[i][0], tips[i][1], tips[i][2], tips[i + 1][0], tips[i + 1][1], tips[i + 1][2], 0.03, 0.03, cBright, cBright);
                    }
                }
                if (layer == 0) {
                    for (int i = 1; i + 1 < prof.length; i += 2) {
                        g.rod(s * 0.2, 1.35, -0.28, tips[i][0], tips[i][1], tips[i][2], 0.02, 0.012, Col.rgba(bright, 0.6f), Col.rgba(bright, 0.2f));
                    }
                }
            }
        }

        // --- crown of spear blades floating above the head
        double cy = 2.2 + 0.04 * Math.sin(c.t * 0.1);
        if (stone) g.mat(Mat.STONE);
        for (int i = 0; i < 16; i++) {
            double a = PI * 2 * i / 16;
            double h = 0.45 + 0.25 * Rnd.h(c.seed, i);
            double r = 0.4;
            g.push();
            g.translate(Math.cos(a) * r, cy, Math.sin(a) * r);
            g.rotZ(-Math.cos(a) * 0.12);
            g.rotX(Math.sin(a) * 0.12);
            int body = stone ? Col.rgb(0xE0E0E0) : cBright;
            g.box(-0.025, 0, -0.025, 0.025, h, 0.025, body);
            g.translate(0, h, 0);
            g.coneY(0.2, 0.055, 4, body, body);
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.push();
        g.translate(0, cy + 0.14, 0);
        g.torus(0.4, 0.022, 24, 4, cBright);
        g.translate(0, 0.22, 0);
        g.torus(0.4, 0.016, 24, 4, cBright);
        g.pop();

        // --- floating halberd at the caster's right hand: pole, halo ring, pom, star head, crescent blade
        g.push();
        g.translate(-0.75, 0, 0.25);
        if (stone) g.mat(Mat.STONE);
        int pole = stone ? Col.rgb(0xD8D8D8) : cBright;
        g.frustumY(2.6, 0.035, 0.035, 6, pole, pole, 3);
        g.mat(Mat.GLOW);
        g.push();
        g.translate(0, 1.45, 0);
        g.rotX(0.25);
        g.torus(0.19, 0.012, 18, 4, cBright);
        g.pop();
        g.push();
        g.translate(0, 1.75, 0);
        g.sphere(0.1, 5, 8, Col.rgba(main, 0.9f), cBright);
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8;
            g.push();
            g.rotY(a);
            g.rotZ(-1.1 + 0.4 * Math.sin(i));
            g.coneY(0.12, 0.02, 3, cBright, Col.rgba(main, 0f));
            g.pop();
        }
        g.pop();
        g.push();
        g.translate(0, 2.35, 0);
        g.rotY(0.0);
        // five-point star head (flat, faces sideways)
        for (int i = 0; i < 5; i++) {
            double a0 = PI * 2 * i / 5 + PI / 2, a1 = a0 + PI / 5, a2 = a0 + PI * 2 / 5;
            g.tri(0, 0, 0, Math.cos(a0) * 0.6, Math.sin(a0) * 0.6, 0, Math.cos(a1) * 0.24, Math.sin(a1) * 0.24, 0, cBright, cMain, cMain);
            g.tri(0, 0, 0, Math.cos(a1) * 0.24, Math.sin(a1) * 0.24, 0, Math.cos(a2) * 0.6, Math.sin(a2) * 0.6, 0, cBright, cMain, cMain);
        }
        g.translate(0, 0.35, 0);
        g.coneY(0.5, 0.045, 4, cBright, cBright);
        g.pop();
        g.push();
        g.translate(0.0, 2.35, 0.0);
        g.rotX(PI / 2);
        g.translate(0, 0, 0);
        g.rotZ(PI / 2);
        g.translate(0, 0, -0.15);
        g.arcBlade(1.1, 0.5, 1.7, 12, 0.025, cBright, cMain);       // crescent axe blade
        g.pop();
        g.pop();

        g.pop();

        // --- sparkles and falling light streaks
        for (int i = 0; i < 9; i++) {
            double a = PI * 2 * Rnd.h(c.seed, i + 100) + c.t * 0.01, r = 1.6 + 1.8 * Rnd.h(c.seed, i + 130);
            double y = 0.6 + 2.2 * Rnd.h(c.seed, i + 160);
            double tw = 0.6 + 0.4 * Math.sin(c.t * 0.15 + i * 1.9);
            g.push();
            g.translate(Math.cos(a) * r, y, Math.sin(a) * r);
            g.rotY(c.t * 0.02 + i);
            Parts.star4(g, 0.28 * tw, Col.rgba(bright, 1f), Col.rgba(main, 0f));
            g.pop();
        }
        for (int i = 0; i < 16; i++) {
            double a = PI * 2 * Rnd.h(c.seed, i + 200), r = 0.8 + 3.2 * Rnd.h(c.seed, i + 230);
            double ph = (c.t * (0.012 + 0.01 * Rnd.h(c.seed, i + 260)) + Rnd.h(c.seed, i + 290)) % 1.0;
            double top = 4.2 - ph * 4.6;
            g.rod(Math.cos(a) * r, top, Math.sin(a) * r, Math.cos(a) * r, top - 1.4, Math.sin(a) * r, 0.012, 0.004,
                    Col.rgba(bright, 0.55f), Col.rgba(main, 0f));
        }
        // element extras
        if (kind == 3) {
            g.mat(Mat.STONE);
            for (int i = 0; i < 5; i++) {
                double a = c.t * 0.05 + PI * 2 * i / 5;
                g.push();
                g.translate(Math.cos(a) * 1.5, 0.7 + 0.3 * Math.sin(c.t * 0.1 + i), Math.sin(a) * 1.5);
                g.rock(0.14, c.seed + i, Col.rgb(0xFFFFFF));
                g.pop();
            }
            g.mat(Mat.GLOW);
        } else if (kind == 1) {
            for (int i = 0; i < 8; i++) {
                double a = c.t * 0.06 + PI * 2 * i / 8, y = ((c.t * 0.02 + i * 0.125) % 1.0) * 2.2;
                g.push();
                g.translate(Math.cos(a) * 1.2, y, Math.sin(a) * 1.2);
                g.sphere(0.07, 4, 6, Col.rgba(main, 0.9f), cBright);
                g.pop();
            }
        } else if (kind == 0) {
            for (int i = 0; i < 6; i++) {
                double a = c.t * 0.07 + PI * 2 * i / 6;
                g.push();
                g.translate(Math.cos(a) * 1.3, 0, Math.sin(a) * 1.3);
                Parts.flame(g, 0.9, 0.14, c.t, i, Col.rgba(main, 0.8f), Col.rgba(dark, 0f));
                g.pop();
            }
        }
        g.push();
        g.translate(0, 0.04, 0);
        Parts.circle(g, 1.9, c.t * 0.04, Col.rgba(main, 0.9f), Col.rgba(bright, 1f));
        g.pop();
    }
}
