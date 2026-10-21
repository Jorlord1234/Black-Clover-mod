package com.clovermagic.client.vfx;

import com.clovermagic.client.vfx.Gfx.Mat;
import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Earth spells: real stone texture, brown dust, spikes and slabs. */
final class EarthFx {
    private EarthFx() {
    }

    static final int STONE = Col.rgb(0xFFFFFF), DARK = Col.rgb(0xB0B0B0), DUST = Col.rgba(0xB59A74, 0.35f);

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case EARTH_SPIKE -> earthSpike(g, c);
            case STONE_ARMOR -> stoneArmor(g, c);
            case QUAKE -> quake(g, c);
            case BOULDER_TOSS -> boulderToss(g, c);
            case STONE_BIND -> stoneBind(g, c);
            case EARTHEN_SHIELD -> earthenShield(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void earthSpike(Gfx g, Ctx c) {
        g.rotY(-Math.toRadians(c.yaw));
        for (int i = 0; i < 8; i++) {
            double d = 0.9 + i * (c.len - 0.9) / 7.0;
            double gy = c.ground(i);
            if (Double.isNaN(gy)) gy = 0;
            double start = i * 1.2;                       // wave runs outward
            double k = Parts.clamp01((c.t - start) / 3.0);
            double out = Parts.clamp01((c.life - c.t - (7 - i) * 0.6) / 5.0);
            double h = (1.3 + 0.9 * Rnd.h(c.seed, i)) * Parts.ease(k) * out;
            if (h < 0.05) continue;
            double sway = Rnd.s(c.seed, i + 20) * 0.35;
            g.push();
            g.mat(Mat.STONE);
            g.translate(sway, gy - 0.15, d);
            g.rotX(-0.25 * Rnd.s(c.seed, i + 50));
            g.rotZ(0.3 * Rnd.s(c.seed, i + 70));
            g.crystal(h, 0.38 + 0.1 * Rnd.h(c.seed, i + 3), 5, DARK, STONE);
            g.push();
            g.translate(0.3, 0, 0.1);
            g.rotZ(-0.35);
            g.crystal(h * 0.55, 0.2, 4, DARK, STONE);
            g.pop();
            g.mat(Mat.GLOW);
            g.ring(0.4, 0.95, 10, DUST, Col.rgba(0xB59A74, 0f));
            g.pop();
        }
    }

    private static void stoneArmor(Gfx g, Ctx c) {
        float a = c.fade(6, 10);
        g.alpha = a;
        g.mat(Mat.STONE);
        g.rotY(-Math.toRadians(c.bodyYaw));
        double grow = Parts.ease(c.t / 8.0);
        g.scale(1, 1, 1);
        // chest and back plates, shoulder blocks, arm and leg guards (positions around a 0.6 x 1.8 body)
        double o = 0.06 * (1 - grow);
        g.box(-0.34, 0.95, 0.19 + o, 0.34, 1.5, 0.34 + o, STONE);
        g.box(-0.34, 0.95, -0.34 - o, 0.34, 1.5, -0.19 - o, DARK);
        g.box(-0.3, 0.7, 0.2, 0.3, 0.95, 0.3, DARK);
        for (int s = -1; s <= 1; s += 2) {
            g.box(Math.min(s * 0.3, s * 0.62), 1.38, -0.22, Math.max(s * 0.3, s * 0.62), 1.62, 0.22, STONE);      // shoulder
            g.box(Math.min(s * 0.36, s * 0.52), 0.78, -0.14, Math.max(s * 0.36, s * 0.52), 1.3, 0.14, DARK);      // arm
            g.box(Math.min(s * 0.02, s * 0.3), 0.2, 0.1, Math.max(s * 0.02, s * 0.3), 0.7, 0.3, STONE);         // thigh
            g.box(Math.min(s * 0.02, s * 0.3), 0.0, 0.08, Math.max(s * 0.02, s * 0.3), 0.3, 0.3, DARK);
            g.push();
            g.translate(s * 0.5, 1.7, 0);
            g.rotZ(-s * 0.4);
            g.crystal(0.45, 0.1, 4, DARK, STONE);                                                              // pauldron spike
            g.pop();
        }
        g.mat(Mat.GLOW);
        // orbiting pebbles and dust
        for (int i = 0; i < 5; i++) {
            double ang = c.t * 0.06 + PI * 2 * i / 5;
            g.push();
            g.translate(Math.cos(ang) * 0.9, 0.5 + i * 0.28, Math.sin(ang) * 0.9);
            g.mat(Mat.STONE);
            g.rock(0.12, c.seed + i, STONE);
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.ring(0.5, 1.2, 16, DUST, Col.rgba(0xB59A74, 0f));
    }

    private static void quake(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = Math.min(1f, (1f - p) * 3f);
        double r = c.len * Parts.ease(p * 2.0);
        // cracks
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10 + 0.2 * Rnd.s(c.seed, i);
            double L = r * (0.8 + 0.3 * Rnd.h(c.seed, i + 9));
            g.push();
            g.translate(0, 0.05, 0);
            g.bolt(0, 0, 0, Math.cos(a) * L, 0, Math.sin(a) * L, 6, 0.35, c.seed + i, 0.05, Col.rgba(0x201208, 0.95f), Col.rgba(0x5A3A1E, 0.8f));
            g.pop();
        }
        g.ring(0.3, r, 24, DUST, Col.rgba(0xB59A74, 0f));
        g.torus(r, 0.1, 24, 4, Col.rgba(0xB59A74, 0.6f));
        // debris leaping up
        g.mat(Mat.STONE);
        for (int i = 0; i < 12; i++) {
            double a = PI * 2 * Rnd.h(c.seed, i + 100), d = 0.8 + (r - 0.8) * Rnd.h(c.seed, i + 130);
            double life = Parts.clamp01(c.t / 16.0 + 0.05 * i / 12.0);
            double y = 2.6 * 4 * life * (1 - life) * (0.6 + 0.8 * Rnd.h(c.seed, i + 160));
            g.push();
            g.translate(Math.cos(a) * d, y, Math.sin(a) * d);
            g.rotY(c.t * 0.3 + i);
            g.rotX(c.t * 0.2);
            g.rock(0.18 + 0.12 * Rnd.h(c.seed, i + 190), c.seed + i * 3, STONE);
            g.pop();
        }
    }

    private static void boulderToss(Gfx g, Ctx c) {
        double f = Parts.easeIn(c.t / 8.0 + 0.0) * 0.6 + Parts.ease(c.t / 8.0) * 0.4;
        double z = 0.8 + (c.len - 0.8) * Parts.clamp01(f);
        g.alpha = c.fade(0, 5);
        g.aim(c);
        g.push();
        g.translate(0, 0, z);
        g.rotY(c.t * 0.5);
        g.rotX(c.t * 0.38);
        g.mat(Mat.STONE);
        g.rock(0.75, c.seed, STONE);
        g.push();
        g.translate(0.5, 0.4, 0.2);
        g.rock(0.28, c.seed + 4, DARK);
        g.pop();
        g.pop();
        g.mat(Mat.GLOW);
        g.frustumZ(z, 0.05, 0.6, 8, Col.rgba(0xB59A74, 0f), Col.rgba(0xB59A74, 0.35f), 0);
        if (c.t > 8) {
            float k = (float) Parts.clamp01((c.t - 8) / 6.0);
            g.push();
            g.translate(0, 0, c.len);
            g.sphere(0.3 + 1.1 * k, 5, 8, Col.rgba(0xB59A74, 0.45f * (1 - k)), Col.rgba(0xD8C090, 0.45f * (1 - k)));
            g.pop();
        }
    }

    private static void stoneBind(Gfx g, Ctx c) {
        g.alpha = c.fade(4, 8);
        double w = Math.max(0.35, c.fw * 0.6) + 0.12, h = c.fh;
        double grow = Parts.ease(c.t / 6.0);
        g.mat(Mat.STONE);
        double[] ys = {0.12 * h, 0.5 * h, 0.85 * h};
        for (int ring = 0; ring < 3; ring++) {
            double y = ys[ring] * grow;
            int n = 8;
            for (int i = 0; i < n; i++) {
                double a = PI * 2 * i / n + ring * 0.4;
                g.push();
                g.translate(Math.cos(a) * w, y, Math.sin(a) * w);
                g.rotY(-a);
                g.box(-0.07, -0.1, -0.2, 0.12, 0.1, 0.2, i % 2 == 0 ? STONE : DARK);
                g.pop();
            }
        }
        for (int i = 0; i < 6; i++) {
            double a = PI * 2 * i / 6 + 0.3;
            g.push();
            g.translate(Math.cos(a) * (w + 0.25), -0.1, Math.sin(a) * (w + 0.25));
            g.rotZ(-Math.cos(a) * 0.35);
            g.rotX(Math.sin(a) * 0.35);
            g.crystal((0.6 + 0.5 * Rnd.h(c.seed, i)) * grow, 0.2, 4, DARK, STONE);
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.translate(0, 0.04, 0);
        g.ring(w, w + 0.9, 18, DUST, Col.rgba(0xB59A74, 0f));
    }

    private static void earthenShield(Gfx g, Ctx c) {
        g.alpha = c.fade(8, 12);
        g.push();
        g.translate(0, 0.9, 0);
        g.scale(1, 1.15, 1);
        g.sphere(1.9, 7, 14, Col.rgba(0x8A6A3A, 0.12f * c.shell()), Col.rgba(0xC8A060, 0.08f * c.shell()));
        g.pop();
        g.mat(Mat.STONE);
        int n = 6;
        double grow = Parts.ease(c.t / 10.0);
        for (int i = 0; i < n; i++) {
            double a = c.t * 0.04 + PI * 2 * i / n;
            double bob = 0.12 * Math.sin(c.t * 0.12 + i * 1.3);
            g.push();
            g.translate(Math.cos(a) * 1.7 * grow, 0.95 + bob, Math.sin(a) * 1.7 * grow);
            g.rotY(-a + PI / 2);
            g.box(-0.45, -0.7, -0.1, 0.45, 0.7, 0.1, i % 2 == 0 ? STONE : DARK);
            g.push();
            g.translate(0, 0.7, 0);
            g.crystal(0.35, 0.18, 4, DARK, STONE);
            g.pop();
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.translate(0, 0.04, 0);
        g.ring(1.0, 2.2, 22, Col.rgba(0xC8A060, 0.45f), Col.rgba(0x8A6A3A, 0f));
    }
}
