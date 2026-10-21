package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Wind spells (Sylph style: white and pale green, thin curved blades and feathers). */
final class WindFx {
    private WindFx() {
    }

    static int white(float a) {
        return Col.rgba(0xFFFFFF, a);
    }

    static int mint(float a) {
        return Col.rgba(0x9FF5C8, a);
    }

    static int green(float a) {
        return Col.rgba(0x3CCB86, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case WIND_BLADE -> windBlade(g, c);
            case WIND_DASH -> windDash(g, c);
            case WHIRLWIND -> whirlwind(g, c);
            case GALE_WHITE_BOW -> galeBow(g, c);
            case THIEF_WIND_CLOUD -> thiefCloud(g, c);
            case SWIFT_WHITE_HAWK -> whiteHawk(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void windBlade(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 5.0);
        double z = 0.6 + (c.len - 0.6) * f;
        g.alpha = c.fade(0, 4);
        g.aim(c);
        for (int i = 0; i < 3; i++) {
            g.push();
            g.translate(0, 0, z - i * 0.55);
            g.rotZ(PI / 2 + 0.3);
            g.arcBlade(1.5 - i * 0.1, 0.5 - i * 0.1, 1.9, 14, 0.05, white(0.95f - i * 0.3f), green(0.1f));
            g.pop();
        }
        g.push();
        g.translate(0, 0, z);
        g.rotZ(0.3);
        for (int i = 0; i < 5; i++) {
            double a = PI * 2 * i / 5 + c.t * 0.6;
            g.rod(Math.cos(a) * 0.15, Math.sin(a) * 0.15, 0, Math.cos(a) * 0.35, Math.sin(a) * 0.35, -1.6, 0.03, 0.01, white(0.8f), mint(0f));
        }
        g.pop();
    }

    private static void windDash(Gfx g, Ctx c) {
        g.alpha = c.fade(0, 8);
        g.translate(0, 0.9, 0);
        g.aim(c);
        for (int i = 0; i < 9; i++) {
            double a = PI * 2 * Rnd.h(c.seed, i), r = 0.2 + 0.55 * Rnd.h(c.seed, i + 30);
            double len = 2.5 + 3.5 * Rnd.h(c.seed, i + 60);
            double x = Math.cos(a) * r, y = Math.sin(a) * r;
            double start = -0.2 - c.t * 0.25;
            g.rod(x, y, start, x, y, start - len, 0.03, 0.005, white(0.9f), mint(0f));
        }
        for (int i = 0; i < 4; i++) {
            g.push();
            g.translate(0, 0, -0.8 - i * 0.9 - c.t * 0.2);
            g.rotX(PI / 2);
            g.torus(0.5 + i * 0.18, 0.03, 16, 4, white(0.7f - i * 0.15f));
            g.pop();
        }
        g.frustumZ(-3.2, 0.2, 0.9, 10, mint(0.25f), mint(0f), 0);
    }

    private static void whirlwind(Gfx g, Ctx c) {
        float rise = (float) Parts.ease(c.t / 8.0);
        g.alpha = c.fade(0, 10);
        double H = 5.2 * rise;
        g.frustumY(H, 0.35, 2.7, 16, white(0.28f), mint(0.08f), 0);
        for (int lvl = 0; lvl < 8; lvl++) {
            double y = lvl * H / 8.0;
            double r = 0.5 + 2.2 * (y / Math.max(H, 0.1));
            double spin = c.t * (0.25 + lvl * 0.03) + lvl * 1.1;
            for (int k = 0; k < 2; k++) {
                g.push();
                g.translate(0, y, 0);
                g.rotY(spin + k * PI);
                g.arcBlade(r, 0.22, 1.7, 8, 0.035, white(0.9f), mint(0.2f));
                g.pop();
            }
        }
        for (int i = 0; i < 8; i++) {
            double a = c.t * 0.35 + PI * 2 * i / 8;
            double y = ((c.t * 0.05 + i * 0.125) % 1.0) * H;
            double r = 0.7 + 2.0 * (y / Math.max(H, 0.1));
            g.push();
            g.translate(Math.cos(a) * r, y, Math.sin(a) * r);
            g.rotY(a);
            g.cube(0, 0, 0, 0.14, white(0.9f));
            g.pop();
        }
        g.translate(0, 0.03, 0);
        g.ring(0.3, 2.2, 20, white(0.5f), mint(0f));
    }

    private static void galeBow(Gfx g, Ctx c) {
        g.aim(c);
        float bowA = c.fade(2, 5);
        double fly = Parts.easeIn((c.t - 3.0) / 6.0);
        double az = 0.9 + (c.len - 0.9) * fly;
        // bow (vertical crescent, tips toward the target)
        g.push();
        g.alpha = bowA;
        g.translate(0, 0, 1.15);
        g.rotZ(PI / 2);
        g.rotY(PI);
        g.arcBlade(1.1, 0.16, 2.4, 14, 0.05, white(1f), green(0.2f));
        g.pop();
        double tipY = 1.1 * Math.sin(1.2), tipZ = 1.15 - 1.1 * Math.cos(1.2) * -1 * 0 + 0;
        double pull = fly > 0 ? 0 : -0.2;
        g.rod(0, tipY, 1.15 + 0.5 * (1 - Math.cos(1.2)) * -1 + 0.7, 0, 0, 0.5 + pull, 0.01, 0.01, white(0.9f), white(0.9f));
        g.rod(0, -tipY, 1.15 + 0.5 * (1 - Math.cos(1.2)) * -1 + 0.7, 0, 0, 0.5 + pull, 0.01, 0.01, white(0.9f), white(0.9f));
        // arrow
        g.alpha = c.fade(0, 4);
        g.push();
        g.translate(0, 0, az);
        g.rod(0, 0, -1.2, 0, 0, 0.7, 0.025, 0.025, white(1f), white(1f));
        g.push();
        g.translate(0, 0, 0.7);
        g.coneZ(0.4, 0.07, 6, white(1f), mint(1f));
        g.pop();
        for (int s = -1; s <= 1; s += 2) {
            g.tri(0, 0, -1.2, s * 0.14, 0, -1.4, 0, 0, -0.9, green(0.9f), green(0.9f), white(0.9f));
            g.tri(0, 0, -1.2, 0, s * 0.14, -1.4, 0, 0, -0.9, green(0.9f), green(0.9f), white(0.9f));
        }
        g.pop();
        // spiral wind around the flight path
        int strands = 2;
        for (int s = 0; s < strands; s++) {
            double px = 0, py = 0, pz = 0.2;
            for (double d = 0.5; d <= az; d += 0.35) {
                double r = 0.22 + 0.05 * Math.sin(d + c.t);
                double a = d * 2.2 + c.t * 0.7 + s * PI;
                double x = Math.cos(a) * r, y = Math.sin(a) * r;
                float k = (float) (1 - d / Math.max(az, 1.0));
                g.rod(px, py, pz, x, y, d, 0.035, 0.035, white(0.8f * (0.3f + 0.7f * (1 - k))), mint(0.6f));
                px = x;
                py = y;
                pz = d;
            }
        }
    }

    private static void thiefCloud(Gfx g, Ctx c) {
        g.alpha = c.fade(6, 10);
        double rise = Parts.ease(c.t / 6.0);
        g.translate(0, -0.35 + 0.1 * Math.sin(c.t * 0.2), 0);
        double[][] puffs = {{0, 0, 0.55}, {0.55, 0.05, 0.4}, {-0.5, 0.05, 0.42}, {0.15, 0.0, -0.55}, {0.5, 0.02, -0.35},
                {-0.45, 0.03, -0.3}, {0.0, 0.18, 0.0}, {0.75, 0.0, 0.1}, {-0.75, 0.0, 0.05}};
        for (int i = 0; i < puffs.length; i++) {
            double[] p = puffs[i];
            g.push();
            g.translate(p[0] * rise, p[1] + 0.03 * Math.sin(c.t * 0.3 + i), p[2] * rise);
            g.scale(1, 0.62, 1);
            double s = 0.38 + 0.12 * Rnd.h(c.seed, i);
            g.sphere(s, 5, 8, white(0.9f), white(1f));
            g.pop();
        }
        g.push();
        g.translate(0, -0.15, 0);
        g.rotY(c.t * 0.2);
        g.ring(0.6, 1.1, 18, mint(0.5f), mint(0f));
        g.pop();
        for (int i = 0; i < 6; i++) {
            double a = c.t * 0.3 + PI * i / 3;
            g.rod(Math.cos(a) * 0.9, -0.1, Math.sin(a) * 0.9, Math.cos(a) * 0.9, -0.9 - 0.4 * Rnd.h(c.seed, i), Math.sin(a) * 0.9,
                    0.03, 0.005, white(0.7f), mint(0f));
        }
    }

    private static void whiteHawk(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 6.0);
        double z = 0.8 + (c.len - 0.8) * f;
        g.alpha = c.fade(0, 6);
        g.aim(c);
        g.frustumZ(z, 0.03, 0.4, 8, white(0f), mint(0.3f), 0);
        g.push();
        g.translate(0, 0, z);
        g.scale(1.4);
        double flap = Math.sin(c.t * 1.2) * 0.55;
        g.push();
        g.scale(0.2, 0.2, 0.6);
        g.sphere(1, 5, 8, white(1f), white(1f));
        g.pop();
        g.push();
        g.translate(0, 0.08, 0.62);
        g.sphere(0.17, 5, 8, white(1f), white(1f));
        g.translate(0, -0.02, 0.15);
        g.coneZ(0.2, 0.06, 4, Col.rgb(0xFFD25A), Col.rgb(0xFFD25A));
        g.pop();
        g.cube(0.1, 0.14, 0.7, 0.05, Col.rgb(0x203040));
        g.cube(-0.1, 0.14, 0.7, 0.05, Col.rgb(0x203040));
        for (int k = 0; k < 3; k++) {
            g.push();
            g.translate(0, 0, -0.55);
            g.rotY((k - 1) * 0.3);
            g.box(-0.05, -0.01, -0.5, 0.05, 0.01, 0, mint(0.95f));
            g.pop();
        }
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.14, 0.05, 0.1);
            g.rotZ(s * flap);
            for (int i = 0; i < 6; i++) {
                g.push();
                g.rotY(-s * (i - 1.5) * 0.17);
                double L = 0.75 + 0.28 * Math.sin(Math.PI * (i + 1) / 7.0);
                g.box(Math.min(0, s * L), -0.012, -0.1, Math.max(0, s * L), 0.012, 0.1,
                        i % 2 == 0 ? white(0.95f) : mint(0.9f));
                g.pop();
            }
            g.pop();
        }
        g.pop();
    }
}
