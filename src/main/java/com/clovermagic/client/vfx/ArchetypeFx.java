package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

import static com.clovermagic.client.vfx.Parts.PI;

/**
 * 3D models for the spells that have no hand-built recipe, built from a few archetypes (spear, wave, rays, burst, pillars, zone,
 * aura, dome, target cage, strike, rain, dash, wall) in the colors of each element, plus the cast circle with the floating
 * grimoire that appears under every caster. All pure geometry, like the other recipes.
 */
final class ArchetypeFx {
    private ArchetypeFx() {
    }

    /** core = bright centre, main = body color, edge = dark rim. */
    private record Pal(int core, int main, int edge) {
    }

    private static final Pal FIRE = new Pal(0xFFF0A0, 0xFF7A10, 0xD02A0A);
    private static final Pal WATER = new Pal(0xE0F8FF, 0x3FA9F5, 0x1E5FC0);
    private static final Pal WIND = new Pal(0xFFFFE0, 0xB6F35A, 0x2FA060);
    private static final Pal EARTH = new Pal(0xFFF0C0, 0xC8A060, 0x6A4A28);
    private static final Pal ICE = new Pal(0xFFFFFF, 0x7FD8FF, 0x3A8ACF);
    private static final Pal LIGHTNING = new Pal(0xFFFFFF, 0xFFE040, 0x3060FF);
    private static final Pal LIGHT = new Pal(0xFFFFFF, 0xFFF0A0, 0xE0B040);
    private static final Pal DARK = new Pal(0xE0A0FF, 0x6030A0, 0x120818);
    private static final Pal ANTI = new Pal(0xFF5060, 0x2A2A2E, 0xFF2030);

    private static int a(int rgb, float alpha) {
        return Col.rgba(rgb, Math.max(0f, Math.min(1f, alpha)));
    }

