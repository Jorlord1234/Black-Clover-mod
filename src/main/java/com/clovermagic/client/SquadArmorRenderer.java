package com.clovermagic.client;

import com.clovermagic.armor.SquadArmorItem;

import software.bernie.geckolib.renderer.GeoArmorRenderer;

/** Renders squad robes, hoods and the Black Bull bandana with GeckoLib. */
public class SquadArmorRenderer extends GeoArmorRenderer<SquadArmorItem> {
    public SquadArmorRenderer() {
        super(new SquadArmorModel());
    }
}
