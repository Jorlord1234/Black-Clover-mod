package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Darkness spells: near-black smoke with violet edges. */
final class DarkFx {
    private DarkFx() {
    }

    static int black(float a) {
        return Col.rgba(0x0A0610, a);
    }

    static int purple(float a) {
        return Col.rgba(0x6A2BD0, a);
    }

    static int violet(float a) {
        return Col.rgba(0xB070FF, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case BLACK_SLASH -> blackSlash(g, c);
            case DARK_SHROUD -> darkShroud(g, c);
            case ABYSS_DRAIN -> abyssDrain(g, c);
            case DARK_PULSE -> darkPulse(g, c);
            case SHADOW_BIND -> shadowBind(g, c);
            case DREAD_MARK -> dreadMark(g, c);
            case AVIDYA_SLASH -> avidyaSlash(g, c);
            case DIMENSION_SLASH -> dimensionSlash(g, c);
            case BLACK_HOLE -> blackHole(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void blackSlash(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 4.0);
        double z = 0.6 + (c.len - 0.6) * f;
        g.alpha = c.fade(0, 5);
        g.aim(c);
        for (int k = 0; k < 2; k++) {
            double roll = (k == 0 ? 0.75 : -0.75);
            for (int i = 0; i < 3; i++) {
                g.push();
                g.translate(0, 0, z - i * 0.4);
                g.rotZ(PI / 2 + roll);
                g.arcBlade(1.3 - i * 0.12, 0.45 - i * 0.08, 1.8, 14, 0.05, violet(0.95f - i * 0.28f), black(0.85f));
                g.pop();
            }
        }
        g.frustumZ(z, 0.05, 0.5, 8, black(0f), black(0.45f), 0);
        g.push();
        g.translate(0, 0, z);
        g.sphere(0.16, 5, 8, violet(0.9f), Col.rgba(0xFFFFFF, 0.9f));
        g.pop();
    }

    private static void darkShroud(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = Math.min(1f, c.fade(3, 18) * 1.2f);
        double sw = 0.6 + 0.9 * p;
        for (int i = 0; i < 12; i++) {
            double a = c.t * 0.18 + PI * 2 * i / 12;
            double y = 0.1 + 1.9 * ((Rnd.h(c.seed, i) + c.t * 0.015) % 1.0);
            g.push();
            g.translate(Math.cos(a) * sw, y, Math.sin(a) * sw);
            g.scale(1.0, 0.8, 1.0);
            double s = 0.3 + 0.25 * Rnd.h(c.seed, i + 20);
            g.sphere(s, 5, 8, black(0.6f * c.shell()), purple(0.4f * c.shell()));
            g.pop();
        }
        g.push();
        g.translate(0, 0.95, 0);
        g.scale(0.8, 1.2, 0.8);
        g.sphere(0.8, 6, 10, black(0.35f * c.shell()), purple(0.2f * c.shell()));
        g.pop();
        g.translate(0, 0.04, 0);
        g.ring(0.4, 1.5, 18, black(0.7f), black(0f));
    }

    private static void abyssDrain(Gfx g, Ctx c) {
        g.alpha = c.fade(5, 10);
        double R = c.len;
        g.disc(R, 28, black(0.8f), purple(0.1f));
        for (int arm = 0; arm < 6; arm++) {
            double px = 0, pz = 0, py = 0;
            boolean first = true;
            for (double k = 0; k <= 1.0001; k += 0.06) {
                double r = R * (1 - k) + 0.4;
                double a = arm * PI / 3 + k * 3.2 - c.t * 0.12;
                double x = Math.cos(a) * r, z = Math.sin(a) * r, y = 0.1 + 0.9 * k * k;
                if (!first) g.rod(px, py, pz, x, y, z, 0.1 * (1.1 - k), 0.1 * (1.1 - k + 0.06), violet(0.35f + 0.5f * (float) k), purple(0.5f));
                first = false;
                px = x;
                py = y;
                pz = z;
            }
        }
        g.push();
        g.translate(0, 1.4 + 0.15 * Math.sin(c.t * 0.2), 0);
        g.sphere(0.55, 7, 12, black(1f), black(1f));
        g.push();
        g.rotX(0.5);
        g.rotY(c.t * 0.3);
        g.torus(0.85, 0.07, 22, 4, violet(0.95f));
        g.pop();
        g.sphere(0.75, 6, 10, purple(0.3f), purple(0.3f));
        g.pop();
        g.translate(0, 0.05, 0);
        g.torus(R, 0.08, 28, 4, purple(0.8f));
    }

    private static void darkPulse(Gfx g, Ctx c) {
        float grow = (float) Parts.ease(c.t / 6.0);
        g.alpha = c.fade(0, 8);
        g.aim(c);
        double L = c.len * grow;
        g.frustumZ(L, 0.1, L * 0.34, 10, black(0.6f), purple(0f), 0);
        for (int i = 0; i < 6; i++) {
            double z = (c.t * 1.2 + i * c.len / 6.0) % c.len;
            double r = 0.25 + z * 0.33;
            g.push();
            g.translate(0, 0, z);
            g.rotX(PI / 2);
            g.torus(r, 0.1, 18, 4, Col.lerp(violet(0.9f), black(0.4f), (float) (z / c.len)));
            g.pop();
        }
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8 + c.t * 0.2;
            g.push();
            g.translate(Math.cos(a) * 0.3, Math.sin(a) * 0.3, 0.6);
            g.rotX(-Math.sin(a) * 0.25);
            g.rotY(Math.cos(a) * 0.25);
            g.coneZ(L * (0.6 + 0.3 * Rnd.h(c.seed, i)), 0.12, 4, black(0.9f), purple(0f));
            g.pop();
        }
    }

    private static void shadowBind(Gfx g, Ctx c) {
        g.alpha = c.fade(4, 8);
        double w = Math.max(0.4, c.fw * 0.5) + 0.1, h = c.fh;
        double grow = Parts.ease(c.t / 8.0);
        g.push();
        g.translate(0, 0.04, 0);
        Parts.circle(g, w + 0.9, c.t * 0.05, purple(0.9f), violet(0.95f));
        g.disc(w + 0.8, 18, black(0.6f), black(0.2f));
        g.pop();
        for (int t = 0; t < 6; t++) {
            double a0 = PI * 2 * t / 6;
            double px = Math.cos(a0) * (w + 0.5), py = 0, pz = Math.sin(a0) * (w + 0.5);
            for (double k = 0.08; k <= 1.0001; k += 0.08) {
                double kk = k * grow;
                double a = a0 + kk * 5.0 + c.t * 0.05;
                double r = (w + 0.45) * (1 - 0.35 * kk);
                double x = Math.cos(a) * r, y = kk * (h + 0.3), z = Math.sin(a) * r;
                g.rod(px, py, pz, x, y, z, 0.11 * (1.1 - k * 0.7), 0.1 * (1.1 - k * 0.7), black(0.9f), purple(0.8f));
                px = x;
                py = y;
                pz = z;
            }
            g.push();
            g.translate(px, py, pz);
            g.sphere(0.1, 4, 6, violet(1f), violet(1f));
            g.pop();
        }
    }

    private static void dreadMark(Gfx g, Ctx c) {
        g.alpha = c.fade(6, 10);
        double y = c.fh + 0.9 + 0.1 * Math.sin(c.t * 0.12);
        double grow = Parts.ease(c.t / 6.0);
        g.push();
        g.translate(0, y, 0);
        g.rotY(c.t * 0.05);
        g.scale(grow);
        int red = Col.rgb(0xFF2A3A);
        g.torus(0.6, 0.035, 24, 4, violet(0.95f));
        g.torus(0.45, 0.02, 24, 4, purple(0.8f));
        for (int i = 0; i < 5; i++) {
            double a0 = PI * 2 * (i * 2 % 5) / 5, a1 = PI * 2 * ((i * 2 + 2) % 5) / 5;
            g.rod(Math.cos(a0) * 0.6, 0, Math.sin(a0) * 0.6, Math.cos(a1) * 0.6, 0, Math.sin(a1) * 0.6, 0.025, 0.025, violet(1f), violet(1f));
        }
        for (int i = 0; i < 5; i++) {
            double a = PI * 2 * i / 5;
            g.push();
            g.translate(Math.cos(a) * 0.62, 0, Math.sin(a) * 0.62);
            g.sphere(0.06, 4, 6, red, red);
            g.pop();
        }
        g.sphere(0.12, 5, 8, red, Col.rgb(0xFF9090));
        g.pop();
        // dark chains down to the target
        for (int i = 0; i < 3; i++) {
            double a = PI * 2 * i / 3 + c.t * 0.04;
            g.rod(Math.cos(a) * 0.4, y - 0.1, Math.sin(a) * 0.4, Math.cos(a) * 0.25, c.fh * 0.85, Math.sin(a) * 0.25, 0.02, 0.02, purple(0.8f), black(0.2f));
        }
    }

    /** Yami's wide flat slash: one huge horizontal crescent with a trailing cloak of shadow. */
    private static void avidyaSlash(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 5.0);
        double z = 0.8 + (c.len - 0.8) * f;
        g.alpha = c.fade(0, 6);
        g.aim(c);
        g.frustumZ(z, 0.05, 1.0, 10, black(0f), black(0.45f), 0);
        for (int i = 0; i < 4; i++) {
            g.push();
            g.translate(0, 0, z - i * 0.5);
            g.rotZ(0.12);
            g.arcBlade(2.6 - i * 0.15, 0.85 - i * 0.16, 2.3, 18, 0.07, violet(0.95f - i * 0.22f), black(0.9f - i * 0.1f));
            g.pop();
        }
        g.push();
        g.translate(0, 0, z + 0.2);
        g.rotZ(0.12);
        g.arcBlade(2.65, 0.08, 2.3, 18, 0.02, Col.rgba(0xFFFFFF, 0.95f), violet(0.2f));
        g.pop();
        for (int i = 0; i < 6; i++) {
            double a = Rnd.s(c.seed, i) * 1.0;
            g.rod(Math.sin(a) * 2.2, 0.0, z - 0.3, Math.sin(a) * 2.2, 0.0, z - 2.2, 0.04, 0.005, violet(0.8f), black(0f));
        }
    }