    static boolean draw(Fx fx, Gfx g, Ctx c) {
        switch (fx) {
            case TIMED_SCATTERSHOT -> rays(g, c, FIRE, 6);
            case VANISHING_FIREBALL -> burst(g, c, FIRE);
            case PROFOUND_SPIRAL -> wave(g, c, FIRE, 1.6);
            case CRIMSON_ERUPTION -> pillars(g, c, FIRE);
            case MANA_ZONE -> zone(g, c, FIRE);
            case SALAMANDER_BREATH -> wave(g, c, FIRE, 1.4);
            case POINT_BLANK_ROAR -> wave(g, c, WATER, 2.0);
            case SEA_GODS_HAMMER -> strike(g, c, WATER, 0.9);
            case SEA_GODS_SHIELD -> dome(g, c, WATER);
            case SURGING_WAVES -> wave(g, c, WATER, 2.4);
            case AQUA_JAVELIN -> spear(g, c, WATER, 0.16);
            case SEA_SERPENTS_BELLOW -> burst(g, c, WATER);
            case SPIRIT_ART_WATER -> zone(g, c, WATER);
            case TORNADO_FANG -> wave(g, c, WIND, 1.7);
            case SPIRIT_STORM -> pillars(g, c, WIND);
            case HEAVENLY_WIND_ARK -> aura(g, c, WIND, 5);
            case SLICING_WIND_EMPEROR -> wave(g, c, WIND, 2.6);
            case PIERCING_TORNADO_NEEDLE -> spear(g, c, WIND, 0.09);
            case WIND_BLADES_SHOWER -> rays(g, c, WIND, 6);
            case KILLING_KAKARA_BLADE -> dash(g, c, WIND);
            case SPIRIT_ART_WIND -> zone(g, c, WIND);
            case AVALANCHE -> wave(g, c, EARTH, 2.6);
            case MOUNTAIN_BREAKER -> pillars(g, c, EARTH);
            case SINKING_GROUND -> zone(g, c, EARTH);
            case ROCK_CAGE -> target(g, c, EARTH);
            case EARTH_PRISON -> zone(g, c, EARTH);
            case STONE_WALL -> wall(g, c, EARTH);
            case LIVING_FORTRESS -> dome(g, c, EARTH);
            case SPIRIT_ART_EARTH -> pillars(g, c, EARTH);
            case ICE_SPEARS -> rays(g, c, ICE, 5);
            case ABSOLUTE_ZERO -> zone(g, c, ICE);
            case FROZEN_CHAINS -> rays(g, c, ICE, 4);
            case GLACIER_WALL -> wall(g, c, ICE);
            case FROZEN_WORLD -> zone(g, c, ICE);
            case CRYSTAL_MIRROR -> dome(g, c, ICE);
            case ETERNAL_WINTER -> zone(g, c, ICE);
            case THUNDER_RAIN -> rain(g, c, LIGHTNING);
            case JUDGEMENT_BOLT -> strike(g, c, LIGHTNING, 0.7);
            case PARALYZING_FIELD -> zone(g, c, LIGHTNING);
            case ARC_CAGE -> target(g, c, LIGHTNING);
            case THUNDER_PRISON -> target(g, c, LIGHTNING);
            case LIGHT_SPEAR -> spear(g, c, LIGHT, 0.14);
            case SUNBURST -> burst(g, c, LIGHT);
            case BLESSING -> aura(g, c, LIGHT, 6);
            case REVIVAL_LIGHT -> strike(g, c, LIGHT, 1.1);
            case LIGHT_BARRIER -> wall(g, c, LIGHT);
            case HALO -> aura(g, c, LIGHT, 3);
            case DIVINE_SHIELD -> dome(g, c, LIGHT);
            case BLACK_MOON -> zone(g, c, DARK);
            case SHADOW_STEP -> burst(g, c, DARK);
            case NIGHT_VEIL -> aura(g, c, DARK, 4);
            case IAI_SLASH -> dash(g, c, DARK);
            case DEATH_THRUST -> spear(g, c, DARK, 0.12);
            case BLACK_DIVIDER -> wave(g, c, ANTI, 1.4);
            case BLACK_METEORITE -> strike(g, c, ANTI, 1.2);
            case BLACK_FORM -> aura(g, c, ANTI, 4);
            case BLACK_ASTA -> aura(g, c, ANTI, 7);
            case NULLIFY -> zone(g, c, ANTI);
            case DEFLECT -> dome(g, c, ANTI);
            case MAGIC_ZERO -> zone(g, c, ANTI);
            case CAST_FIRE -> cast(g, c, FIRE);
            case CAST_WATER -> cast(g, c, WATER);
            case CAST_WIND -> cast(g, c, WIND);
            case CAST_EARTH -> cast(g, c, EARTH);
            case CAST_ICE -> cast(g, c, ICE);
            case CAST_LIGHTNING -> cast(g, c, LIGHTNING);
            case CAST_LIGHT -> cast(g, c, LIGHT);
            case CAST_DARKNESS -> cast(g, c, DARK);
            case CAST_ANTI_MAGIC -> cast(g, c, ANTI);
            default -> {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- cast circle + grimoire
    private static void cast(Gfx g, Ctx c, Pal p) {
        g.alpha = c.fade(2, 8);
        double open = Parts.ease(c.t / 5.0);
        g.push();
        g.translate(0, 0.05, 0);
        Parts.circle(g, 0.4 + 1.5 * open, c.t * 0.16, a(p.main, 0.85f), a(p.core, 0.95f));
        g.translate(0, 0.02, 0);
        Parts.circle(g, 0.8 * open, -c.t * 0.22, a(p.edge, 0.9f), a(p.core, 0.9f));
        g.pop();
        for (int i = 0; i < 8; i++) {   // light motes rising from the circle
            double ang = PI * 2 * i / 8 + c.t * 0.1;
            double rise = ((c.t * 0.08 + Rnd.h(c.seed, i)) % 1.0) * 1.8;
            g.push();
            g.translate(Math.cos(ang) * 1.1 * open, 0.1 + rise, Math.sin(ang) * 1.1 * open);
            g.coneY(0.35, 0.04, 4, a(p.main, 0.0f), a(p.core, 0.9f));
            g.pop();
        }
        // the grimoire floats at your side and opens
        g.push();
        g.rotY(-Math.toRadians(c.bodyYaw));
        g.translate(-0.85, 1.35 + 0.05 * Math.sin(c.t * 0.3), 0.15);
        double ang = 0.15 + 1.0 * open;
        int cover = Col.rgb(0x5A2E1E), page = Col.rgb(0xF2E8C8);
        g.push();
        g.rotZ(ang);
        g.boxG(0, -0.02, -0.2, 0.3, 0.03, 0.2, cover, a(p.main, 1f));
        g.boxG(0, 0.03, -0.17, 0.27, 0.045, 0.17, page, page);
        g.pop();
        g.push();
        g.rotZ(-ang);
        g.boxG(-0.3, -0.02, -0.2, 0, 0.03, 0.2, cover, a(p.main, 1f));
        g.boxG(-0.27, 0.03, -0.17, 0, 0.045, 0.17, page, page);
        g.pop();
        g.box(-0.03, -0.03, -0.2, 0.03, 0.03, 0.2, cover);
        g.push();
        g.translate(0, 0.25 * open, 0);
        Parts.star4(g, 0.12 * open, a(p.core, 1f), a(p.main, 0.0f));
        g.pop();
        g.pop();
    }

    // ---------------------------------------------------------------- beams
    /** A thin fast lance of energy with rings spinning around it. */
    private static void spear(Gfx g, Ctx c, Pal p, double w) {
        double z = 0.3 + (c.len - 0.3) * Parts.ease(c.t / 3.0);
        g.alpha = c.fade(0, 6);
        g.aim(c);
        g.rod(0, 0, 0.1, 0, 0, z, w * 0.4, w, a(p.main, 0.0f), a(p.core, 0.9f));
        g.rod(0, 0, 0.1, 0, 0, z, w * 0.2, w * 0.5, a(p.core, 0.5f), a(p.core, 1f));
        for (int i = 1; i <= 5; i++) {
            g.push();
            g.translate(0, 0, z * i / 6.0);
            g.rotX(PI / 2);
            g.rotY(c.t * 0.5 + i);
            g.ring(w * 1.3, w * 1.9, 10, a(p.main, 0.5f), a(p.edge, 0.0f));
            g.pop();
        }
        g.push();
        g.translate(0, 0, z);
        g.sphere(w * 1.2, 5, 8, a(p.core, 1f), a(p.main, 0.8f));
        g.pop();
    }

    /** A wide widening wave in front of the caster. */
    private static void wave(Gfx g, Ctx c, Pal p, double w) {
        double len = Math.max(0.5, c.len * Parts.ease(c.t / 4.0));
        g.alpha = c.fade(0, 8);
        g.aim(c);
        g.frustumZ(len, w * 0.15, w, 12, a(p.core, 0.8f), a(p.main, 0.0f), 0);
        g.frustumZ(len, w * 0.07, w * 0.45, 8, a(p.core, 0.9f), a(p.edge, 0.0f), 0);
        for (int i = 1; i <= 4; i++) {
            double f = i / 5.0;
            g.push();
            g.translate(0, 0, len * f);
            g.rotX(PI / 2);
            g.rotY(c.t * 0.4 + i);
            g.ring(w * f * 0.7, w * f * 1.0 + 0.1, 14, a(p.main, 0.45f), a(p.edge, 0.0f));
            g.pop();
        }
    }

    /** The caster rushes forward: a fat streak with afterimages. */
    private static void dash(Gfx g, Ctx c, Pal p) {
        double len = Math.max(0.5, c.len * Parts.ease(c.t / 3.0));
        g.alpha = c.fade(0, 8);
        g.aim(c);
        g.rod(0, 0, 0, 0, 0, len, 0.1, 0.45, a(p.main, 0.0f), a(p.core, 0.8f));
        g.rod(0, 0, 0, 0, 0, len, 0.05, 0.2, a(p.core, 0.4f), a(p.core, 1f));
        for (int i = 1; i <= 3; i++) {
            g.push();
            g.translate(0, 0, len * i / 4.0);
            g.rotX(PI / 2);
            g.ring(0.3, 0.6, 12, a(p.main, 0.6f), a(p.edge, 0.0f));
            g.pop();
        }
    }

    // ---------------------------------------------------------------- around the caster
    /** Rays shooting out in all directions (several targets at once). */
    private static void rays(Gfx g, Ctx c, Pal p, int n) {
        double ext = Parts.ease(c.t / 4.0);
        g.alpha = c.fade(0, 8);
        for (int i = 0; i < n; i++) {
            double ang = PI * 2 * i / n + Rnd.s(c.seed, i) * 0.3;
            double len = c.len * (0.6 + 0.4 * Rnd.h(c.seed, i + 9)) * ext;
            double dy = 0.4 * Rnd.s(c.seed, i + 20);
            g.push();
            g.lookFromTo(0, 1.2, 0, Math.cos(ang), 1.2 + dy, Math.sin(ang));
            g.frustumZ(Math.max(0.2, len), 0.03, 0.11, 6, a(p.main, 0.2f), a(p.core, 0.95f), 0);
            g.push();
            g.translate(0, 0, Math.max(0.2, len));
            g.sphere(0.15, 4, 6, a(p.core, 1f), a(p.main, 0.9f));
            g.pop();
            g.pop();
        }
        g.push();
        g.translate(0, 0.04, 0);
        g.ring(0.5, 1.6, 16, a(p.core, 0.5f), a(p.main, 0f));
        g.pop();
    }

    /** A shockwave ring with spikes and a flash at the caster. */
    private static void burst(Gfx g, Ctx c, Pal p) {
        double e = Parts.ease(c.t / 6.0);
        double r = Math.max(0.5, c.len * e);
        g.alpha = c.fade(0, 12);
        g.push();
        g.translate(0, 0.06, 0);
        g.ring(Math.max(0, r - 0.9), r, 32, a(p.main, 0f), a(p.core, 0.9f));
        g.ring(r * 0.3, r * 0.9, 28, a(p.edge, 0.2f * (1 - c.p())), a(p.main, 0.0f));
        g.pop();
        for (int i = 0; i < 14; i++) {
            double ang = PI * 2 * i / 14 + Rnd.s(c.seed, i) * 0.2;
            double rr = r * (0.55 + 0.45 * Rnd.h(c.seed, i));
            g.push();
            g.translate(Math.cos(ang) * rr, 0, Math.sin(ang) * rr);
            g.coneY((0.8 + 1.2 * Rnd.h(c.seed, i + 30)) * (1 - c.p()), 0.18, 5, a(p.edge, 0.8f), a(p.core, 0.9f));
            g.pop();
        }
        g.push();
        g.translate(0, 1.0, 0);
        g.sphere(0.6 + r * 0.25 * (1 - e * 0.5), 6, 10, a(p.core, (float) (0.5 * (1 - e))), a(p.main, (float) (0.3 * (1 - e))));
        g.pop();
    }

    /** Tall columns erupting from the ground in a wide ring. */
    private static void pillars(Gfx g, Ctx c, Pal p) {
        double e = Parts.ease(c.t / 6.0);
        double r = Math.max(0.5, c.len);
        g.alpha = c.fade(0, 12);
        g.push();
        g.translate(0, 0.06, 0);
        g.ring(r * 0.6, r, 28, a(p.edge, 0.4f), a(p.main, 0.0f));
        g.pop();
        for (int i = 0; i < 16; i++) {
            double ang = PI * 2 * i / 16 + Rnd.s(c.seed, i) * 0.3;
            double rr = r * (0.25 + 0.75 * Rnd.h(c.seed, i + 5));
            double h = (1.6 + 2.6 * Rnd.h(c.seed, i + 40)) * e * (1 - Parts.easeIn(c.p()));
            g.push();
            g.translate(Math.cos(ang) * rr, 0, Math.sin(ang) * rr);
            g.frustumY(h, 0.28, 0.06, 6, a(p.edge, 0.9f), a(p.core, 0.95f), 0);
            g.pop();
        }
    }

    /** A large rotating magic circle on the ground that stays for a while. */
    private static void zone(Gfx g, Ctx c, Pal p) {
        double e = Parts.ease(c.t / 6.0);
        double r = Math.max(0.5, c.len * e);
        g.alpha = c.fade(4, 14);
        g.push();
        g.translate(0, 0.06, 0);
        Parts.circle(g, r, c.t * 0.07, a(p.main, 0.8f), a(p.core, 0.95f));
        g.translate(0, 0.02, 0);
        Parts.circle(g, r * 0.55, -c.t * 0.1, a(p.edge, 0.85f), a(p.core, 0.9f));
        g.pop();
        for (int i = 0; i < 12; i++) {
            double ang = PI * 2 * i / 12 + c.t * 0.05;
            double rr = r * (0.2 + 0.75 * Rnd.h(c.seed, i));
            double rise = ((c.t * 0.05 + Rnd.h(c.seed, i + 3)) % 1.0) * 2.2;
            g.push();
            g.translate(Math.cos(ang) * rr, rise, Math.sin(ang) * rr);
            g.coneY(0.5, 0.05, 4, a(p.main, 0f), a(p.core, 0.9f));
            g.pop();
        }
    }

    // ---------------------------------------------------------------- on the caster
    /** Rings and orbiting orbs around the body (long buffs). */
    private static void aura(Gfx g, Ctx c, Pal p, int orbs) {
        g.alpha = c.fade(8, 14);
        g.push();
        g.translate(0, 0.95, 0);
        g.scale(0.9, 1.2, 0.9);
        g.sphere(1.0, 6, 10, a(p.main, 0.14f * c.shell()), a(p.core, 0.1f * c.shell()));
        g.pop();
        for (int k = 0; k < 3; k++) {
            g.push();
            g.translate(0, 0.35 + k * 0.7, 0);
            g.rotY(c.t * (0.15 + 0.05 * k) * (k % 2 == 0 ? 1 : -1));
            g.ring(0.65 + 0.08 * k, 0.78 + 0.08 * k, 22, a(p.core, 0.7f), a(p.main, 0.2f));
            g.pop();
        }
        for (int i = 0; i < orbs; i++) {
            double ang = PI * 2 * i / orbs + c.t * 0.12;
            g.push();
            g.translate(Math.cos(ang) * 1.0, 1.0 + 0.4 * Math.sin(c.t * 0.1 + i * 2), Math.sin(ang) * 1.0);
            g.sphere(0.1, 4, 6, a(p.core, 1f), a(p.main, 0.9f));
            g.pop();
        }
        g.push();
        g.translate(0, 0.04, 0);
        g.ring(0.4, 1.3, 20, a(p.core, 0.5f), a(p.main, 0f));
        g.pop();
    }

    /** A big translucent dome around the caster with a glowing ring on the ground (shields and barriers). */
    private static void dome(Gfx g, Ctx c, Pal p) {
        double r = Math.max(1.4, Math.min(2.8, c.len * 0.45));
        g.alpha = c.fade(8, 14);
        g.push();
        g.translate(0, 0, 0);
        g.sphere(r, 8, 14, a(p.main, 0.1f * c.shell()), a(p.core, 0.14f * c.shell()));
        g.pop();
        for (int i = 0; i < 6; i++) {
            double ang = PI * 2 * i / 6 + c.t * 0.03;
            g.push();
            g.translate(Math.cos(ang) * r * 0.9, 0, Math.sin(ang) * r * 0.9);
            g.coneY(0.9 + 0.3 * Math.sin(c.t * 0.2 + i), 0.12, 5, a(p.edge, 0.8f), a(p.core, 0.9f));
            g.pop();
        }
        g.push();
        g.translate(0, 0.04, 0);
        g.ring(r * 0.55, r * 1.05, 28, a(p.core, 0.55f), a(p.main, 0.0f));
        g.pop();
    }

    // ---------------------------------------------------------------- on a target / at a point
    /** A cage of rods around the followed target, with a ring on the ground. */
    private static void target(Gfx g, Ctx c, Pal p) {
        g.alpha = c.fade(3, 10);
        double w = Math.max(0.45, c.fw * 0.5 + 0.2), h = c.fh + 0.25;
        double grow = Parts.ease(c.t / 4.0);
        g.boxG(-w, -0.05, -w, w, h * grow, w, a(p.main, 0.3f), a(p.core, 0.12f));
        for (int i = 0; i < 4; i++) {
            double sx = (i % 2 == 0 ? 1 : -1) * w, sz = (i < 2 ? 1 : -1) * w;
            g.rod(sx, -0.05, sz, sx, h * grow, sz, 0.07, 0.07, a(p.edge, 0.95f), a(p.core, 0.95f));
        }
        for (int k = 1; k <= 3; k++) {
            double y = h * grow * k / 4.0;
            g.rod(-w, y, -w, w, y, -w, 0.04, 0.04, a(p.core, 0.9f), a(p.core, 0.9f));
            g.rod(w, y, -w, w, y, w, 0.04, 0.04, a(p.core, 0.9f), a(p.core, 0.9f));
            g.rod(w, y, w, -w, y, w, 0.04, 0.04, a(p.core, 0.9f), a(p.core, 0.9f));
            g.rod(-w, y, w, -w, y, -w, 0.04, 0.04, a(p.core, 0.9f), a(p.core, 0.9f));
        }
        g.push();
        g.translate(0, 0.04, 0);
        g.ring(w, w + 1.0, 18, a(p.core, 0.55f), a(p.main, 0f));
        g.pop();
    }

    /** A pillar of energy from above, a shockwave ring and a flash (big single hits). */
    private static void strike(Gfx g, Ctx c, Pal p, double width) {
        g.alpha = c.fade(0, 12);
        double e = Parts.ease(c.t / 3.0);
        g.frustumY(16 * e, width * 0.5, width, 10, a(p.core, 0.9f), a(p.main, 0.0f), 0);
        g.frustumY(16 * e, width * 0.2, width * 0.5, 8, a(p.core, 1f), a(p.core, 0.5f), 0);
        g.push();
        g.translate(0, 0.05, 0);
        g.ring(0.3, 1.2 + 3.0 * e, 24, a(p.core, 0.9f * (1 - c.p())), a(p.main, 0f));
        g.pop();
        g.push();
        g.translate(0, 0.6, 0);
        g.sphere(0.5 + 1.4 * e, 6, 10, a(p.core, 0.6f * (1 - c.p())), a(p.main, 0.2f));
        g.pop();
    }

    /** Bolts or streaks falling from the sky inside the radius. */
    private static void rain(Gfx g, Ctx c, Pal p) {
        g.alpha = c.fade(2, 10);
        double r = Math.max(1.0, c.len);
        for (int i = 0; i < 14; i++) {
            double ang = PI * 2 * Rnd.h(c.seed, i);
            double rr = r * Math.sqrt(Rnd.h(c.seed, i + 50));
            double phase = (c.t * 0.35 + Rnd.h(c.seed, i + 90)) % 1.0;
            double x = Math.cos(ang) * rr, z = Math.sin(ang) * rr;
            double y = 9.0 * (1 - phase);
            g.rod(x, y + 2.4, z, x + 0.15 * Rnd.s(c.seed, i), y, z, 0.05, 0.12, a(p.main, 0.0f), a(p.core, 1f));
            if (phase > 0.88) {
                g.push();
                g.translate(x, 0.05, z);
                g.ring(0.1, 0.6 * (phase - 0.88) * 8, 10, a(p.core, 0.8f), a(p.main, 0f));
                g.pop();
            }
        }
    }

    /** A glowing wall of energy a few blocks in front of the caster (the real blocks are placed by the spell). */
    private static void wall(Gfx g, Ctx c, Pal p) {
        g.alpha = c.fade(3, 14);
        double e = Parts.ease(c.t / 4.0);
        g.rotY(-Math.toRadians(c.yaw));
        g.translate(0, 0, 3.0);
        g.boxG(-3.6, 0, -0.18, 3.6, 3.1 * e, 0.18, a(p.edge, 0.3f), a(p.core, 0.15f));
        for (int i = -3; i <= 3; i++) {
            g.rod(i, 0, 0, i, 3.1 * e, 0, 0.05, 0.05, a(p.main, 0.7f), a(p.core, 0.9f));
        }
        g.push();
        g.translate(0, 0.05, 0);
        g.ring(0.2, 1.2, 12, a(p.core, 0.5f), a(p.main, 0f));
        g.pop();
    }
}
