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

    /** Sea dragon head after the anime: long upturned snout, wide-open jaws with rows of fangs, glowing cyan eye, big serrated frill. Faces +Z. */
    private static void dragonHead(Gfx g, float t, float open) {
        int navy = Col.rgba(0x0A2A66, 0.95f);
        g.boxG(-0.42, -0.25, -0.5, 0.42, 0.38, 0.4, deep(0.92f), mid(0.92f));
        // upper jaw: slanted up, nose lump at the tip
        g.push();
        g.translate(0, 0.02, 0.4);
        g.rotX(-0.22);
        g.boxG(-0.3, -0.05, 0, 0.3, 0.3, 1.5, deep(0.92f), mid(0.92f));
        g.push();
        g.translate(0, 0.3, 1.45);
        g.scale(0.9, 0.7, 1.1);
        g.sphere(0.3, 5, 8, mid(0.95f), light(0.95f));
        g.pop();
        for (int i = 0; i < 8; i++) {
            for (int s = -1; s <= 1; s += 2) {
                g.push();
                g.translate(s * 0.27, -0.05, 0.15 + i * 0.17);
                g.rotX(PI);
                g.coneY(0.2 + 0.05 * (i % 2), 0.045, 4, FOAM, light(0.9f));
                g.pop();
            }
        }
        g.pop();
        // lower jaw, dropped open
        g.push();
        g.translate(0, -0.2, 0.15);
        g.rotX(open);
        g.boxG(-0.28, -0.22, 0, 0.28, 0.0, 1.35, deep(0.92f), mid(0.92f));
        for (int i = 0; i < 8; i++) {
            for (int s = -1; s <= 1; s += 2) {
                g.push();
                g.translate(s * 0.25, 0.0, 0.12 + i * 0.16);
                g.coneY(0.19 + 0.05 * (i % 2), 0.045, 4, FOAM, light(0.9f));
                g.pop();
            }
        }
        g.pop();
        // dark throat
        g.boxG(-0.24, -0.22, 0.05, 0.24, 0.05, 0.85, navy, navy);
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.4, 0.2, 0.2);                 // glowing cyan eye
            g.scale(0.5, 1.0, 1.4);
            g.sphere(0.14, 5, 8, Col.rgb(0x9FFAFF), Col.rgb(0xFFFFFF));
            g.pop();
            // serrated frill fanning back from the jaw hinge
            double[] len = {1.1, 0.7, 1.25, 0.8, 1.3, 0.85, 1.1, 0.7};
            double base = 0.0;
            double px = s * 0.42, py = 0.0, pz = -0.15;
            double prevX = 0, prevY = 0, prevZ = 0;
            for (int i = 0; i < len.length; i++) {
                double phi = Math.toRadians(10 + i * 18);
                double tx = s * 0.55, ty = Math.sin(phi) * len[i] * 0.9 - 0.15, tz = -0.15 - Math.cos(phi) * len[i] * 1.1;
                if (i > 0) g.tri(px, py, pz, prevX, prevY, prevZ, tx, ty, tz, mid(0.9f), light(0.8f), light(0.8f));
                prevX = tx;
                prevY = ty;
                prevZ = tz;
            }
            for (int i = 0; i < 3; i++) {
                g.push();
                g.translate(s * 0.32, 0.38, -0.3 + i * 0.28);
                g.coneY(0.3 - i * 0.05, 0.07, 4, mid(0.9f), light(0.9f));
                g.pop();
            }
        }
    }

    private static void seaDragonRoar(Gfx g, Ctx c) {
        g.alpha = c.fade(1, 6);
        g.aim(c);
        double f = Parts.ease(c.t / 9.0);
        double z = 1.5 + (c.len - 3.0) * f;
        // thick coiling jet of striped water, spiralling from the caster to the head
        int n = 22;
        double px = 0, py = 0, pz = 0;
        for (int k = 0; k < n; k++) {
            double fk = (double) k / n;
            double zz = z * fk;
            double rho = 0.15 + 0.55 * fk;
            double a = k * 0.75 - c.t * 0.35;
            double cx = Math.cos(a) * rho, cy = Math.sin(a) * rho;
            double rad = 0.3 + 0.55 * fk;
            int col = (k % 2 == 0) ? Col.rgba(0xEAF8FF, 0.85f) : Col.rgba(0x2A6FD0, 0.78f);
            g.push();
            g.translate(cx, cy, zz);
            g.frustumZ(z / n * 1.15, rad, rad * 1.1, 10, col, col, 0);
            g.pop();
            g.push();
            g.translate(cx * 0.9, cy * 0.9, zz);
            g.frustumZ(z / n * 1.1, rad * 1.5, rad * 1.6, 10, Col.rgba(0xEAF8FF, 0.16f), Col.rgba(0xEAF8FF, 0.16f), 0);
            g.pop();
        }
        for (int i = 0; i < 7; i++) {
            double a = c.t * 0.3 + PI * 2 * i / 7;
            g.rod(Math.cos(a) * 1.3, Math.sin(a) * 1.3, z * 0.2, Math.cos(a + 0.8) * 1.5, Math.sin(a + 0.8) * 1.5, z * 0.75, 0.04, 0.01, mid(0.8f), deep(0f));
        }
        g.push();
        g.translate(0, 0, z);
        g.scale(1.4);
        dragonHead(g, c.t, (float) (0.5 + 0.2 * Math.sin(c.t * 0.5)));
        g.pop();
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10;
            double d = ((c.t * 0.5 + i * 0.37) % 1.0);
            g.push();
            g.translate(Math.cos(a) * 0.7 * d, Math.sin(a) * 0.7 * d, z + 1.8 + d * 2.8);
            g.sphere(0.1, 4, 6, light(0.8f * (1 - (float) d)), FOAM);
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

    /** Translucent dome (upper half of a sphere). */
    private static void dome(Gfx g, double r, int lat, int lon, int cLow, int cHigh) {
        for (int i = 0; i < lat; i++) {
            double p0 = PI / 2 * i / lat, p1 = PI / 2 * (i + 1) / lat;
            int k0 = Col.lerp(cLow, cHigh, (float) i / lat), k1 = Col.lerp(cLow, cHigh, (float) (i + 1) / lat);
            for (int j = 0; j < lon; j++) {
                double a0 = PI * 2 * j / lon, a1 = PI * 2 * (j + 1) / lon;
                g.quad(r * Math.cos(p0) * Math.cos(a0), r * Math.sin(p0), r * Math.cos(p0) * Math.sin(a0),
                        r * Math.cos(p0) * Math.cos(a1), r * Math.sin(p0), r * Math.cos(p0) * Math.sin(a1),
                        r * Math.cos(p1) * Math.cos(a1), r * Math.sin(p1), r * Math.cos(p1) * Math.sin(a1),
                        r * Math.cos(p1) * Math.cos(a0), r * Math.sin(p1), r * Math.cos(p1) * Math.sin(a0), k0, k0, k1, k1);
            }
        }
    }

    /** Sea Dragon's Nest after the anime: a big blue water dome with three spiral swirls on its surface and a spiky white foam ring at the base. */
    private static void seaDragonNest(Gfx g, Ctx c) {
        g.alpha = c.fade(10, 14);
        double R = Math.max(3.0, c.len * 0.9);
        double grow = Parts.ease(c.t / 10.0);
        double r = R * grow;
        float sh = c.shell();
        dome(g, r, 8, 24, deep(0.36f * sh), mid(0.28f * sh));
        double[][] centers = {{200, 48}, {335, 42}, {95, 52}};
        float vis = c.firstPerson ? 0.45f : 1f;
        for (int sp = 0; sp < centers.length; sp++) {
            double az = Math.toRadians(centers[sp][0]), el = Math.toRadians(centers[sp][1]);
            double nx = Math.cos(el) * Math.cos(az), ny = Math.sin(el), nz = Math.cos(el) * Math.sin(az);
            double e1x = -Math.sin(az), e1y = 0, e1z = Math.cos(az);
            double e2x = ny * e1z - nz * e1y, e2y = nz * e1x - nx * e1z, e2z = nx * e1y - ny * e1x;
            for (int arm = 0; arm < 2; arm++) {
                double[] prev = null;
                for (int i = 0; i <= 56; i++) {
                    double a = i * 0.28;
                    double rho = 0.025 + a * 0.0225;
                    double ang = a + arm * PI + c.t * 0.04 * (sp % 2 == 0 ? 1 : -1);
                    double x = nx + (Math.cos(ang) * e1x + Math.sin(ang) * e2x) * rho;
                    double y = ny + (Math.cos(ang) * e1y + Math.sin(ang) * e2y) * rho;
                    double z = nz + (Math.cos(ang) * e1z + Math.sin(ang) * e2z) * rho;
                    double l = Math.sqrt(x * x + y * y + z * z);
                    double[] pt = {x / l * r * 0.99, y / l * r * 0.99, z / l * r * 0.99};
                    if (prev != null && pt[1] > 0.05) {
                        int col = arm == 0 ? Col.rgba(0xEAF8FF, 0.8f * vis) : Col.rgba(0x4FA8F0, 0.7f * vis);
                        g.rod(prev[0], prev[1], prev[2], pt[0], pt[1], pt[2], 0.085, 0.085, col, col);
                    }
                    prev = pt;
                }
            }
        }
        for (int i = 0; i < 40; i++) {
            double a = PI * 2 * i / 40;
            g.push();
            g.translate(Math.cos(a) * r * 0.99, -0.05, Math.sin(a) * r * 0.99);
            g.rotY(-a);
            g.rotZ(0.15 * Math.sin(i * 1.7));
            g.coneY((0.55 + 0.4 * Rnd.h(c.seed, i)) * grow, 0.22, 4, Col.rgba(0xFFFFFF, 0.95f), Col.rgba(0xBDEBFF, 0.5f));
            g.pop();
        }
        g.translate(0, 0.03, 0);
        g.ring(r - 0.5, r + 0.55, 36, FOAM, Col.rgba(0xFFFFFF, 0.1f));
    }

    /**
     * Noelle's Valkyrie Dress, all of water: ornate scale-patterned armour (tiara, ear guards, scaled pauldrons, bell skirt, tall
     * heeled boots) with foam tufts, two huge pointed water-blade wings, swirling wind streaks and drifting spray.
     */
    private static void valkyrieDress(Gfx g, Ctx c) {
        g.alpha = c.fade(10, 14) * (c.firstPerson ? 0.5f : 1f);
        double grow = Parts.ease(c.t / 10.0);
        int body = Col.rgba(0x7FC4F0, 0.62f), bodyD = Col.rgba(0x3F86D8, 0.66f), pale = Col.rgba(0xEAF8FF, 0.9f), foam = Col.rgba(0xFFFFFF, 0.95f);
        g.push();
        g.rotY(-Math.toRadians(c.bodyYaw));
        // tiara of rounded spikes, ear guards
        for (int i = 0; i < 7; i++) {
            double a = PI * 2 * i / 7;
            g.push();
            g.translate(Math.cos(a) * 0.28, 2.02, Math.sin(a) * 0.28);
            g.coneY(0.22 + 0.08 * (i % 2), 0.05, 5, bodyD, pale);
            g.translate(0, 0.24 + 0.08 * (i % 2), 0);
            g.sphere(0.045, 4, 6, pale, foam);
            g.pop();
        }
        for (int sd = -1; sd <= 1; sd += 2) {
            g.push();
            g.translate(sd * 0.32, 1.72, 0);
            g.sphere(0.14 * (c.firstPerson ? 0.3 : 1.0), 5, 8, body, pale);
            g.pop();
        }
        // chest: scaled breastplate with a collar frill
        g.boxG(-0.34, 0.95, 0.18, 0.34, 1.5, 0.34, bodyD, body);
        g.boxG(-0.34, 0.95, -0.34, 0.34, 1.5, -0.18, bodyD, body);
        for (int row = 0; row < 3; row++) {
            for (int i = 0; i < 4; i++) {
                g.push();
                g.translate(-0.24 + i * 0.16 + (row % 2) * 0.08, 1.38 - row * 0.15, 0.345);
                g.scale(1.0, 0.8, 0.35);
                g.sphere(0.075, 4, 6, pale, foam);
                g.pop();
            }
        }
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8;
            g.push();
            g.translate(Math.cos(a) * 0.26, 1.52, Math.sin(a) * 0.26);
            g.sphere(0.06, 4, 6, pale, foam);
            g.pop();
        }
        // shoulders and sleeves
        for (int sd = -1; sd <= 1; sd += 2) {
            g.push();
            g.translate(sd * 0.46, 1.52, 0);
            g.scale(1.0, 0.75, 1.1);
            g.sphere(0.26, 5, 8, body, pale);
            g.pop();
            for (int i = 0; i < 3; i++) {
                g.push();
                g.translate(sd * (0.55 + i * 0.05), 1.6 - i * 0.1, 0);
                g.rotZ(-sd * (0.9 + 0.2 * i));
                g.coneY(0.28, 0.07, 4, pale, foam);
                g.pop();
            }
            g.box(Math.min(sd * 0.36, sd * 0.53), 0.74, -0.13, Math.max(sd * 0.36, sd * 0.53), 1.3, 0.13, body);
            for (int i = 0; i < 4; i++) {
                g.push();
                g.translate(sd * 0.5, 0.78, -0.1 + i * 0.07);
                g.rotZ(-sd * 1.2);
                g.coneY(0.2, 0.045, 4, foam, pale);
                g.pop();
            }
            // thigh-high scaled boots with a frill at the knee and a heel
            double x0 = sd * 0.02, x1 = sd * 0.3;
            g.boxG(Math.min(x0, x1), 0.0, 0.06, Math.max(x0, x1), 0.85, 0.3, bodyD, body);
            g.box(Math.min(x0, x1), 0.0, -0.1, Math.max(x0, x1), 0.85, 0.06, body);
            for (int i = 0; i < 6; i++) {
                double a = PI * 2 * i / 6;
                g.push();
                g.translate(sd * 0.16 + Math.cos(a) * 0.16, 0.78, 0.18 + Math.sin(a) * 0.13);
                g.rotZ(-Math.cos(a) * 0.8);
                g.rotX(Math.sin(a) * 0.8);
                g.coneY(0.2, 0.06, 4, pale, foam);
                g.pop();
            }
            for (int row = 0; row < 3; row++) {
                g.push();
                g.translate(sd * 0.16, 0.2 + row * 0.2, 0.31);
                g.scale(1.2, 0.8, 0.4);
                g.sphere(0.085, 4, 6, pale, foam);
                g.pop();
            }
            g.push();
            g.translate(sd * 0.16, 0.0, -0.1);
            g.rotX(PI);
            g.coneY(0.18, 0.05, 4, bodyD, pale);
            g.pop();
        }
        // layered bell skirt with a foam hem
        g.frustumY(0.42, 0.3, 0.62, 18, bodyD, body, 0);
        g.push();
        g.translate(0, 0.52, 0);
        g.frustumY(0.4, 0.28, 0.5, 16, body, pale, 0);
        g.pop();
        for (int i = 0; i < 16; i++) {
            double a = PI * 2 * i / 16;
            g.push();
            g.translate(Math.cos(a) * 0.63, 0.5, Math.sin(a) * 0.63);
            g.sphere(0.065, 4, 6, pale, foam);
            g.pop();
        }
        g.pop();
        // two huge pointed water-blade wings sweeping out and down, with foam at the roots
        for (int sd = -1; sd <= 1; sd += 2) {
            g.push();
            g.rotY(-Math.toRadians(c.bodyYaw));
            g.translate(sd * 0.2, 1.4, -0.3);
            double flap = Math.sin(c.t * 0.07 + sd) * 0.05;
            g.lookFromTo(0, 0, 0, sd * 2.6, -1.2 + flap * 3, -1.7);
            double L = 3.6 * grow;
            g.tri(-0.45, 0, 0, 0.45, 0, 0, 0, 0, L, Col.rgba(0xBDEBFF, 0.7f), Col.rgba(0x3F86D8, 0.55f), Col.rgba(0xFFFFFF, 0.95f));
            g.tri(-0.45, 0, 0, -0.62, 0, L * 0.35, 0, 0, L, Col.rgba(0x7FC4F0, 0.6f), Col.rgba(0x3F86D8, 0.5f), Col.rgba(0xFFFFFF, 0.9f));
            g.tri(0.45, 0, 0, 0.62, 0, L * 0.35, 0, 0, L, Col.rgba(0x7FC4F0, 0.6f), Col.rgba(0x3F86D8, 0.5f), Col.rgba(0xFFFFFF, 0.9f));
            g.rod(0, 0, 0.2, 0, 0, L, 0.035, 0.012, foam, foam);
            for (int i = 0; i < 7; i++) {
                g.push();
                g.translate((i % 2 == 0 ? -1 : 1) * 0.3, 0, 0.2 + i * 0.12);
                g.coneZ(0.7 + 0.1 * i, 0.13, 4, foam, Col.rgba(0xBDEBFF, 0.1f));
                g.pop();
            }
            g.pop();
        }
        // swirling wind streaks and spray
        for (int i = 0; i < 3; i++) {
            g.push();
            g.translate(0, 0.5 + i * 0.6, 0);
            g.rotY(c.t * 0.1 + i * 2.1);
            g.arcBlade(1.5 + 0.35 * i, 0.22, 1.9, 12, 0.02, Col.rgba(0xEAF8FF, 0.75f), Col.rgba(0x7FC4F0, 0f));
            g.pop();
        }
        for (int i = 0; i < 16; i++) {
            double a = PI * 2 * Rnd.h(c.seed, i), r = 0.9 + 2.2 * Rnd.h(c.seed, i + 30);
            double ph = (c.t * 0.012 + Rnd.h(c.seed, i + 60)) % 1.0;
            g.push();
            g.translate(Math.cos(a) * r, 3.4 - ph * 3.6, Math.sin(a) * r);
            g.sphere(0.045, 3, 5, foam, foam);
            g.pop();
        }
        g.translate(0, 0.03, 0);
        g.ring(0.4, 1.5, 22, Col.rgba(0x7FC4F0, 0.5f), Col.rgba(0x3F86D8, 0f));
    }
}
