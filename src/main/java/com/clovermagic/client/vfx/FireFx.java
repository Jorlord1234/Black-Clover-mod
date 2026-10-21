package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/** Fire spells (Crimson Lion style: gold core, orange flame, red edge). */
final class FireFx {
    private FireFx() {
    }

    static final int WHITE = Col.rgb(0xFFFFFF), CORE = Col.rgb(0xFFF0A0), YEL = Col.rgb(0xFFC830),
            ORA = Col.rgb(0xFF7A10), RED = Col.rgb(0xD02A0A);

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case FLAME_BULLET -> flameBullet(g, c);
            case EXPLODING_FIREBALL -> muzzle(g, c, 1.0f, 1);
            case EXPLODING_BUCKSHOT -> muzzle(g, c, 0.8f, 5);
            case SPIRAL_FLAME -> spiralFlame(g, c);
            case SOL_LINEA -> solLinea(g, c);
            case LEO_RUGIENS -> leoRugiens(g, c);
            case LEO_PALMA -> leoPalma(g, c);
            case IGNIS_COLUMNA -> ignisColumna(g, c);
            case FIREBALL_PROJECTILE -> {
                g.aim(c);
                Parts.fireOrb(g, 0.5 * c.len, c.t, c.seed, 2.4 * c.len);
            }
            case FIREBALL_BURST -> fireballBurst(g, c);
            case CALIDUS_BRACHIUM -> calidusBrachium(g, c);
            case CALIDUS_BRACHIUM_BARRAGE -> calidusBarrage(g, c);
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void flameBullet(Gfx g, Ctx c) {
        float travel = Math.min(1f, c.t / 4f);
        double z = 0.2 + (c.len - 0.2) * travel;
        g.alpha = c.fade(0, 5);
        g.aim(c);
        g.rod(0, 0, 0.1, 0, 0, z, 0.03, 0.09, Col.rgba(0xFF8010, 0f), Col.rgba(0xFFC040, 0.8f));
        g.push();
        g.translate(0, 0, z);
        Parts.fireOrb(g, 0.22, c.t, c.seed, 1.3);
        g.pop();
        // impact flash
        if (travel >= 1f) {
            float k = (c.t - 4f) / 6f;
            g.push();
            g.translate(0, 0, c.len);
            g.sphere(0.2 + 0.6 * k, 6, 10, Col.rgba(0xFFB030, 0.7f * (1 - k)), Col.rgba(0xFFE080, 0.7f * (1 - k)));
            g.pop();
        }
    }

    /** Magic circle floating in front of the caster's hand. n>1 adds small satellites (buckshot). */
    private static void muzzle(Gfx g, Ctx c, float size, int n) {
        float k = c.fade(2, 7);
        g.alpha = k;
        g.aim(c);
        g.translate(0, 0, 0.5);
        g.rotX(PI / 2);   // circle plane faces forward
        Parts.circle(g, 0.65 * size * (0.6 + 0.4 * Math.min(1, c.t / 3)), c.t * 0.25, ORA, CORE);
        if (n > 1) {
            for (int i = 0; i < n; i++) {
                double a = PI * 2 * i / n + c.t * 0.3;
                g.push();
                g.translate(Math.cos(a) * 0.9, 0.02, Math.sin(a) * 0.9);
                g.sphere(0.1, 4, 6, ORA, CORE);
                g.pop();
            }
        }
    }

