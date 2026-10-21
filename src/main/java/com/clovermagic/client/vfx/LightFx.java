package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Light spells: white-gold radiance, halos, rainbow rays. */
final class LightFx {
    private LightFx() {
    }

    static int gold(float a) {
        return Col.rgba(0xFFE680, a);
    }

    static int white(float a) {
        return Col.rgba(0xFFFFFF, a);
    }

    static int warm(float a) {
        return Col.rgba(0xFFF4C0, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case LIGHT_BEAM -> holyRay(g, c);
            case HEALING -> healing(g, c);
            case PURIFY -> purify(g, c);
            case PRISM_BURST -> prismBurst(g, c);
            case LIGHT_SPEED_DASH -> lightDash(g, c);
            case RADIANT_AEGIS -> radiantAegis(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void holyRay(Gfx g, Ctx c) {
        g.alpha = c.fade(1, 8);
        g.aim(c);
        double L = c.len;
        double w = 0.16 + 0.04 * Math.sin(c.t * 1.1);
        g.frustumZ(L, w, w, 8, white(1f), white(1f), 3);
        g.frustumZ(L, w * 2.3, w * 2.3, 10, gold(0.55f), gold(0.45f), 3);
        g.frustumZ(L, w * 4.5, w * 4.5, 10, warm(0.18f), warm(0.1f), 0);
        for (int i = 0; i < 4; i++) {
            double z = (c.t * 1.8 + i * L / 4) % L;
            g.push();
            g.translate(0, 0, z);
            Parts.starburst(g, 4, w * 6, 0.04, white(0.9f), gold(0f), c.t * 0.2 + i);
            g.pop();
        }
        g.push();
        g.translate(0, 0, 0.7);
        g.rotX(PI / 2);
        Parts.circle(g, 0.8, c.t * 0.2, Col.rgb(0xFFD860), white(1f));
        g.pop();
        g.push();
        g.translate(0, 0, L);
        g.sphere(0.4, 6, 10, white(0.9f), gold(0.9f));
        Parts.starburst(g, 8, 1.0, 0.06, white(0.9f), gold(0f), c.t * 0.3);
        g.pop();
    }

    private static void healing(Gfx g, Ctx c) {
        g.alpha = c.fade(3, 12);
        int green = Col.rgb(0x7CF2A0);
        g.frustumY(2.6, 0.9, 0.7, 16, Col.rgba(0x7CF2A0, 0.3f), Col.rgba(0xFFFFFF, 0.04f), 0);
        for (int k = 0; k < 3; k++) {
            double y = ((c.t * 0.05 + k * 0.33) % 1.0) * 2.4;
            g.push();
            g.translate(0, y, 0);
            g.torus(0.85, 0.045, 20, 4, Col.rgba(0xFFFFFF, 0.9f));
            g.pop();
        }
        for (int i = 0; i < 6; i++) {
            double a = c.t * 0.09 + PI * 2 * i / 6;
            double y = ((c.t * 0.03 + i * 0.17) % 1.0) * 2.4;
            g.push();
            g.translate(Math.cos(a) * 0.9, y, Math.sin(a) * 0.9);
            g.rotY(-a + PI / 2);
            g.box(-0.04, -0.14, -0.02, 0.04, 0.14, 0.02, green);
            g.box(-0.14, -0.04, -0.02, 0.14, 0.04, 0.02, green);
            g.pop();
        }
        g.translate(0, 0.04, 0);
        Parts.circle(g, 1.3, c.t * 0.06, Col.rgba(0x7CF2A0, 0.9f), white(1f));
    }

    private static void purify(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = c.fade(3, 12);
        g.frustumY(4.2, 0.55, 0.35, 12, white(0.5f), gold(0.05f), 0);
        g.frustumY(4.2, 0.25, 0.15, 10, white(0.9f), white(0.1f), 0);
        double r = 0.3 + 2.4 * Parts.ease(p * 1.5);
        g.ring(0.1, r, 24, white(0.6f), gold(0f));
        g.push();
        g.translate(0, 0.05, 0);
        g.torus(r, 0.07, 24, 4, white(0.9f));
        g.pop();
        double hy = 3.0 - 2.0 * Parts.clamp01(p * 1.6);
        g.push();
        g.translate(0, hy, 0);
        g.torus(0.55, 0.04, 20, 4, gold(1f));
        g.pop();
        for (int i = 0; i < 8; i++) {
            double a = c.t * 0.1 + PI * 2 * i / 8;
            double y = ((c.t * 0.04 + i * 0.125) % 1.0) * 3.5;
            g.push();
            g.translate(Math.cos(a) * 0.9, y, Math.sin(a) * 0.9);
            g.crystal(0.28, 0.05, 4, white(1f), gold(1f));
            g.pop();
        }
    }

    private static void prismBurst(Gfx g, Ctx c) {
        g.alpha = c.fade(0, 6);
        g.aim(c);
        double grow = Parts.ease(c.t / 4.0);
        int[] rainbow = {0xFF4040, 0xFF9A30, 0xFFEB40, 0x50E060, 0x40B8FF, 0x5060FF, 0xB050FF};
        double L = c.len * grow;
        for (int i = 0; i < 7; i++) {
            double a = (i - 3) * 0.19;
            double dx = Math.sin(a), dz = Math.cos(a);
            g.rod(0, 0, 1.0, dx * L, 0.0, 1.0 + dz * L, 0.06, 0.16, Col.rgba(rainbow[i], 0.95f), Col.rgba(rainbow[i], 0.15f));
        }
        for (int i = 0; i < 5; i++) {
            double a = (i - 2) * 0.17 + 0.09;
            double el = ((i % 2 == 0) ? 1 : -1) * 0.12;
            g.rod(0, 0, 1.0, Math.sin(a) * L * 0.9, Math.sin(el) * L * 0.4, 1.0 + Math.cos(a) * L * 0.9, 0.03, 0.08, white(0.8f), white(0f));
        }
        g.push();
        g.translate(0, 0, 1.0);
        g.rotZ(c.t * 0.3);
        g.rotY(c.t * 0.25);
        g.crystal(0.55, 0.22, 4, white(0.95f), white(1f));
        g.rotX(PI);
        g.crystal(0.55, 0.22, 4, white(0.95f), white(1f));
        g.pop();
        g.push();
        g.translate(0, 0, 0.9);
        g.rotX(PI / 2);
        Parts.circle(g, 0.55, c.t * 0.2, Col.rgb(0xB0D0FF), white(1f));
        g.pop();
    }

    private static void lightDash(Gfx g, Ctx c) {
        g.alpha = c.fade(0, 9);
        g.aim(c);
        double L = c.len;
        for (int k = 0; k < 3; k++) {
            g.push();
            g.rotZ(k * PI / 3 + 0.2);
            g.box(-0.55, -0.025, 0.2, 0.55, 0.025, L, white(0.75f));
            g.pop();
        }
        g.frustumZ(L, 0.12, 0.12, 6, white(1f), gold(0.9f), 3);
        g.frustumZ(L, 0.4, 0.2, 8, gold(0.35f), gold(0f), 0);
        for (int i = 0; i < 6; i++) {
            double a = PI * 2 * Rnd.h(c.seed, i), r = 0.4 + 0.5 * Rnd.h(c.seed, i + 9);
            g.rod(Math.cos(a) * r, Math.sin(a) * r, 0, Math.cos(a) * r, Math.sin(a) * r, L * (0.6 + 0.4 * Rnd.h(c.seed, i + 20)), 0.03, 0.005, white(0.9f), gold(0f));
        }
        g.sphere(0.5 * (1 - c.p()), 5, 8, white(0.9f), gold(0.8f));
    }

    private static void radiantAegis(Gfx g, Ctx c) {
        g.alpha = c.fade(8, 12);
        double R = 1.7;
        double grow = Parts.ease(c.t / 8.0);
        int n = 6;
        for (int i = 0; i < n; i++) {
            double a0 = PI * 2 * i / n + c.t * 0.01, a1 = PI * 2 * (i + 1) / n + c.t * 0.01;
            double x0 = Math.cos(a0) * R * grow, z0 = Math.sin(a0) * R * grow, x1 = Math.cos(a1) * R * grow, z1 = Math.sin(a1) * R * grow;
            double top = 0.62;
            g.quad(x0, 0.0, z0, x1, 0.0, z1, x1 * top, 2.4, z1 * top, x0 * top, 2.4, z0 * top,
                    gold(0.30f * c.shell()), gold(0.30f * c.shell()), white(0.14f * c.shell()), white(0.14f * c.shell()));
            g.rod(x0, 0, z0, x0 * top, 2.4, z0 * top, 0.03, 0.03, gold(0.9f), white(0.9f));
        }
        g.push();
        g.translate(0, 2.4, 0);
        g.rotY(c.t * 0.1);
        g.torus(R * 0.62, 0.05, 24, 4, gold(1f));
        g.pop();
        g.push();
        g.translate(0, 2.7 + 0.08 * Math.sin(c.t * 0.15), 0);
        g.rotY(-c.t * 0.07);
        g.torus(0.38, 0.04, 18, 4, white(1f));
        g.pop();
        g.translate(0, 0.04, 0);
        Parts.circle(g, R * 1.1, -c.t * 0.04, Col.rgba(0xFFD860, 0.95f), white(1f));
    }
}
