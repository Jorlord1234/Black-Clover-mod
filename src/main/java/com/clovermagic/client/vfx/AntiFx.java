package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Anti-Magic spells (Asta): pitch black with dark red, demon-slayer sword. */
final class AntiFx {
    private AntiFx() {
    }

    static int black(float a) {
        return Col.rgba(0x07070A, a);
    }

    static int grey(float a) {
        return Col.rgba(0x6A6A78, a);
    }

    static int red(float a) {
        return Col.rgba(0xB0101E, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case SUMMON_DEMON_SLAYER -> swordSummon(g, c, 1);
            case SUMMON_DEMON_DWELLER -> swordSummon(g, c, 2);
            case SUMMON_DEMON_DESTROYER -> swordSummon(g, c, 3);
            case SUMMON_DEMON_SLASHER -> swordSummon(g, c, 4);
            case DEVIL_UNION -> devilUnion(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    /** Flat blade made of cross-sections (z, half width); the last one is the tip (z, 0). Black with a light fuller. */
    private static void bladeStrip(Gfx g, double[][] sec, double tipZ, int body, int edge) {
        for (int i = 0; i + 1 < sec.length; i++) {
            double z0 = sec[i][0], w0 = sec[i][1], z1 = sec[i + 1][0], w1 = sec[i + 1][1];
            g.quad(-w0, 0, z0, w0, 0, z0, w1, 0, z1, -w1, 0, z1, body, body, body, body);
            g.rod(-w0, 0, z0, -w1, 0, z1, 0.012, 0.012, edge, edge);
            g.rod(w0, 0, z0, w1, 0, z1, 0.012, 0.012, edge, edge);
        }
        double[] last = sec[sec.length - 1];
        g.tri(-last[1], 0, last[0], last[1], 0, last[0], 0, 0, tipZ, body, body, edge);
    }

    /** The three summoned swords, drawn along +Z with the hilt at z<0.5: tier 1 Demon-Slayer, 2 Demon-Dweller, 3 Demon-Destroyer. */
    private static void sword(Gfx g, int tier) {
        int blade = Col.rgb(0x101016), edge = Col.rgb(0x8A8A9C), white = Col.rgb(0xE8E8F0), dark = Col.rgb(0x2A2A36);
        if (tier == 1) {
            // wide blade, narrow at the guard, widest near the top, pointed tip; thin long crossguard with ball ends
            bladeStrip(g, new double[][] {{0.5, 0.14}, {1.0, 0.40}, {2.2, 0.46}, {3.1, 0.46}}, 3.2, blade, edge);
            g.box(-0.03, 0.002, 0.6, 0.03, 0.02, 3.3, edge);
            for (int i = 0; i < 9; i++) {
                double z = 1.0 + 0.27 * i, x = (i % 2 == 0 ? -1 : 1) * (0.08 + 0.2 * Rnd.h(5, i)) * (0.4 + z * 0.15);
                g.tri(x, 0.003, z, x + 0.09, 0.003, z + 0.12, x - 0.02, 0.003, z + 0.2, white, white, white);
            }
            g.box(-0.9, -0.04, 0.38, 0.9, 0.04, 0.50, dark);
            g.sphere(0.07, 4, 6, edge, edge);
            g.push();
            g.translate(-0.92, 0, 0.44);
            g.sphere(0.07, 4, 6, edge, edge);
            g.pop();
            g.push();
            g.translate(0.92, 0, 0.44);
            g.sphere(0.07, 4, 6, edge, edge);
            g.pop();
            g.box(-0.04, -0.04, -0.4, 0.04, 0.04, 0.4, dark);
            g.push();
            g.translate(0, 0, -0.45);
            g.sphere(0.09, 5, 8, edge, edge);
            g.pop();
        } else if (tier == 2) {
            // long straight double-edged blade with an ornate white centre; small ornate guard, round pommel
            bladeStrip(g, new double[][] {{0.5, 0.12}, {1.0, 0.15}, {2.8, 0.15}, {3.4, 0.11}}, 4.1, blade, edge);
            g.box(-0.035, 0.002, 0.55, 0.035, 0.02, 3.6, white);
            for (int i = 0; i < 10; i++) {
                double z = 0.8 + 0.3 * i;
                g.push();
                g.translate(0, 0.004, z);
                g.rotY(PI / 4);
                g.box(-0.04, 0, -0.04, 0.04, 0.012, 0.04, white);
                g.pop();
            }
            g.box(-0.34, -0.04, 0.38, 0.34, 0.04, 0.47, edge);
            for (int sd = -1; sd <= 1; sd += 2) {
                g.push();
                g.translate(sd * 0.34, 0, 0.42);
                g.coneY(0.2, 0.06, 4, edge, dark);
                g.pop();
            }
            g.box(-0.04, -0.04, -0.3, 0.04, 0.04, 0.4, dark);
            g.push();
            g.translate(0, 0, -0.38);
            g.sphere(0.1, 5, 8, edge, edge);
            g.pop();
        } else if (tier == 4) {
            // katana: slim single-edged blade, round tsuba, wrapped handle
            bladeStrip(g, new double[][] {{0.6, 0.07}, {1.0, 0.08}, {3.4, 0.08}, {3.9, 0.06}}, 4.2, blade, edge);
            g.box(0.07, 0.002, 0.6, 0.085, 0.02, 3.9, white);
            g.push();
            g.translate(0, 0, 0.5);
            g.disc(0.2, 12, edge, dark);
            g.pop();
            g.box(-0.035, -0.035, -0.5, 0.035, 0.035, 0.5, dark);
            g.push();
            g.translate(0, 0, -0.55);
            g.sphere(0.06, 4, 6, edge, edge);
            g.pop();
        } else {
            // huge cleaver: straight spine, curved cutting edge, slanted tip, white scribble pattern and a clover mark; cross guard with big paddles
            double[][] sec = {{0.5, 0.10}, {1.2, 0.46}, {2.0, 0.62}, {2.8, 0.62}, {3.3, 0.52}};
            for (int i = 0; i + 1 < sec.length; i++) {
                g.quad(-0.22, 0, sec[i][0], sec[i][1], 0, sec[i][0], sec[i + 1][1], 0, sec[i + 1][0], -0.22, 0, sec[i + 1][0], blade, blade, blade, blade);
                g.rod(sec[i][1], 0, sec[i][0], sec[i + 1][1], 0, sec[i + 1][0], 0.014, 0.014, edge, edge);
            }
            g.tri(-0.22, 0, 3.3, 0.52, 0, 3.3, 0.2, 0, 3.85, blade, blade, edge);
            g.rod(-0.22, 0, 0.5, -0.22, 0, 3.3, 0.016, 0.016, edge, edge);
            for (int i = 0; i < 12; i++) {
                double z = 0.9 + 0.22 * i, x = -0.1 + 0.55 * Rnd.h(9, i);
                g.rod(x, 0.003, z, x + 0.12, 0.003, z + 0.16, 0.012, 0.012, white, white);
                g.rod(x + 0.05, 0.003, z, x - 0.04, 0.003, z + 0.2, 0.008, 0.008, white, white);
            }
            for (int i = 0; i < 3; i++) {
                double a = PI * 2 * i / 3 + PI / 2;
                g.push();
                g.translate(0.12 + Math.cos(a) * 0.07, 0.004, 3.0 + Math.sin(a) * 0.07);
                g.box(-0.04, 0, -0.04, 0.04, 0.01, 0.04, white);
                g.pop();
            }
            g.box(-0.62, -0.06, 0.34, 0.62, 0.06, 0.46, dark);
            for (int sd = -1; sd <= 1; sd += 2) {
                g.box(Math.min(sd * 0.46, sd * 0.78), -0.07, 0.24, Math.max(sd * 0.46, sd * 0.78), 0.07, 0.58, edge);
            }
            g.box(-0.05, -0.05, -0.15, 0.05, 0.05, 0.36, dark);
        }
    }

    /** The sword materialises in the caster's right hand above a rotating anti-magic circle (tier 1 slayer, 2 dweller, 3 destroyer). */
    private static void swordSummon(Gfx g, Ctx c, int tier) {
        g.alpha = c.fade(4, 10);
        double grow = Parts.ease(c.t / 10.0);
        g.push();
        g.translate(0, 0.05, 0);
        Parts.circle(g, 1.1 + 0.3 * tier, c.t * 0.12, red(0.95f), Col.rgb(0xFF4050));
        g.disc(1.0 + 0.3 * tier, 18, black(0.55f), black(0.1f));
        g.pop();
        g.push();
        g.rotY(-Math.toRadians(c.bodyYaw));
        g.translate(-0.5, 0.6 + 0.9 * grow, 0.45);
        g.rotX(-PI / 2);
        g.rotZ(c.t * 0.35);
        double sc = tier == 3 ? 0.5 : tier == 2 ? 0.45 : 0.45;
        g.scale(sc);
        g.translate(0, 0, -1.8);
        sword(g, tier);
        g.pop();
        for (int i = 0; i < 6 + 2 * tier; i++) {
            double a = c.t * 0.2 + PI * 2 * i / (6 + 2 * tier);
            double y = ((c.t * 0.04 + i * 0.13) % 1.0) * 2.2;
            double r = (0.9 + 0.2 * tier) * (1 - 0.3 * y / 2.2);
            g.push();
            g.translate(Math.cos(a) * r, y, Math.sin(a) * r);
            Parts.flame(g, 0.55, 0.09, c.t, i, black(0.9f), red(0.2f));
            g.pop();
        }
        if (tier >= 2) {
            for (int i = 0; i < 4; i++) {
                double a = PI * 2 * i / 4 + c.t * 0.1;
                g.rod(Math.cos(a) * 1.8, 0.1, Math.sin(a) * 1.8, Math.cos(a) * 0.45, 1.0, Math.sin(a) * 0.45, 0.05, 0.01, red(0.9f), black(0f));
            }
        }
        if (tier == 3) {
            for (int i = 0; i < 6; i++) {
                double a = PI * 2 * i / 6;
                g.push();
                g.translate(Math.cos(a) * 2.2, -0.05, Math.sin(a) * 2.2);
                g.rotZ(-Math.cos(a) * 0.3);
                g.rotX(Math.sin(a) * 0.3);
                g.coneY(1.2 * grow, 0.2, 4, black(0.95f), red(0.9f));
                g.pop();
            }
        }
    }

    private static void devilUnion(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = Math.min(1f, (1f - p) * 2.0f);
        double r = 1.0 + (c.len - 1.0) * Parts.ease(p * 2.2);
        double h = 0.3 + 1.2 * Math.sin(Math.PI * Math.min(1, p * 2.0));
        g.frustumY(h, r, r + 0.5, 28, black(0.8f), red(0.6f), 0);
        g.frustumY(7, 0.8, 0.3, 14, red(0.35f), black(0.05f), 0);
        g.frustumY(7, 0.35, 0.15, 10, Col.rgba(0xFF3040, 0.7f), black(0.05f), 0);
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10;
            double grow = Parts.ease((c.t - 2.0) / 5.0) * Parts.clamp01((c.life - c.t) / 8.0);
            g.push();
            g.translate(Math.cos(a) * 2.2, -0.05, Math.sin(a) * 2.2);
            g.rotZ(-Math.cos(a) * 0.3);
            g.rotX(Math.sin(a) * 0.3);
            g.coneY((1.0 + 0.8 * Rnd.h(c.seed, i)) * grow, 0.22, 5, black(0.95f), red(0.9f));
            g.pop();
        }
        g.translate(0, 0.05, 0);
        Parts.circle(g, 2.8, c.t * 0.08, red(0.95f), Col.rgb(0xFF4050));
        g.ring(0.2, r, 26, red(0.5f), black(0f));
    }
}