    private static void spiralFlame(Gfx g, Ctx c) {
        float prog = Math.min(1f, c.t / 5f);
        double L = c.len * prog;
        g.alpha = c.fade(0, 6);
        g.aim(c);
        int strands = 3;
        for (int s = 0; s < strands; s++) {
            double off = PI * 2 * s / strands + c.t * 0.5;
            double px = 0, py = 0, pz = 0.3;
            for (double d = 0.6; d <= L; d += 0.3) {
                double r = 0.15 + d * 0.07;
                double a = off + d * 1.9;
                double x = Math.cos(a) * r, y = Math.sin(a) * r;
                float f = (float) (d / Math.max(L, 0.1));
                g.rod(px, py, pz, x, y, d, 0.1 + 0.05 * f, 0.1 + 0.05 * f, Col.lerp(CORE, ORA, f), Col.lerp(CORE, ORA, f));
                px = x;
                py = y;
                pz = d;
            }
        }
        g.frustumZ(L, 0.07, 0.35 + L * 0.03, 8, Col.rgba(0xFFE070, 0.55f), Col.rgba(0xFF5000, 0.12f), 0);
    }

    private static void solLinea(Gfx g, Ctx c) {
        g.alpha = c.fade(1, 6);
        g.aim(c);
        double L = c.len;
        double w = 0.12 + 0.05 * Math.sin(c.t * 1.3);
        g.frustumZ(L, w * 0.9, w * 0.9, 6, WHITE, WHITE, 3);
        g.frustumZ(L, w * 2.2, w * 2.2, 8, Col.rgba(0xFFD040, 0.55f), Col.rgba(0xFFA020, 0.55f), 3);
        g.frustumZ(L, w * 4.0, w * 4.0, 8, Col.rgba(0xFF6010, 0.2f), Col.rgba(0xFF3000, 0.2f), 0);
        // sun rings sliding down the beam
        for (int i = 0; i < 5; i++) {
            double z = ((c.t * 2.5 + i * L / 5) % L);
            g.push();
            g.translate(0, 0, z);
            g.rotX(PI / 2);
            g.torus(w * 4.5, w * 0.35, 14, 4, Col.rgba(0xFFD040, 0.85f));
            g.pop();
        }
        g.push();
        g.rotX(PI / 2);
        Parts.circle(g, 0.6, c.t * 0.3, ORA, CORE);
        g.pop();
        g.push();
        g.translate(0, 0, L);
        g.sphere(0.35, 6, 10, Col.rgba(0xFFFFFF, 0.9f), Col.rgba(0xFFE070, 0.9f));
        g.pop();
    }

    private static void leoRugiens(Gfx g, Ctx c) {
        float grow = Math.min(1f, c.t / 6f);
        g.alpha = c.fade(0, 7);
        g.aim(c);
        double L = c.len * grow;
        // roar cone, nested
        g.frustumZ(L, 0.15, L * 0.33, 10, Col.rgba(0xFFD040, 0.5f), Col.rgba(0xFF4000, 0.0f), 0);
        g.frustumZ(L * 0.8, 0.1, L * 0.22, 10, Col.rgba(0xFFF0A0, 0.6f), Col.rgba(0xFF8000, 0.0f), 0);
        // shock rings along the cone
        for (int i = 0; i < 5; i++) {
            double z = ((c.t * 1.4 + i * 1.8) % c.len);
            double r = 0.3 + z * 0.3;
            g.push();
            g.translate(0, 0, z);
            g.rotX(PI / 2);
            g.torus(r, 0.06, 16, 4, Col.rgba(0xFFB030, 0.7f * (1f - (float) (z / c.len))));
            g.pop();
        }
        // flaming lion head
        g.push();
        g.translate(0, 0, 1.4 + 3.0 * grow);
        double s = 0.55 + 0.45 * grow;
        g.scale(s);
        lionHead(g, c.t, c.seed);
        g.pop();
    }

