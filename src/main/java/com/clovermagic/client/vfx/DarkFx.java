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

    /** Dark Cloaked Avidya Slash after the anime: a rough net of crisscrossing black slash lines cutting through the whole area ahead. */
    private static void avidyaSlash(Gfx g, Ctx c) {
        g.alpha = c.fade(0, 7);
        g.aim(c);
        double L = c.len;
        for (int i = 0; i < 28; i++) {
            double appear = Math.min(1.0, Math.max(0.0, (c.t - i * 0.13) / 2.5));
            if (appear <= 0) continue;
            double mz = 1.5 + (L - 1.5) * Rnd.h(c.seed, i), mr = 3.2 * Math.sqrt(Rnd.h(c.seed, i + 40)), ma = PI * 2 * Rnd.h(c.seed, i + 80);
            double mx = Math.cos(ma) * mr, my = Math.sin(ma) * mr * 0.8;
            double th = PI * Rnd.h(c.seed, i + 120), tz = 0.35 * Rnd.s(c.seed, i + 160);
            double dx = Math.cos(th), dy = Math.sin(th), dz = tz;
            double len = (3.0 + 4.0 * Rnd.h(c.seed, i + 200)) * appear;
            double ax = mx - dx * len, ay = my - dy * len, az = mz - dz * len;
            double bx = mx + dx * len, by = my + dy * len, bz = mz + dz * len;
            g.rod(ax, ay, az, bx, by, bz, 0.15, 0.15, Col.rgba(0x2A1448, 0.2f), Col.rgba(0x2A1448, 0.2f));
            g.rod(ax, ay, az, bx, by, bz, 0.05, 0.05, black(0.96f), black(0.96f));
            g.push();
            g.translate(bx, by, bz);
            g.cube(0, 0, 0, 0.12, black(0.9f));
            g.pop();
        }
        for (int i = 0; i < 14; i++) {
            double z = 1.5 + (L - 1.5) * Rnd.h(c.seed, i + 300), a = PI * 2 * Rnd.h(c.seed, i + 340), r = 3.0 * Rnd.h(c.seed, i + 380);
            g.push();
            g.translate(Math.cos(a) * r, Math.sin(a) * r, z);
            g.cube(0, 0, 0, 0.08, black(0.8f));
            g.pop();
        }
        g.push();
        g.translate(-0.3, -0.1, 0.5);
        g.box(-0.28, -0.2, 0, 0.28, 0.2, 0.03, Col.rgba(0xC040FF, 0.7f * c.fade(0, 4)));
        g.pop();
    }

    /**
     * Dimension Slash: the view itself splits in two. A straight vertical cut opens forward from the caster; the two halves of space
     * slide apart and shear, with a black void between them and a thin white-violet seam. No flying blade, just the cut.
     */
    private static void dimensionSlash(Gfx g, Ctx c) {
        g.aim(c);
        double travel = Parts.ease(c.t / 3.0);
        double z0 = 1.5, z1 = z0 + (c.len - z0) * travel;
        double H = 5.5;
        double open = Parts.ease(c.t / 6.0) * Parts.clamp01((c.life - c.t) / 6.0);
        double gap = 0.55 * open;
        double shear = 0.65 * open;
        g.alpha = c.fade(0, 5);
        // void between the halves
        g.quad(-gap, -H, z0, gap, -H, z0, gap, H, z0, -gap, H, z0, black(0.96f), black(0.96f), black(0.96f), black(0.96f));
        g.quad(-gap, -H, z0, -gap, H, z0, -gap, H, z1, -gap, -H, z1, black(0.96f), black(0.96f), black(0.96f), black(0.96f));
        g.quad(gap, -H, z0, gap, H, z0, gap, H, z1, gap, -H, z1, black(0.96f), black(0.96f), black(0.96f), black(0.96f));
        // the two displaced halves of space: dark sheets fading out to the sides, one sliding up and the other down
        for (int sd = -1; sd <= 1; sd += 2) {
            double sy = sd * shear;
            double x0 = sd * gap, x1 = sd * (gap + 3.2);
            g.quad(x0, -H + sy, z0, x1, -H + sy, z0, x1, H + sy, z0, x0, H + sy, z0, violet(0.55f), violet(0f), violet(0f), violet(0.55f));
            g.quad(x0, -H + sy, z0, x1, -H + sy, z0, x1, -H + sy, z1, x0, -H + sy, z1, violet(0.3f), violet(0f), violet(0f), violet(0.3f));
            g.quad(x0, -H + sy, z0, x0, H + sy, z0, x0, H + sy, z1, x0, -H + sy, z1, black(0.8f), black(0.8f), black(0.8f), black(0.8f));
            g.quad(x0, -H + sy, z1, x1, -H + sy, z1, x1, H + sy, z1, x0, H + sy, z1, purple(0.35f), purple(0f), purple(0f), purple(0.35f));
            // slice edge glow
            g.rod(x0, -H + sy, z0, x0, H + sy, z0, 0.04, 0.04, Col.rgba(0xFFFFFF, 0.95f), Col.rgba(0xFFFFFF, 0.95f));
            g.rod(x0, -H + sy, z1, x0, H + sy, z1, 0.04, 0.04, Col.rgba(0xFFFFFF, 0.95f), Col.rgba(0xFFFFFF, 0.95f));
            g.rod(x0, H + sy, z0, x0, H + sy, z1, 0.03, 0.03, Col.rgba(0xFFFFFF, 0.8f), Col.rgba(0xFFFFFF, 0.8f));
            g.rod(x0, -H + sy, z0, x0, -H + sy, z1, 0.03, 0.03, Col.rgba(0xFFFFFF, 0.8f), Col.rgba(0xFFFFFF, 0.8f));
        }
        // shards of space tumbling out of the cut
        for (int i = 0; i < 14; i++) {
            double z = z0 + (z1 - z0) * Rnd.h(c.seed, i), y = H * 1.1 * Rnd.s(c.seed, i + 20);
            double side = (i % 2 == 0 ? 1 : -1) * (gap + 0.2 + 1.4 * Rnd.h(c.seed, i + 40) * open);
            g.push();
            g.translate(side, y, z);
            g.rotY(c.t * 0.1 + i);
            g.rotX(c.t * 0.08 + i);
            g.tri(0, 0, 0, 0.28, 0.1, 0, 0.05, 0.3, 0.08, black(0.9f), purple(0.8f), black(0.9f));
            g.pop();
        }
    }

    /** Black Hole after the manga: a perfectly black sphere with thin white rings around it, black jagged streaks pulled toward it. */
    private static void blackHole(Gfx g, Ctx c) {
        float in = (float) Parts.ease(c.t / 6.0);
        g.alpha = c.fade(0, 10);
        g.aim(c);
        g.translate(0, 0, c.len);
        g.scale(Math.max(0.01, in));
        g.sphere(0.95, 8, 14, black(1f), black(1f));
        g.sphere(1.3, 7, 12, Col.rgba(0x000000, 0.28f), Col.rgba(0x000000, 0.28f));
        int ring = Col.rgba(0xFFFFFF, 0.95f);
        g.push();
        g.rotX(PI / 2);
        g.torus(1.4, 0.03, 36, 4, ring);
        g.torus(2.2, 0.018, 36, 4, Col.rgba(0xFFFFFF, 0.5f));
        g.pop();
        g.push();
        g.rotX(1.2);
        g.rotY(c.t * 0.1);
        g.torus(1.7, 0.02, 36, 4, Col.rgba(0xE8E8F8, 0.6f));
        g.pop();
        for (int i = 0; i < 16; i++) {
            double ph = (c.t * 0.05 + Rnd.h(c.seed, i)) % 1.0;
            double r = 4.2 * (1 - ph) + 1.5;
            double a = PI * 2 * Rnd.h(c.seed, i + 30), el = Rnd.s(c.seed, i + 60) * 0.9;
            double x = Math.cos(a) * Math.cos(el), y = Math.sin(el), z = Math.sin(a) * Math.cos(el);
            g.push();
            g.lookFromTo(x * r, y * r, z * r, 0, 0, 0);
            g.coneZ(1.1 + 0.8 * (1 - ph), 0.09 + 0.07 * (1 - ph), 3, black(0.95f), black(0.95f));
            g.pop();
        }
    }
}
