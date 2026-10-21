package com.clovermagic.client.vfx;

import com.clovermagic.client.vfx.Gfx.Mat;
import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Ice spells: blue-ice texture crystals, frost glow, snow. */
final class IceFx {
    private IceFx() {
    }

    static int ice(float a) {
        return Col.rgba(0xCFEFFF, a);
    }

    static int deep(float a) {
        return Col.rgba(0x5AB8F0, a);
    }

    static int white(float a) {
        return Col.rgba(0xFFFFFF, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case FROST_LANCE -> frostLance(g, c);
            case ICE_SHELL -> iceShell(g, c);
            case FROZEN_RING -> frozenRing(g, c);
            case HAILSTORM -> hailstorm(g, c);
            case ICE_PRISON -> icePrison(g, c);
            case FROST_BARRIER -> frostBarrier(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void frostLance(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 5.0);
        double z = 1.2 + (c.len - 1.2) * f;
        g.alpha = c.fade(0, 5);
        g.aim(c);
        g.frustumZ(z, 0.02, 0.25, 8, deep(0f), ice(0.4f), 0);
        g.push();
        g.translate(0, 0, z);
        g.mat(Mat.ICE);
        g.push();
        g.translate(0, 0, -1.4);
        g.rotX(PI / 2);
        g.crystal(2.6, 0.13, 6, ice(0.9f), white(1f));
        g.pop();
        for (int i = 0; i < 4; i++) {
            double a = PI / 2 * i + 0.4;
            g.push();
            g.rotZ(a);
            g.translate(0.17, 0, -1.2);
            g.rotY(0.25);
            g.rotX(PI / 2 - 0.2);
            g.crystal(0.7, 0.05, 4, ice(0.9f), white(1f));
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.push();
        g.translate(0, 0, -0.9);
        g.rotX(PI / 2);
        g.torus(0.4, 0.03, 14, 4, white(0.8f));
        g.pop();
        g.pop();
        if (c.t > 5) {
            float k = (float) Parts.clamp01((c.t - 5) / 8.0);
            g.mat(Mat.ICE);
            for (int i = 0; i < 8; i++) {
                double az = PI * 2 * Rnd.h(c.seed, i), el = Rnd.s(c.seed, i + 9) * 1.0;
                double d = 0.3 + 1.5 * k;
                g.push();
                g.translate(Math.cos(az) * d * Math.cos(el), Math.sin(el) * d, c.len + Math.sin(az) * d * Math.cos(el));
                g.rotX(c.t * 0.4 + i);
                g.crystal(0.3 * (1 - k), 0.06, 4, ice(0.9f), white(1f));
                g.pop();
            }
        }
    }

    private static void iceShell(Gfx g, Ctx c) {
        g.alpha = c.fade(8, 12);
        g.push();
        g.translate(0, 0.95, 0);
        g.scale(0.9, 1.2, 0.9);
        g.sphere(1.05, 7, 12, deep(0.16f * c.shell()), white(0.12f * c.shell()));
        g.pop();
        g.mat(Mat.ICE);
        double grow = Parts.ease(c.t / 8.0);
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10 + 0.3;
            double y = 0.4 + 1.2 * Rnd.h(c.seed, i);
            g.push();
            g.translate(Math.cos(a) * 0.62, y, Math.sin(a) * 0.62);
            g.rotY(-a);
            g.rotZ(-PI / 2 + 0.5 + 0.4 * Rnd.h(c.seed, i + 20));
            g.crystal((0.5 + 0.5 * Rnd.h(c.seed, i + 40)) * grow, 0.1, 5, ice(0.9f), white(1f));
            g.pop();
        }
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.5, 1.55, 0);
            g.rotZ(-s * 0.5);
            g.crystal(0.7 * grow, 0.13, 5, ice(0.9f), white(1f));
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.translate(0, 0.03, 0);
        g.ring(0.5, 1.4, 20, white(0.5f), deep(0f));
    }

    private static void frozenRing(Gfx g, Ctx c) {
        float p = c.p();
        double R = c.len;
        g.alpha = Math.min(1f, (1f - p) * 2.5f);
        double r = R * Parts.ease(p * 3.0);
        g.ring(0.2, r, 28, white(0.55f), deep(0.05f));
        g.mat(Mat.ICE);
        int[] counts = {8, 14, 20};
        for (int k = 0; k < 3; k++) {
            double rad = 2.0 * (k + 1) * R / 6.0;
            double grow = Parts.ease((c.t - k * 3.0) / 5.0);
            double out = Parts.clamp01((c.life - c.t - 3) / 6.0);
            for (int i = 0; i < counts[k]; i++) {
                double a = PI * 2 * i / counts[k] + k;
                double h = (0.9 + 0.7 * Rnd.h(c.seed, i + k * 30) + 0.25 * k) * grow * out;
                if (h < 0.05) continue;
                g.push();
                g.translate(Math.cos(a) * rad, -0.1, Math.sin(a) * rad);
                g.rotZ(-Math.cos(a) * 0.2);
                g.rotX(Math.sin(a) * 0.2);
                g.crystal(h, 0.16 + 0.05 * k, 5, ice(0.9f), white(1f));
                g.pop();
            }
        }
        g.mat(Mat.GLOW);
        g.push();
        g.translate(0, 0.05, 0);
        g.torus(r, 0.08, 28, 4, white(0.7f));
        g.pop();
    }

    private static void hailstorm(Gfx g, Ctx c) {
        g.alpha = c.fade(3, 8);
        double yaw = Math.toRadians(c.yaw), pit = Math.toRadians(c.pitch);
        double dx = -Math.sin(yaw) * Math.cos(pit), dy = -Math.sin(pit), dz = Math.cos(yaw) * Math.cos(pit);
        // dark frost cloud above the area
        for (int i = 0; i < 7; i++) {
            double d = c.len * (0.15 + 0.7 * Rnd.h(c.seed, i + 200));
            double ox = 2.2 * Rnd.s(c.seed, i + 210), oz = 2.2 * Rnd.s(c.seed, i + 220);
            g.push();
            g.translate(dx * d + ox, dy * d + 4.3, dz * d + oz);
            g.scale(1, 0.55, 1);
            g.sphere(1.1, 5, 8, Col.rgba(0x9FB8D0, 0.4f), Col.rgba(0xE8F4FF, 0.4f));
            g.pop();
        }
        g.mat(Mat.ICE);
        for (int i = 0; i < 26; i++) {
            double d = c.len * Rnd.h(c.seed, i);
            double a = PI * 2 * Rnd.h(c.seed, i + 40), rr = 2.7 * Math.sqrt(Rnd.h(c.seed, i + 80));
            double phase = ((c.t * 0.11 + Rnd.h(c.seed, i + 120)) % 1.0);
            double y = dy * d + 4.2 - 5.0 * phase;
            g.push();
            g.translate(dx * d + Math.cos(a) * rr, y, dz * d + Math.sin(a) * rr);
            g.rotY(c.t * 0.3 + i);
            g.rotX(PI);
            g.crystal(0.55, 0.13, 5, ice(0.95f), white(1f));
            g.pop();
        }
        g.mat(Mat.GLOW);
        g.push();
        g.translate(dx * c.len * 0.5, dy * c.len * 0.5 + 0.05, dz * c.len * 0.5);
        g.disc(2.9, 20, white(0.25f), deep(0.0f));
        g.pop();
    }

    private static void icePrison(Gfx g, Ctx c) {
        g.alpha = c.fade(3, 10);
        double w = Math.max(0.45, c.fw * 0.5 + 0.2), h = c.fh + 0.25;
        double grow = Parts.ease(c.t / 4.0);
        g.mat(Mat.ICE);
        g.boxG(-w, -0.05, -w, w, h * grow, w, ice(0.55f), ice(0.4f));
        for (int i = 0; i < 4; i++) {
            double sx = (i % 2 == 0 ? 1 : -1) * w, sz = (i < 2 ? 1 : -1) * w;
            g.push();
            g.translate(sx, -0.05, sz);
            g.rotZ(-sx * 0.35 / w);
            g.rotX(sz * 0.35 / w);
            g.crystal((0.9 + 0.5 * Rnd.h(c.seed, i)) * grow, 0.17, 5, ice(0.9f), white(1f));
            g.pop();
        }
        g.push();
        g.translate(0, h * grow, 0);
        g.crystal(0.6 * grow, 0.2, 6, ice(0.9f), white(1f));
        g.pop();
        g.mat(Mat.GLOW);
        g.translate(0, 0.04, 0);
        g.ring(w, w + 1.0, 18, white(0.55f), deep(0f));
    }

    private static void frostBarrier(Gfx g, Ctx c) {
        g.alpha = c.fade(8, 12);
        double R = c.len * 0.8;
        int n = 10;
        double grow = Parts.ease(c.t / 10.0);
        g.mat(Mat.ICE);
        for (int i = 0; i < n; i++) {
            double a = c.t * 0.01 + PI * 2 * i / n;
            g.push();
            g.translate(Math.cos(a) * R, -0.1, Math.sin(a) * R);
            g.rotZ(-Math.cos(a) * 0.12);
            g.rotX(Math.sin(a) * 0.12);
            g.crystal((2.0 + 0.7 * Rnd.h(c.seed, i)) * grow, 0.3, 6, ice(0.9f), white(1f));
            g.pop();
        }
        g.mat(Mat.GLOW);
        // translucent wall between the pillars
        g.frustumY(1.9 * grow, R, R * 0.97, 30, deep(0.2f * c.shell()), white(0.08f * c.shell()), 0);
        g.push();
        g.translate(0, 1.9 * grow, 0);
        g.torus(R * 0.97, 0.05, 30, 4, white(0.75f * Math.max(0.3f, c.shell())));
        g.pop();
        g.translate(0, 0.03, 0);
        g.disc(R, 28, white(0.35f), deep(0.1f));
    }
}
