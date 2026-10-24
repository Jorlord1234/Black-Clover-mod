package com.clovermagic.client.vfx;

import com.clovermagic.vfx.Fx;

/** Entry point: draws the 3D model of one effect into the Gfx. Pure geometry, no Minecraft classes. */
public final class SpellModels {
    private SpellModels() {
    }

    public static void draw(Fx fx, Gfx g, Ctx c) {
        g.mat(Gfx.Mat.GLOW);
        g.alpha = 1f;
        if (FireFx.draw(fx, g, c)) return;
        if (WaterFx.draw(fx, g, c)) return;
        if (WindFx.draw(fx, g, c)) return;
        if (EarthFx.draw(fx, g, c)) return;
        if (IceFx.draw(fx, g, c)) return;
        if (LightningFx.draw(fx, g, c)) return;
        if (DarkFx.draw(fx, g, c)) return;
        if (LightFx.draw(fx, g, c)) return;
        if (AntiFx.draw(fx, g, c)) return;
        if (SpiritFx.draw(fx, g, c)) return;
        if (ArchetypeFx.draw(fx, g, c)) return;
    }
}
