package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.BroomEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BroomRenderer extends MobRenderer<BroomEntity, BroomModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/broom.png");

    public BroomRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BroomModel(ctx.bakeLayer(ClientSetup.BROOM_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(BroomEntity entity) {
        return TEXTURE;
    }
}
