package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.armor.Squad;
import com.clovermagic.armor.SquadArmorItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import software.bernie.geckolib.model.GeoModel;

/**
 * One shared model per piece type, one texture per squad:
 *   geo/item/armor/{robe|hood|bandana}.geo.json
 *   textures/item/armor/{robe|hood|bandana}/{squad}.png
 *   animations/item/armor/none.animation.json (empty)
 */
public class SquadArmorModel extends GeoModel<SquadArmorItem> {
    public static String kind(SquadArmorItem item) {
        if (item.getType() == ArmorItem.Type.CHESTPLATE) return "robe";
        return item.squad() == Squad.BLACK_BULL ? "bandana" : "hood";
    }

    public static ResourceLocation modelLocation(SquadArmorItem item) {
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "geo/item/armor/" + kind(item) + ".geo.json");
    }

    @Override
    public ResourceLocation getModelResource(SquadArmorItem animatable) {
        return modelLocation(animatable);
    }

    @Override
    public ResourceLocation getTextureResource(SquadArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID,
                "textures/item/armor/" + kind(animatable) + "/" + animatable.squad().id + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SquadArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "animations/item/armor/none.animation.json");
    }
}
