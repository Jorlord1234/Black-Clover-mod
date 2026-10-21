package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Water spells (Sea Dragon style: deep blue body, light blue sheen, white foam). */
final class WaterFx {
    private WaterFx() {
    }

    static final int DEEP = Col.rgb(0x1E6FD8), MID = Col.rgb(0x3FA9F5), LIGHT = Col.rgb(0xBDEBFF), FOAM = Col.rgb(0xFFFFFF);

    static int deep(float a) {
        return Col.rgba(0x1E6FD8, a);
    }

    static int mid(float a) {
        return Col.rgba(0x3FA9F5, a);
    }

    static int white(float a) {
        return Col.rgba(0xFFFFFF, a);
    }

    static int light(float a) {
        return Col.rgba(0xBDEBFF, a);
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case WATER_BALL -> waterBall(g, c);
            case WATER_VEIL -> waterVeil(g, c);
            case SEA_DRAGON_WAVE -> seaDragonWave(g, c);
            case SEA_DRAGON_ROAR -> seaDragonRoar(g, c);
            case CLIMBING_COELACANTH -> coelacanth(g, c);
            case SEA_DRAGON_NEST -> seaDragonNest(g, c);
            case VALKYRIE_DRESS -> valkyrieDress(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void waterBall(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 5.0);
        double z = 0.4 + (c.len - 0.4) * f;
        g.alpha = c.fade(0, 4);
        g.aim(c);
        g.frustumZ(z, 0.02, 0.2, 8, deep(0f), mid(0.45f), 0);
        g.push();
        g.translate(0, 0, z);
        double w = 0.04 * Math.sin(c.t * 1.6);
        g.scale(1 + w, 1 - w, 1 + w);
        g.sphere(0.2, 6, 10, light(0.9f), FOAM);
        g.sphere(0.36, 7, 12, deep(0.55f), mid(0.55f));
        for (int i = 0; i < 4; i++) {
            double a = c.t * 0.5 + PI * i / 2;
            g.push();
            g.translate(Math.cos(a) * 0.5, Math.sin(a) * 0.5, -0.1 * i);
            g.sphere(0.07, 4, 6, mid(0.9f), light(0.9f));
            g.pop();
        }
        g.pop();
        if (f >= 1) {
            float k = (float) Math.min(1, (c.t - 5) / 6.0);
            g.push();
            g.translate(0, 0, c.len);
            for (int i = 0; i < 8; i++) {
                double a = PI * 2 * i / 8;
                g.rod(0, 0, 0, Math.cos(a) * (0.4 + k), Math.sin(a) * (0.4 + k), 0.3 * k, 0.07, 0.01, light(0.9f), deep(0f));
            }
            g.sphere(0.2 + 0.5 * k, 5, 8, mid(0.5f * (1 - k)), light(0.5f * (1 - k)));
            g.pop();
        }
    }

    private static void waterVeil(Gfx g, Ctx c) {
        float a = c.fade(8, 12);
        g.alpha = a;
        g.push();
        g.translate(0, 0.95, 0);
        g.scale(0.85, 1.2, 0.85);
        g.sphere(1.2, 8, 14, deep(0.20f * c.shell()), light(0.14f * c.shell()));
        g.pop();
        for (int i = 0; i < 3; i++) {
            g.push();
            g.translate(0, 0.95, 0);
            g.rotY(c.t * (0.05 + 0.02 * i) + i * 2.1);
            g.rotZ(0.9 * (i - 1) + 0.3);
            g.torus(1.15, 0.045, 24, 4, Col.alpha(light(0.8f), c.shell() > 0.5f ? 1f : 0.4f));
            g.pop();
        }
        for (int i = 0; i < 6; i++) {
            double ang = c.t * 0.08 + PI * 2 * i / 6;
            double y = 0.2 + ((c.t * 0.02 + i * 0.17) % 1.0) * 1.6;
            g.push();
            g.translate(Math.cos(ang) * 0.95, y, Math.sin(ang) * 0.95);
            g.sphere(0.07, 4, 6, mid(0.9f), light(0.9f));
            g.pop();
        }
        g.translate(0, 0.03, 0);
        g.ring(0.4, 1.4, 22, mid(0.5f), deep(0f));
    }

    private static void seaDragonWave(Gfx g, Ctx c) {
        float p = c.p();
        double r = 1.0 + (c.len - 1.0) * Parts.ease(p * 1.3);
        double h = 0.25 + 1.5 * Math.sin(Math.PI * Math.min(1, p * 1.2));
        g.alpha = Math.min(1f, (1f - p) * 2.2f);
        g.frustumY(h, r, r + h * 0.7, 28, deep(0.7f), light(0.75f), 0);
        g.push();
        g.translate(0, h, 0);
        g.torus(r + h * 0.7, 0.12, 28, 4, Col.alpha(FOAM, 0.9f));
        g.pop();
        double r2 = Math.max(0.3, r * 0.65);
        g.frustumY(h * 0.7, r2, r2 + h * 0.5, 24, deep(0.5f), mid(0.5f), 0);
        // foam blobs on the crest
        for (int i = 0; i < 16; i++) {
            double a = PI * 2 * i / 16 + p * 2;
            g.push();
            g.translate(Math.cos(a) * (r + h * 0.7), h + 0.1 * Math.sin(c.t + i), Math.sin(a) * (r + h * 0.7));
            g.sphere(0.13, 4, 6, light(0.9f), FOAM);
            g.pop();
        }
        g.translate(0, 0.03, 0);
        g.ring(0.5, r, 26, mid(0.4f), deep(0.05f));
    }

    private static void dragonHead(Gfx g, float t, float open) {
        // skull and snout
        g.boxG(-0.38, -0.25, -0.5, 0.38, 0.3, 0.55, deep(0.9f), mid(0.9f));
        g.boxG(-0.24, -0.1, 0.55, 0.24, 0.24, 1.45, deep(0.9f), mid(0.9f));
        g.box(-0.12, 0.24, 1.1, 0.12, 0.3, 1.45, light(0.9f));
        // lower jaw hinged under the skull
        g.push();
        g.translate(0, -0.22, 0.2);
        g.rotX(open);
        g.boxG(-0.2, -0.16, 0.0, 0.2, 0.0, 1.2, deep(0.9f), mid(0.9f));
        for (int i = 0; i < 4; i++) {
            for (int s = -1; s <= 1; s += 2) {
                g.push();
                g.translate(s * 0.17, 0, 0.3 + i * 0.27);
                g.coneY(0.14, 0.04, 4, FOAM, FOAM);
                g.pop();
            }
        }
        g.pop();
        for (int i = 0; i < 4; i++) {
            for (int s = -1; s <= 1; s += 2) {
                g.push();
                g.translate(s * 0.21, -0.1, 0.6 + i * 0.25);
                g.rotX(PI);
                g.coneY(0.17, 0.04, 4, FOAM, FOAM);
                g.pop();
            }
        }
        // mouth glow
        g.push();
        g.translate(0, -0.1, 0.9);
        g.sphere(0.16, 4, 6, FOAM, light(1f));
        g.pop();
        for (int s = -1; s <= 1; s += 2) {
            g.cube(s * 0.4, 0.14, 0.2, 0.14, FOAM);          // eyes
            g.push();                                      // horns sweeping back
            g.translate(s * 0.25, 0.28, -0.3);
            g.rotX(-1.2);
            g.rotZ(-s * 0.35);
            g.coneY(0.9, 0.11, 5, MID, light(0.8f));
            g.pop();
            g.push();                                      // side fins
            g.translate(s * 0.38, 0.0, -0.1);
            g.rotZ(-s * 1.1);
            g.box(0, -0.02, -0.3, 0.5, 0.02, 0.1, mid(0.75f));
            g.pop();
        }
        for (int i = 0; i < 4; i++) {
            g.push();
            g.translate(0, 0.3, -0.4 + i * 0.28);
            g.coneY(0.28 - i * 0.04, 0.07, 4, mid(0.9f), light(0.9f));
            g.pop();
        }
    }

    private static void seaDragonRoar(Gfx g, Ctx c) {
        g.alpha = c.fade(1, 6);
        g.aim(c);
        double f = Parts.ease(c.t / 9.0);
        double z = 1.0 + (c.len - 2.0) * f;
        float open = (float) (0.25 + 0.5 * Math.abs(Math.sin(c.t * 0.5)));
        // water jet behind the head
        g.frustumZ(z, 0.4, 0.9, 12, deep(0.05f), mid(0.45f), 0);
        g.frustumZ(z, 0.25, 0.55, 10, light(0.05f), light(0.7f), 0);
        for (int i = 0; i < 6; i++) {
            double zz = (c.t * 1.8 + i * z / 6) % z;
            g.push();
            g.translate(0, 0, zz);
            g.rotX(PI / 2);
            g.torus(0.4 + 0.5 * zz / z, 0.06, 16, 4, light(0.8f));
            g.pop();
        }
        g.push();
        g.translate(0, 0, z);
        g.scale(1.25);
        dragonHead(g, c.t, open);
        g.pop();
        // spray in front
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10;
            double d = ((c.t * 0.5 + i * 0.37) % 1.0);
            g.push();
            g.translate(Math.cos(a) * 0.5 * d, Math.sin(a) * 0.5 * d, z + 2.0 + d * 2.5);
            g.sphere(0.09, 4, 6, light(0.8f * (1 - (float) d)), FOAM);
            g.pop();
        }
    }

