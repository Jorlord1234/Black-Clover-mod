package com.clovermagic.client.vfx;

import com.clovermagic.client.vfx.Gfx.Mat;
import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Spirit Dive: elemental spirit wings, a halo, orbiting orbs and a glyph. One model, four elements. */
final class SpiritFx {
    private SpiritFx() {
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case SPIRIT_DIVE_FIRE -> dive(g, c, 0xFF6A10, 0xFFE070, false, 0);
            case SPIRIT_DIVE_WATER -> dive(g, c, 0x2F8FE8, 0xD0F4FF, false, 1);
            case SPIRIT_DIVE_WIND -> dive(g, c, 0x6CE0A8, 0xFFFFFF, false, 2);
            case SPIRIT_DIVE_EARTH -> dive(g, c, 0xB0925A, 0xF0E0B0, true, 3);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void dive(Gfx g, Ctx c, int main, int bright, boolean stone, int kind) {
        g.alpha = c.fade(14, 24) * (c.firstPerson ? 0.55f : 1f);
        double grow = Parts.ease(c.t / 14.0);
        // wings behind the caster
        g.push();
        g.rotY(-Math.toRadians(c.bodyYaw));
        double flap = Math.sin(c.t * 0.12) * 0.12;
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.15, 1.35, -0.3);
            g.rotY(s * 0.5);
            for (int i = 0; i < 7; i++) {
                g.push();
                g.rotZ(s * (0.1 + i * 0.25 + flap));
                double L = (2.0 - i * 0.17) * grow;
                if (stone) {
                    g.mat(Mat.STONE);
                    g.push();
                    g.translate(s * L * 0.5, 0, 0);
                    g.rotZ(s * -PI / 2);
                    g.crystal(L, 0.11, 4, Col.rgb(0xB8B8B8), Col.rgb(0xFFFFFF));
                    g.pop();
                    g.mat(Mat.GLOW);
                } else if (kind == 0) {
                    g.push();
                    g.rotY(s * -PI / 2);
                    g.coneZ(L, 0.13, 4, Col.rgba(main, 0.85f), Col.rgba(0xC01800, 0f));
                    g.pop();
                } else {
                    g.box(Math.min(0, s * L), -0.015, -0.1, Math.max(0, s * L), 0.015, 0.1,
                            i % 2 == 0 ? Col.rgba(bright, 0.8f) : Col.rgba(main, 0.75f));
                }
                g.pop();
            }
            g.pop();
        }
        g.pop();
        // halo above the head
        g.push();
        g.translate(0, 2.55 + 0.05 * Math.sin(c.t * 0.1), 0);
        g.rotY(c.t * 0.08);
        g.torus(0.42, 0.04, 20, 4, Col.rgba(bright, 1f));
        g.torus(0.3, 0.025, 16, 4, Col.rgba(main, 0.9f));
        g.pop();
        // orbiting element orbs with a short tail
        for (int i = 0; i < 4; i++) {
            double a = c.t * 0.09 + PI / 2 * i;
            double y = 0.9 + 0.55 * Math.sin(c.t * 0.05 + i * 1.7);
            g.push();
            g.translate(Math.cos(a) * 1.15, y, Math.sin(a) * 1.15);
            g.sphere(0.12, 5, 8, Col.rgba(main, 0.95f), Col.rgba(bright, 0.95f));
            g.sphere(0.2, 5, 8, Col.rgba(main, 0.28f), Col.rgba(bright, 0.28f));
            g.pop();
            g.rod(Math.cos(a) * 1.15, y, Math.sin(a) * 1.15, Math.cos(a - 0.5) * 1.15, y, Math.sin(a - 0.5) * 1.15, 0.05, 0.005,
                    Col.rgba(main, 0.7f), Col.rgba(main, 0f));
        }
        g.push();
        g.translate(0, 0.04, 0);
        Parts.circle(g, 1.7, c.t * 0.05, Col.rgba(main, 0.9f), Col.rgba(bright, 1f));
        g.pop();
    }
}
