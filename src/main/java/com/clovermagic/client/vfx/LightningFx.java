package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Lightning spells: white-gold cores with blue glow, jagged bolts that re-roll a few times a second. */
final class LightningFx {
    private LightningFx() {
    }

    static final int CORE = Col.rgb(0xFFFFE0);

    static int gold(float a) {
        return Col.rgba(0xFFD83A, a);
    }

    static int blue(float a) {
        return Col.rgba(0x5EA8FF, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case THUNDER_BOLT -> thunderStrike(g, c);
            case LIGHTNING_SPEED -> lightningSpeed(g, c);
            case THUNDER_CHAIN -> arc(g, c, 0.06, 0.4, true);
            case STATIC_SHOCK -> staticShock(g, c);
            case SPARK_BARRAGE -> sparkArc(g, c);
            case OVERCHARGE -> overcharge(g, c);
            case TRUE_LIGHTNING -> trueLightning(g, c);
            case LIGHTNING_BATTLE_FIEND -> battleFiend(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void thunderStrike(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = Math.min(1f, (1f - p) * 1.6f);
        double r = 0.5 + 3.5 * Parts.ease(p * 2.0);
        g.frustumY(14, 0.7, 0.35, 8, blue(0.45f), blue(0.05f), 0);
        g.frustumY(14, 0.3, 0.12, 8, Col.rgba(0xFFFFFF, 0.8f), Col.rgba(0xFFFFFF, 0.1f), 0);
        g.ring(0.2, r, 24, gold(0.7f), blue(0f));
        g.push();
        g.translate(0, 0.06, 0);
        g.torus(r, 0.1, 24, 4, gold(0.85f));
        g.pop();
        g.disc(1.1, 14, Col.rgba(0x101018, 0.7f), Col.rgba(0x101018, 0f));
        long s = c.seed + (long) (c.t / 2);
        for (int i = 0; i < 6; i++) {
            double a = PI * 2 * i / 6 + 0.4 * Rnd.s(s, i);
            g.bolt(0, 0.1, 0, Math.cos(a) * r * 0.95, 0.1, Math.sin(a) * r * 0.95, 5, 0.3, s * 7 + i, 0.04, CORE, blue(0.6f));
        }
        g.push();
        g.translate(0, 0.6, 0);
        g.sphere(0.5 * (1 - p), 5, 8, Col.rgba(0xFFFFFF, 0.9f), gold(0.9f));
        g.pop();
    }

    private static void lightningSpeed(Gfx g, Ctx c) {
        g.alpha = c.fade(5, 10);
        long s = c.seed * 31 + (long) (c.t / 2);
        for (int i = 0; i < 7; i++) {
            double a = PI * 2 * Rnd.h(s, i), a2 = a + Rnd.s(s, i + 10) * 1.3;
            double y1 = 0.2 + 1.6 * Rnd.h(s, i + 20), y2 = 0.2 + 1.6 * Rnd.h(s, i + 30);
            g.bolt(Math.cos(a) * 0.4, y1, Math.sin(a) * 0.4, Math.cos(a2) * 0.85, y2, Math.sin(a2) * 0.85, 4, 0.16, s * 11 + i,
                    0.025, CORE, blue(0.65f));
        }
        g.push();
        g.translate(0, 0.9, 0);
        g.scale(0.7, 1.15, 0.7);
        g.sphere(0.75, 6, 10, blue(0.08f * c.shell()), gold(0.1f * c.shell()));
        g.pop();
        g.translate(0, 0.04, 0);
        g.rotY(c.t * 0.3);
        g.ring(0.5, 1.0, 14, gold(0.6f), blue(0f));
        for (int i = 0; i < 4; i++) {
            double a = PI / 2 * i;
            g.rod(Math.cos(a) * 0.5, 0, Math.sin(a) * 0.5, Math.cos(a + 0.5) * 1.3, 0, Math.sin(a + 0.5) * 1.3, 0.03, 0.005, gold(0.9f), blue(0f));
        }
    }

    /** Bolt along local +Z; used by Thunder Chain. */
    private static void arc(Gfx g, Ctx c, double w, double jag, boolean flash) {
        g.alpha = c.fade(0, 5);
        g.aim(c);
        double L = c.len;
        long s = c.seed + (long) (c.t * 0.8);
        int segs = (int) Math.max(4, L * 2.5);
        g.bolt(0, 0, 0, 0, 0, L, segs, jag, s, w, CORE, gold(0.7f));
        for (int i = 0; i < 2; i++) {
            double f = 0.3 + 0.4 * Rnd.h(s, i + 60);
            double bx = Rnd.s(s, i + 70) * 1.0, by = Rnd.s(s, i + 80) * 1.0;
            g.bolt(0, 0, L * f, bx, by, L * f + 1.0, 3, 0.2, s + i * 13, w * 0.6, CORE, blue(0.6f));
        }
        if (flash) {
            g.push();
            g.translate(0, 0, L);
            g.sphere(0.25, 4, 6, Col.rgba(0xFFFFFF, 0.9f), gold(0.8f));
            g.pop();
        }
    }

    private static void staticShock(Gfx g, Ctx c) {
        arc(g, c, 0.035, 0.22, false);
        g.alpha = c.fade(0, 6);
        g.aim(c);
        g.push();
        g.translate(0, 0, c.len);
        long s = c.seed + (long) (c.t * 0.8);
        g.sphere(0.3 + 0.04 * Math.sin(c.t * 2), 5, 8, blue(0.6f), Col.rgba(0xE8F4FF, 0.85f));
        for (int i = 0; i < 6; i++) {
            double az = PI * 2 * Rnd.h(s, i), el = Rnd.s(s, i + 9) * 1.2;
            g.bolt(0, 0, 0, Math.cos(az) * Math.cos(el) * 0.8, Math.sin(el) * 0.8, Math.sin(az) * Math.cos(el) * 0.8, 3, 0.1, s + i, 0.02, CORE, blue(0.6f));
        }
        g.pop();
    }

    private static void sparkArc(Gfx g, Ctx c) {
        arc(g, c, 0.03, 0.25, true);
        g.alpha = c.fade(0, 4);
        g.aim(c);
        double z = c.len * Parts.clamp01(c.t / 3.0);
        g.push();
        g.translate(0, 0, z);
        g.sphere(0.13, 4, 6, gold(1f), Col.rgba(0xFFFFFF, 1f));
        g.pop();
    }

    private static void overcharge(Gfx g, Ctx c) {
        g.alpha = c.fade(8, 12);
        g.push();
        g.translate(0, 0.95, 0);
        g.scale(0.75, 1.2, 0.75);
        g.sphere(0.78, 6, 10, gold(0.14f * c.shell()), gold(0.2f * c.shell()));
        g.pop();
        for (int i = 0; i < 2; i++) {
            g.push();
            g.translate(0, 0.95, 0);
            g.rotY(c.t * (0.14 + 0.05 * i) * (i == 0 ? 1 : -1));
            g.rotZ(0.6 + 1.0 * i);
            g.torus(0.95, 0.04, 22, 4, gold(0.95f));
            g.pop();
        }
        g.push();
        g.translate(0, 2.35, 0);
        g.rotY(c.t * 0.1);
        g.torus(0.38, 0.035, 16, 4, gold(1f));
        g.pop();
        long s = c.seed * 17 + (long) (c.t / 2);
        for (int i = 0; i < 5; i++) {
            double a = PI * 2 * i / 5 + c.t * 0.05;
            g.bolt(Math.cos(a) * 0.9, 0.05, Math.sin(a) * 0.9, Math.cos(a) * 0.6, 1.2 + 0.8 * Rnd.h(s, i), Math.sin(a) * 0.6, 4, 0.18, s * 5 + i,
                    0.03, CORE, gold(0.6f));
        }
        g.translate(0, 0.04, 0);
        g.ring(0.6, 1.5, 22, gold(0.55f), gold(0f));
    }

    /** The dash as a bolt: long thick bolt along the whole path, flat flashing ribbons, flash at both ends. */
    private static void trueLightning(Gfx g, Ctx c) {
        g.alpha = c.fade(0, 6);
        g.aim(c);
        double L = c.len;
        long s = c.seed + (long) (c.t * 0.7);
        int segs = (int) Math.max(6, L * 1.6);
        g.bolt(0, 0, 0, 0, 0, L, segs, 0.35, s, 0.09, CORE, blue(0.65f));
        for (int i = 0; i < 4; i++) {
            double f = 0.15 + 0.2 * i;
            g.bolt(0, 0, L * f, Rnd.s(s, i + 5) * 1.4, Rnd.s(s, i + 9) * 1.4, L * f + 1.8, 4, 0.25, s + i * 7, 0.045, CORE, gold(0.6f));
        }
        for (int k = 0; k < 2; k++) {
            g.push();
            g.rotZ(k * PI / 2 + 0.4);
            g.box(-0.6, -0.02, 0.2, 0.6, 0.02, L, Col.rgba(0x9CC8FF, 0.35f));
            g.pop();
        }
        for (int i = 0; i < 4; i++) {
            double z = L * (i + 0.5) / 4;
            g.push();
            g.translate(0, 0, z);
            g.rotX(PI / 2);
            g.torus(0.35 + 0.15 * Math.sin(c.t + i), 0.03, 14, 4, gold(0.8f));
            g.pop();
        }
        g.sphere(0.4 * (1 - c.p()), 5, 8, Col.rgba(0xFFFFFF, 0.9f), gold(0.8f));
        g.push();
        g.translate(0, 0, L);
        g.sphere(0.5, 5, 8, Col.rgba(0xFFFFFF, 0.85f), blue(0.7f));
        Parts.starburst(g, 8, 1.1, 0.05, CORE, blue(0f), c.t * 0.2);
        g.pop();
    }

    /**
     * Luck's Lightning Battle Fiend: glowing white-blue body with lightning markings and spiky hair, six big glowing orbs hanging
     * behind the caster, a dark horned beast looming behind them, drifting snow-like sparks and bolts that re-roll every few ticks.
     */
    private static void battleFiend(Gfx g, Ctx c) {
        float fade = c.fade(10, 16);
        g.alpha = fade;
        long s = c.seed * 29 + (long) (c.t / 2);
        int white = Col.rgb(0xFFFFFF), ice = Col.rgb(0x7FD8FF), deep = Col.rgb(0x1E78E0);
        float sh = c.shell();
        g.push();
        g.rotY(-Math.toRadians(c.bodyYaw));
        // ---- glowing body shell (player-shaped)
        int body = Col.rgba(0xBFEFFF, 0.30f * sh);
        g.boxG(-0.27, 0.72, -0.17, 0.27, 1.48, 0.17, Col.rgba(0x7FD8FF, 0.3f * sh), body);
        g.box(-0.52, 0.72, -0.12, -0.28, 1.48, 0.12, body);
        g.box(0.28, 0.72, -0.12, 0.52, 1.48, 0.12, body);
        g.box(-0.27, 0.0, -0.13, -0.01, 0.72, 0.13, body);
        g.box(0.01, 0.0, -0.13, 0.27, 0.72, 0.13, body);
        g.push();
        g.translate(0, 1.5, 0);
        g.sphere(0.34, 5, 8, Col.rgba(0xBFEFFF, 0.3f * sh * 0.5f), Col.rgba(0xFFFFFF, 0.3f * sh * 0.5f));
        g.pop();
        // ---- spiky hair crown
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10 + 0.3;
            g.push();
            g.translate(Math.cos(a) * 0.14, 2.02, Math.sin(a) * 0.14);
            g.rotZ(-Math.cos(a) * (0.45 + 0.3 * Rnd.h(c.seed, i)));
            g.rotX(Math.sin(a) * (0.45 + 0.3 * Rnd.h(c.seed, i + 9)));
            g.coneY(0.45 + 0.35 * Rnd.h(c.seed, i + 20), 0.07, 4, white, Col.rgba(0xBFEFFF, 0.1f));
            g.pop();
        }
        // ---- lightning markings on chest, arms and legs
        int dk = Col.rgba(0x0A3C8C, 0.95f);
        g.bolt(-0.2, 1.45, 0.19, 0.08, 1.05, 0.19, 5, 0.07, 3, 0.012, dk, Col.rgba(0xFFFFFF, 0.5f));
        g.bolt(0.15, 1.4, 0.19, -0.05, 0.8, 0.19, 5, 0.06, 5, 0.012, dk, Col.rgba(0xFFFFFF, 0.5f));
        for (int sd = -1; sd <= 1; sd += 2) {
            g.bolt(sd * 0.4, 1.46, 0.13, sd * 0.4, 0.78, 0.13, 6, 0.06, 7 + sd, 0.012, dk, Col.rgba(0xFFFFFF, 0.5f));
            g.bolt(sd * 0.14, 0.7, 0.14, sd * 0.14, 0.05, 0.14, 6, 0.05, 11 + sd, 0.012, dk, Col.rgba(0xFFFFFF, 0.5f));
        }
        // ---- six big glowing orbs hanging behind the caster
        for (int i = 0; i < 6; i++) {
            double th = Math.toRadians(-75 + i * 30);
            double bob = 0.1 * Math.sin(c.t * 0.07 + i * 1.3);
            double x = Math.sin(th) * 2.3, z = -0.6 - Math.cos(th) * 1.9;
            double y = 1.5 + 0.9 * Math.cos(th * 1.6) + bob + (i % 2 == 0 ? 0.35 : -0.15);
            g.push();
            g.translate(x, y, z);
            g.sphere(0.34, 7, 12, white, white);
            g.sphere(0.5, 7, 12, Col.rgba(0x7FD8FF, 0.35f), Col.rgba(0xBFEFFF, 0.35f));
            g.sphere(0.7, 6, 10, Col.rgba(0x1E90FF, 0.1f), Col.rgba(0x7FD8FF, 0.1f));
            g.pop();
        }
        // ---- the dark horned beast looming behind
        g.push();
        g.translate(0, 2.0, -3.6);
        g.sphere(1.3, 7, 12, Col.rgba(0x05060C, 0.4f * sh), Col.rgba(0x0A1030, 0.4f * sh));
        for (int sd = -1; sd <= 1; sd += 2) {
            double[][] pts = new double[9][];
            for (int i = 0; i < 9; i++) {
                double f = i / 8.0;
                pts[i] = new double[] {sd * (0.8 + 1.7 * Math.sin(f * 1.3)), 0.6 + 1.6 * Math.sin(f * 1.0) - 2.0 * f * f, 0.3 * f};
            }
            Parts.tube(g, pts, 0.2, 0.04, Col.rgba(0x1C1C2A, 0.8f * sh), Col.rgba(0x4A4A60, 0.8f * sh));
            g.push();
            g.translate(sd * 0.5, 0.3, 1.0);
            g.scale(1.0, 0.5, 0.3);
            g.sphere(0.4, 5, 8, Col.rgba(0xC040B0, 0.9f), Col.rgba(0xFF80E0, 0.9f));
            g.pop();
        }
        g.pop();
        g.pop();
        // ---- drifting sparks like snow, and bolts that re-roll
        for (int i = 0; i < 26; i++) {
            double ph = (c.t * 0.01 + Rnd.h(c.seed, i)) % 1.0;
            double a = PI * 2 * Rnd.h(c.seed, i + 40), r = 0.8 + 3.6 * Rnd.h(c.seed, i + 80);
            g.push();
            g.translate(Math.cos(a) * r, 4.0 - ph * 4.2, Math.sin(a) * r);
            g.rotY(c.t * 0.05 + i);
            g.rotX(c.t * 0.04 + i);
            g.box(-0.05, -0.012, -0.025, 0.05, 0.012, 0.025, Col.rgba(0xFFFFFF, 0.9f));
            g.pop();
        }
        for (int i = 0; i < 9; i++) {
            double a = PI * 2 * Rnd.h(s, i), a2 = a + Rnd.s(s, i + 10) * 1.5;
            double y1 = 0.1 + 1.9 * Rnd.h(s, i + 20), y2 = 0.1 + 1.9 * Rnd.h(s, i + 30);
            g.bolt(Math.cos(a) * 0.4, y1, Math.sin(a) * 0.4, Math.cos(a2) * 1.3, y2, Math.sin(a2) * 1.3, 4, 0.2, s * 13 + i, 0.028, CORE, blue(0.7f));
        }
        g.push();
        g.translate(0, 0.05, 0);
        g.rotY(c.t * 0.25);
        g.ring(0.6, 1.8, 20, Col.rgba(0xFFFFFF, 0.5f), blue(0f));
        for (int i = 0; i < 6; i++) {
            double a = PI * i / 3;
            g.bolt(0, 0, 0, Math.cos(a) * 3.2, 0.05, Math.sin(a) * 3.2, 6, 0.3, s * 5 + i, 0.03, CORE, blue(0.55f));
        }
        g.pop();
    }
}