    /** Dimension Slash: a crescent flies out and tears a long rift in space along its whole path. */
    private static void dimensionSlash(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = c.fade(0, 8);
        g.aim(c);
        double travel = Parts.ease(c.t / 5.0);
        double L = c.len * travel;
        double open = Math.sin(Math.PI * Math.min(1.0, p * 1.15));
        g.push();
        g.translate(0, 0, 1.0 + L);
        g.rotZ(PI / 2);
        for (int i = 0; i < 3; i++) {
            g.push();
            g.translate(0, 0, -i * 0.5);
            g.arcBlade(3.0 - i * 0.2, 0.9 - i * 0.2, 2.0, 18, 0.07, violet(0.95f - i * 0.25f), black(0.9f));
            g.pop();
        }
        g.pop();
        int steps = (int) Math.max(4, L / 1.2);
        for (int plane = 0; plane < 3; plane++) {
            g.push();
            g.rotZ(plane * PI / 3);
            for (int i = 0; i < steps; i++) {
                double f0 = (double) i / steps, f1 = (double) (i + 1) / steps;
                double z0 = 1.0 + L * f0, z1 = 1.0 + L * f1;
                double h0 = 3.2 * open * Math.pow(Math.sin(Math.PI * (0.05 + 0.9 * f0)), 0.6), h1 = 3.2 * open * Math.pow(Math.sin(Math.PI * (0.05 + 0.9 * f1)), 0.6);
                g.quad(0, -h0, z0, 0, h0, z0, 0, h1, z1, 0, -h1, z1, black(0.9f), black(0.9f), black(0.9f), black(0.9f));
                g.rod(0, h0, z0, 0, h1, z1, 0.05, 0.05, violet(1f), violet(1f));
                g.rod(0, -h0, z0, 0, -h1, z1, 0.05, 0.05, violet(1f), violet(1f));
            }
            g.pop();
        }
        for (int i = 0; i < 12; i++) {
            double z = 1.0 + L * Rnd.h(c.seed, i);
            g.push();
            g.translate(Rnd.s(c.seed, i + 20) * 0.5, Rnd.s(c.seed, i + 40) * 2.4 * open, z);
            g.sphere(0.07, 4, 6, violet(0.9f), Col.rgba(0xFFFFFF, 0.9f));
            g.pop();
        }
    }