    /** Stylised lion head made of flame: mane of spikes, round face, snout, ears, fangs. Looks toward +Z. */
    static void lionHead(Gfx g, float t, long seed) {
        g.sphere(0.5, 6, 10, Col.rgba(0xFFB020, 0.95f), Col.rgba(0xFFD860, 0.95f));
        g.push();
        g.translate(0, 0.05, 0.1);
        g.sphere(0.72, 6, 12, Col.rgba(0xD03008, 0.35f), Col.rgba(0xFF6010, 0.35f));
        g.pop();
        // mane: two rings of spikes pointing outward
        for (int ring = 0; ring < 2; ring++) {
            int n = 14;
            for (int i = 0; i < n; i++) {
                double a = PI * 2 * i / n + ring * 0.22;
                g.push();
                g.rotZ(a);
                g.rotY(PI / 2 + (ring == 0 ? -0.18 : 0.12));   // tilt back
                g.rotX(-PI / 2);
                g.translate(0, 0, 0.35 + 0.05 * ring);
                double len = (0.55 + 0.25 * Rnd.h(seed, i + ring * 20)) * (ring == 0 ? 1 : 0.8)
                        * (1 + 0.12 * Math.sin(t * 0.8 + i));
                g.coneZ(len, 0.14, 4, Col.rgba(0xFF8A10, 0.9f), Col.rgba(0xC01800, 0.2f));
                g.pop();
            }
        }
        // snout + nose + mouth
        g.box(-0.2, -0.22, 0.38, 0.2, -0.02, 0.72, Col.rgb(0xFFE08A));
        g.box(-0.08, -0.04, 0.7, 0.08, 0.04, 0.76, Col.rgb(0x501008));
        g.box(-0.16, -0.3, 0.4, 0.16, -0.22, 0.66, Col.rgb(0xFF7020));
        for (int s = -1; s <= 1; s += 2) {
            g.push();
            g.translate(s * 0.1, -0.22, 0.62);
            g.rotX(PI);
            g.coneY(0.14, 0.035, 4, WHITE, WHITE);
            g.pop();
            g.cube(s * 0.2, 0.16, 0.45, 0.1, Col.rgb(0xFFFFFF));   // eyes
            g.cube(s * 0.2, 0.16, 0.5, 0.05, Col.rgb(0xC01000));
            g.push();
            g.translate(s * 0.34, 0.42, 0.0);                       // ears
            g.sphere(0.13, 4, 6, Col.rgb(0xFF9010), Col.rgb(0xFFD040));
            g.pop();
        }
    }

    private static void leoPalma(Gfx g, Ctx c) {
        g.alpha = c.fade(3, 8);
        double h = c.fh;
        float drop = Math.min(1f, c.t / 6f);
        double y = h + 1.6 * (1 - drop * drop) + 0.2;
        float press = c.t > 6 ? (float) (0.12 * Math.sin((c.t - 6) * 0.8)) : 0f;
        g.push();
        g.translate(0, y - press, 0);
        g.scale(Math.max(0.9, c.fw * 1.4));
        // palm (flattened), toe pads, claws
        g.push();
        g.scale(1.0, 0.45, 0.9);
        g.sphere(0.75, 6, 10, Col.rgba(0xE05010, 0.85f), Col.rgba(0xFFB030, 0.9f));
        g.pop();
        for (int i = 0; i < 4; i++) {
            double x = -0.58 + i * 0.39;
            double z = 0.62 + (i == 0 || i == 3 ? -0.1 : 0.05);
            g.push();
            g.translate(x, -0.05, z);
            g.scale(1, 0.7, 1.2);
            g.sphere(0.2, 4, 8, Col.rgba(0xFF8010, 0.95f), Col.rgba(0xFFD860, 0.95f));
            g.pop();
            g.push();
            g.translate(x, -0.1, z + 0.22);
            g.rotX(PI / 2 + 0.5);
            g.coneZ(0.34, 0.07, 4, Col.rgb(0xFFF0C0), Col.rgb(0xFFFFFF));
            g.pop();
        }
        g.push();
        g.translate(0, -0.12, -0.05);
        g.scale(1.1, 0.5, 1);
        g.sphere(0.38, 5, 8, Col.rgba(0xFFB040, 0.95f), Col.rgba(0xFFD870, 0.95f));
        g.pop();
        g.pop();
        // flame crown under the paw + ground ring
        for (int i = 0; i < 6; i++) {
            double a = PI * 2 * i / 6 + c.t * 0.2;
            g.push();
            g.translate(Math.cos(a) * c.fw * 0.9, 0, Math.sin(a) * c.fw * 0.9);
            Parts.flame(g, 1.0 * drop, 0.2, c.t, i, Col.rgba(0xFF9010, 0.85f), Col.rgba(0xC02000, 0f));
            g.pop();
        }
        g.translate(0, 0.05, 0);
        g.ring(c.fw * 0.9, c.fw * 1.3, 20, Col.rgba(0xFFB030, 0.8f), Col.rgba(0xFF4000, 0f));
    }