    private static void coelacanth(Gfx g, Ctx c) {
        double f = Math.min(1.0, c.t / 10.0);
        double climbF = Parts.clamp01((f - 0.55) / 0.45);
        double z = 0.5 + (c.len - 0.5) * Parts.ease(Math.min(1.0, f / 0.8));
        double y = 3.4 * Parts.easeIn(climbF);
        g.alpha = c.fade(0, 5);
        g.aim(c);
        // water trail
        g.frustumZ(z, 0.05, 0.35, 8, deep(0f), mid(0.35f), 0);
        g.push();
        g.translate(0, y, z);
        g.rotX(-climbF * 1.1);
        g.rotY(Math.sin(c.t * 0.9) * 0.18);
        g.scale(1.3);
        // body
        g.push();
        g.scale(0.42, 0.5, 1.05);
        g.sphere(0.8, 6, 10, deep(0.9f), mid(0.9f));
        g.pop();
        g.push();
        g.translate(0, 0.02, 0.8);
        g.scale(0.34, 0.38, 0.4);
        g.sphere(0.8, 5, 8, mid(0.9f), light(0.9f));          // head
        g.pop();
        g.cube(0.15, 0.1, 1.0, 0.07, FOAM);
        g.cube(-0.15, 0.1, 1.0, 0.07, FOAM);
        // tail with three-lobed fin
        double sw = Math.sin(c.t * 0.9) * 0.15;
        g.tri(0, 0.0, -0.8, sw - 0.5, 0.0, -1.5, sw, 0.55, -1.4, mid(0.9f), deep(0.6f), deep(0.6f));
        g.tri(0, 0.0, -0.8, sw + 0.5, 0.0, -1.5, sw, -0.55, -1.4, mid(0.9f), deep(0.6f), deep(0.6f));
        g.tri(0, 0.0, -0.8, sw, 0.05, -1.7, sw, 0.0, -1.4, light(0.9f), light(0.5f), light(0.5f));
        // dorsal fins and lobed pectoral fins
        g.tri(0, 0.45, 0.2, 0, 0.85, -0.3, 0, 0.45, -0.5, mid(0.9f), light(0.8f), mid(0.9f));
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.3, -0.2, 0.35);
            g.rotZ(-s * (0.5 + 0.2 * Math.sin(c.t * 0.9)));
            g.box(0, -0.02, -0.2, s * 0.5, 0.02, 0.2, light(0.85f));
            g.pop();
        }
        g.pop();
    }

    private static void seaDragonNest(Gfx g, Ctx c) {
        g.alpha = c.fade(10, 12);
        double spin = c.t * 0.05;
        g.push();
        g.translate(0, 1.2, 0);
        g.scale(1, 0.7, 1);
        g.sphere(3.1, 8, 16, deep(0.13f * c.shell()), light(0.08f * c.shell()));
        g.pop();
        int n = 28;
        double R = 2.3;
        for (int k = 0; k < n; k++) {
            double a = spin + k * 0.26;
            double y = 0.35 + k * 0.065 + 0.08 * Math.sin(c.t * 0.12 + k * 0.5);
            double size = k == 0 ? 0.0 : 0.34 - k * 0.0075;
            double x = Math.cos(a) * R, z = Math.sin(a) * R;
            if (k == 0) {
                // head, faces along the travel direction (tangent)
                g.push();
                g.translate(x, y, z);
                g.rotY(PI / 2 - a - PI / 2 + PI);
                g.scale(0.55);
                dragonHead(g, c.t, 0.3f);
                g.pop();
                continue;
            }
            g.push();
            g.translate(x, y, z);
            g.sphere(size, 5, 7, deep(0.9f), mid(0.9f));
            g.push();
            g.rotY(-a);
            g.translate(0, size * 0.8, 0);
            g.coneY(size * 1.0, size * 0.4, 4, mid(0.9f), light(0.9f));
            g.pop();
            g.pop();
        }
        // ground ripple
        g.translate(0, 0.03, 0);
        g.ring(1.2, 3.8, 28, mid(0.35f), deep(0f));
    }

    /** Armour of water: chest and back plates, pauldrons, gauntlets, skirt, greaves, feathered water wings and a winged circlet. */
    private static void valkyrieDress(Gfx g, Ctx c) {
        g.alpha = c.fade(10, 14) * (c.firstPerson ? 0.45f : 1f);
        g.rotY(-Math.toRadians(c.bodyYaw));
        double grow = Parts.ease(c.t / 10.0);
        int d = deep(0.62f), m = mid(0.68f), l = light(0.8f);
        g.boxG(-0.34, 0.95, 0.19, 0.34, 1.5, 0.34, d, l);
        g.boxG(-0.34, 0.95, -0.34, 0.34, 1.5, -0.19, d, m);
        g.boxG(-0.28, 0.72, 0.19, 0.28, 0.95, 0.3, m, d);
        g.frustumY(0.5, 0.3, 0.66 * grow + 0.1, 16, d, light(0.45f), 0);
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.44, 1.5, 0);
            g.scale(1.1, 0.7, 1.1);
            g.sphere(0.22, 5, 8, m, l);
            g.pop();
            g.box(Math.min(s * 0.36, s * 0.52), 0.75, -0.13, Math.max(s * 0.36, s * 0.52), 1.15, 0.13, m);
            g.box(Math.min(s * 0.02, s * 0.28), 0.05, 0.08, Math.max(s * 0.02, s * 0.28), 0.5, 0.28, d);
            g.push();
            g.translate(s * 0.3, 1.8, 0.0);
            g.rotZ(-s * 0.8);
            g.arcBlade(0.35, 0.12, 1.4, 6, 0.02, white(0.95f), mid(0.2f));
            g.pop();
            // wing of feathers (swept back)
            g.push();
            g.translate(s * 0.14, 1.35, -0.28);
            g.rotY(s * 0.55);
            double flap = Math.sin(c.t * 0.1) * 0.1;
            for (int i = 0; i < 6; i++) {
                g.push();
                g.rotZ(s * (0.2 + i * 0.27 + flap));
                double L = (1.5 - i * 0.14) * grow;
                g.box(Math.min(0, s * L), -0.015, -0.09, Math.max(0, s * L), 0.015, 0.09, i % 2 == 0 ? light(0.75f) : mid(0.7f));
                g.pop();
            }
            g.pop();
        }
        g.push();
        g.translate(0, 0.95, 0);
        g.rotY(c.t * 0.07);
        g.rotZ(0.25);
        g.torus(0.78, 0.035, 22, 4, white(0.9f * (c.firstPerson ? 0.3f : 1f)));
        g.pop();
        for (int i = 0; i < 6; i++) {
            double a = c.t * 0.08 + PI * 2 * i / 6;
            double y = ((c.t * 0.02 + i * 0.17) % 1.0) * 1.9;
            g.push();
            g.translate(Math.cos(a) * 0.7, y, Math.sin(a) * 0.7);
            g.sphere(0.06, 4, 6, mid(0.9f), light(0.9f));
            g.pop();
        }
        g.translate(0, 0.03, 0);
        g.ring(0.4, 1.4, 22, mid(0.5f), deep(0f));
    }
}
