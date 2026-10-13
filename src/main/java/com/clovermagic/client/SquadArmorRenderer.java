package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.armor.Squad;
import com.clovermagic.armor.SquadArmorItem;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * Renders a squad's 3D armor. Assets for squad "black_bull":
 *   assets/clovermagic/geo/item/armor/black_bull.geo.json
 *   assets/clovermagic/textures/item/armor/black_bull.png
 *   assets/clovermagic/animations/item/armor/black_bull.animation.json
 */
public class SquadArmorRenderer extends GeoArmorRenderer<SquadArmorItem> {
    public SquadArmorRenderer(Squad squad) {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "armor/" + squad.id)));
    }
}