    private static void ignisColumna(Gfx g, Ctx c) {
        float rise = Math.min(1f, c.t / 6f);
        g.alpha = c.fade(0, 10);
        double H = 3.8 * rise;
        double R = 1.3;
        g.frustumY(H, R * 0.9, R * 0.55, 14, Col.rgba(0xFF7010, 0.6f), Col.rgba(0xFFD040, 0.1f), 0);
        g.frustumY(H * 0.9, R * 0.6, R * 0.35, 12, Col.rgba(0xFFE080, 0.55f), Col.rgba(0xFF9020, 0.05f), 0);
        for (int i = 0; i < 10; i++) {
            double a = PI * 2 * i / 10 + c.t * 0.35;
            g.push();
            g.translate(Math.cos(a) * R, 0, Math.sin(a) * R);
            Parts.flame(g, 1.6 + 1.2 * Rnd.h(c.seed, i), 0.28, c.t, i, Col.rgba(0xFF8010, 0.9f), Col.rgba(0xC01800, 0f));
            g.pop();
        }
        for (int k = 0; k < 3; k++) {
            g.push();
            g.translate(0, ((c.t * 0.12 + k * 0.33) % 1.0) * H, 0);
            g.torus(R * 0.85, 0.07, 18, 4, Col.rgba(0xFFD060, 0.85f));
            g.pop();
        }
        g.translate(0, 0.04, 0);
        g.ring(R * 0.2, R * 1.8, 24, Col.rgba(0xFFB030, 0.8f), Col.rgba(0xFF3000, 0f));
    }

    private static void fireballBurst(Gfx g, Ctx c) {
        float p = c.p();
        double R = c.len;
        double r = R * (0.25 + 0.85 * Math.sqrt(p));
        float a = 1f - p;
        g.alpha = a;
        g.sphere(r, 7, 12, Col.rgba(0xFF5010, 0.55f), Col.rgba(0xFFC040, 0.55f));
        g.sphere(r * 0.65, 6, 10, Col.rgba(0xFFD060, 0.8f), Col.rgba(0xFFFFFF, 0.8f));
        for (int i = 0; i < 12; i++) {
            double az = PI * 2 * Rnd.h(c.seed, i), el = Math.asin(Rnd.s(c.seed, i + 40) * 0.9);
            double dx = Math.cos(az) * Math.cos(el), dy = Math.sin(el), dz = Math.sin(az) * Math.cos(el);
            g.rod(dx * r * 0.7, dy * r * 0.7, dz * r * 0.7, dx * r * 1.35, dy * r * 1.35, dz * r * 1.35, 0.12 * R * 0.5, 0.01,
                    Col.rgba(0xFFC040, 0.9f), Col.rgba(0xFF3000, 0f));
        }
        g.push();
        g.translate(0, -r * 0.2, 0);
        g.ring(r * 0.8, r * 1.5, 20, Col.rgba(0xFFB030, 0.7f), Col.rgba(0xFF3000, 0f));
        g.pop();
    }

