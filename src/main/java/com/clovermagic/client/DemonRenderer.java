package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.DemonEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look for the demon: a huge dark red humanoid. */
public class DemonRenderer extends HumanoidMobRenderer<DemonEntity, HumanoidModel<DemonEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/demon.png");

    public DemonRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 1.0F);
    }

    @Override
    protected void scale(DemonEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.9F, 1.9F, 1.9F);
    }

    @Override
    public ResourceLocation getTextureLocation(DemonEntity entity) {
        return TEXTURE;
    }
}
