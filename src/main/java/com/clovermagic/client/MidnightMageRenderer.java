package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.MidnightMageEntity;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look: a dark hooded mage with a sun mark, on the player model. */
public class MidnightMageRenderer extends HumanoidMobRenderer<MidnightMageEntity, PlayerModel<MidnightMageEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/midnight_mage.png");

    public MidnightMageRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MidnightMageEntity entity) {
        return TEXTURE;
    }
}