    /** A black hole floating where the spell pulls: void sphere, spinning accretion disc, lensing ring, inward streaks. */
    private static void blackHole(Gfx g, Ctx c) {
        float in = (float) Parts.ease(c.t / 6.0);
        g.alpha = c.fade(0, 10);
        g.aim(c);
        g.translate(0, 0, c.len);
        g.scale(Math.max(0.01, in));
        g.sphere(0.75, 8, 14, black(1f), black(1f));
        g.sphere(1.05, 7, 12, purple(0.18f), purple(0.22f));
        g.push();
        g.rotX(0.35);
        g.rotZ(0.25);
        g.rotY(c.t * 0.35);
        g.ring(0.95, 2.4, 28, Col.rgba(0xFFFFFF, 0.9f), purple(0.05f));
        for (int i = 0; i < 3; i++) {
            g.push();
            g.rotY(PI * 2 * i / 3);
            g.arcBlade(1.8, 0.4, 2.0, 10, 0.03, violet(0.9f), black(0.7f));
            g.pop();
        }
        g.pop();
        g.push();
        g.rotX(1.2);
        g.rotY(c.t * 0.2);
        g.torus(1.25, 0.05, 28, 4, violet(0.95f));
        g.pop();
        for (int i = 0; i < 14; i++) {
            double ph = (c.t * 0.05 + Rnd.h(c.seed, i)) % 1.0;
            double r = 5.0 * (1 - ph) + 0.9;
            double a = PI * 2 * Rnd.h(c.seed, i + 30) + ph * 4.0;
            double el = Rnd.s(c.seed, i + 60) * 0.8;
            double x = Math.cos(a) * Math.cos(el) * r, y = Math.sin(el) * r, z = Math.sin(a) * Math.cos(el) * r;
            double r2 = r * 0.88, a2 = a + 0.22;
            g.rod(x, y, z, Math.cos(a2) * Math.cos(el) * r2, Math.sin(el) * r2, Math.sin(a2) * Math.cos(el) * r2, 0.05, 0.02,
                    violet(0.9f * (float) (1 - ph)), purple(0.3f));
        }
    }
}
