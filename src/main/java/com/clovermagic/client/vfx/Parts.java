package com.clovermagic.client.vfx;

import com.clovermagic.client.vfx.Gfx.Mat;

/** Reusable building blocks shared by several spell models. */
final class Parts {
    private Parts() {
    }

    static final double PI = Math.PI;

    static double clamp01(double x) {
        return x < 0 ? 0 : Math.min(x, 1);
    }

    /** Fast start, slow end. */
    static double ease(double x) {
        x = clamp01(x);
        return 1 - (1 - x) * (1 - x);
    }

    /** Slow start, fast end. */
    static double easeIn(double x) {
        x = clamp01(x);
        return x * x;
    }

    /** Flat rotating magic circle in the XZ plane (draw it after rotating so the plane faces where you want). */
    static void circle(Gfx g, double r, double spin, int main, int bright) {
        g.push();
        g.rotY(spin);
        g.ring(r * 0.90, r, 28, Col.alpha(main, 0.2f), main);
        g.ring(r * 0.62, r * 0.68, 24, main, Col.alpha(main, 0.4f));
        for (int i = 0; i < 12; i++) {
            double a = PI * 2 * i / 12;
            g.rod(Math.cos(a) * r * 0.9, 0, Math.sin(a) * r * 0.9, Math.cos(a) * r * 0.68, 0, Math.sin(a) * r * 0.68,
                    r * 0.02, r * 0.02, bright, main);
        }
        for (int k = 0; k < 2; k++) {
            double off = k * PI / 3;
            for (int i = 0; i < 3; i++) {
                double a0 = off + PI * 2 * i / 3, a1 = off + PI * 2 * (i + 1) / 3;
                g.rod(Math.cos(a0) * r * 0.62, 0, Math.sin(a0) * r * 0.62, Math.cos(a1) * r * 0.62, 0, Math.sin(a1) * r * 0.62,
                        r * 0.018, r * 0.018, bright, bright);
            }
        }
        g.disc(r * 0.12, 8, bright, Col.alpha(main, 0.5f));
        g.pop();
    }

    /** Flickering flame spike along +Y. */
    static void flame(Gfx g, double h, double r, float t, int i, int base, int tip) {
        double flick = 1.0 + 0.22 * Math.sin(t * 0.9 + i * 1.7) + 0.1 * Math.sin(t * 1.9 + i * 3.1);
        g.coneY(h * flick, r, 5, base, tip);
    }

    /** A burning orb with a long flame tail along -Z. r = radius. */
    static void fireOrb(Gfx g, double r, float t, long seed, double tail) {
        double pulse = 1.0 + 0.07 * Math.sin(t * 0.9);
        g.sphere(r * 0.52, 5, 8, Col.rgb(0xFFFFFF), Col.rgb(0xFFF0A0));
        g.sphere(r * 0.8 * pulse, 6, 9, Col.rgba(0xFFA020, 0.8f), Col.rgba(0xFFD040, 0.8f));
        g.sphere(r * 1.08 * pulse, 6, 10, Col.rgba(0xE03008, 0.38f), Col.rgba(0xFF6010, 0.38f));
        g.push();
        g.rotX(PI);
        g.coneZ(tail, r * 0.95, 8, Col.rgba(0xFF8010, 0.75f), Col.rgba(0xC01800, 0f));
        g.coneZ(tail * 0.6, r * 0.6, 8, Col.rgba(0xFFE060, 0.9f), Col.rgba(0xFF8000, 0f));
        g.pop();
        for (int i = 0; i < 7; i++) {
            double a = PI * 2 * i / 7 + t * 0.15;
            g.push();
            g.rotZ(a);
            g.rotX(PI - 0.45 - 0.2 * Math.sin(t * 0.8 + i));
            g.translate(0, 0, r * 0.7);
            g.coneZ(tail * (0.45 + 0.25 * Rnd.h(seed, i)), r * 0.28, 4, Col.rgba(0xFF5010, 0.7f), Col.rgba(0xFF2000, 0f));
            g.pop();
        }
    }

    /** Spiky flower of light: n spikes radiating in a plane (XY). */
    static void starburst(Gfx g, int n, double len, double w, int core, int tip, double spin) {
        g.push();
        g.rotZ(spin);
        for (int i = 0; i < n; i++) {
            double a = PI * 2 * i / n;
            g.rod(0, 0, 0, Math.cos(a) * len, Math.sin(a) * len, 0, w, w * 0.1, core, tip);
        }
        g.pop();
    }

    /** Translucent shell (sphere) with the standard layered look. */
    static void shell(Gfx g, double r, int lowCol, int highCol) {
        g.sphere(r, 8, 14, lowCol, highCol);
    }

    /** Orbiting set of shards/boxes around the Y axis. */
    static void swirlRing(Gfx g, int n, double radius, double y, double spin, double size, int col) {
        for (int i = 0; i < n; i++) {
            double a = spin + PI * 2 * i / n;
            g.cube(Math.cos(a) * radius, y, Math.sin(a) * radius, size, col);
        }
    }

    static void useMat(Gfx g, Mat m) {
        g.mat(m);
    }
}