    /** Giant flaming fist, knuckles toward +Z, flaming forearm trailing behind it. */
    private static void flameFist(Gfx g, float t, long seed) {
        g.boxG(-0.34, -0.3, -0.1, 0.34, 0.3, 0.5, Col.rgb(0xFF8A10), Col.rgb(0xFFD860));
        for (int i = 0; i < 4; i++) {
            g.box(-0.3 + i * 0.155, -0.26, 0.5, -0.17 + i * 0.155, 0.26, 0.66, Col.rgb(0xFFF0A0));
        }
        g.box(-0.42, -0.1, 0.0, -0.34, 0.2, 0.4, Col.rgb(0xFFB030));
        g.frustumZ(-2.4, 0.3, 0.46, 10, Col.rgba(0xFF6010, 0.15f), Col.rgba(0xFF9A20, 0.8f), 0);
        g.frustumZ(-2.2, 0.2, 0.32, 8, Col.rgba(0xFFE070, 0.1f), Col.rgba(0xFFF0A0, 0.9f), 0);
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8 + t * 0.2;
            g.push();
            g.translate(Math.cos(a) * 0.42, Math.sin(a) * 0.42, -0.2);
            g.rotX(PI - 0.15);
            g.coneZ(1.2 + 0.7 * Rnd.h(seed, i), 0.13, 4, Col.rgba(0xFF7010, 0.85f), Col.rgba(0xC01800, 0f));
            g.pop();
        }
    }

    private static void calidusBrachium(Gfx g, Ctx c) {
        double f = Parts.ease(c.t / 3.0);
        double z = 1.2 + (c.len - 1.2) * f;
        g.alpha = c.fade(0, 5);
        g.aim(c);
        g.push();
        g.translate(0, 0, z);
        flameFist(g, c.t, c.seed);
        g.pop();
        if (c.t > 2.5) {
            float k = (float) Parts.clamp01((c.t - 2.5) / 7.0);
            for (int i = 0; i < 3; i++) {
                g.push();
                g.translate(0, 0, c.len + 0.7 + i * 0.5 * k);
                g.rotX(PI / 2);
                g.torus(0.4 + 1.6 * k + i * 0.25, 0.08, 18, 4, Col.rgba(0xFFC040, 0.85f * (1 - k)));
                g.pop();
            }
            g.push();
            g.translate(0, 0, c.len + 0.7);
            Parts.starburst(g, 10, 1.2 * k + 0.3, 0.08, Col.rgba(0xFFF0A0, 0.9f * (1 - k)), Col.rgba(0xFF4000, 0f), c.t * 0.1);
            g.pop();
        }
    }

    /** Fists punching out in every direction around the caster. */
    private static void calidusBarrage(Gfx g, Ctx c) {
        float p = c.p();
        g.alpha = c.fade(0, 8);
        double R = c.len;
        for (int i = 0; i < 8; i++) {
            double a = PI * 2 * i / 8 + 0.3;
            double start = (i % 4) * 1.5;
            double f = Parts.ease((c.t - start) / 3.0);
            double out = Parts.clamp01((c.life - c.t) / 6.0);
            if (f <= 0) continue;
            g.push();
            g.translate(0, 1.1, 0);
            g.rotY(Math.atan2(Math.cos(a), Math.sin(a)));
            g.translate(0, 0, (0.8 + (R * 0.55 - 0.8) * f) * (0.6 + 0.4 * out));
            g.scale(0.75);
            flameFist(g, c.t, c.seed + i);
            g.pop();
        }
        double r = R * Parts.ease(p * 1.8);
        g.ring(0.3, r, 28, Col.rgba(0xFFB030, 0.7f), Col.rgba(0xFF3000, 0f));
        g.push();
        g.translate(0, 0.08, 0);
        g.torus(r, 0.12, 28, 4, Col.rgba(0xFFD060, 0.85f * (1 - p)));
        g.pop();
        g.push();
        g.translate(0, 1.0, 0);
        g.sphere(r * 0.6, 6, 10, Col.rgba(0xFF6010, 0.22f * (1 - p)), Col.rgba(0xFFD060, 0.22f * (1 - p)));
        g.pop();
    }
}
