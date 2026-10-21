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
            case DEVIL_UNION -> devilUnion(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void sword(Gfx g) {
        int blade = Col.rgb(0x15151C), edge = Col.rgb(0x7A7A8C);
        g.box(-0.13, -0.025, 0.5, 0.13, 0.025, 3.0, blade);
        g.box(-0.13, 0.025, 0.5, -0.1, 0.03, 3.0, edge);
        for (int i = 0; i < 6; i++) {
            double z = 0.9 + i * 0.38;
            g.tri(0.13, 0, z, 0.27, 0, z + 0.12, 0.13, 0, z + 0.3, edge, blade, blade);
            g.tri(-0.13, 0, z + 0.1, -0.24, 0, z + 0.2, -0.13, 0, z + 0.34, edge, blade, blade);
        }
        g.tri(-0.13, 0, 3.0, 0.13, 0, 3.0, 0.0, 0, 3.35, blade, blade, edge);
        g.box(-0.4, -0.05, 0.38, 0.4, 0.05, 0.5, Col.rgb(0x2A2A36));     // crossguard
        g.box(-0.05, -0.05, -0.2, 0.05, 0.05, 0.38, Col.rgb(0x3A2A20));  // grip
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
        double sc = tier == 3 ? 0.62 : tier == 2 ? 0.5 : 0.42;
        g.scale(sc);
        g.translate(0, 0, -1.6);
        sword(g);
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
