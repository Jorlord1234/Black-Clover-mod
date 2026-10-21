package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.HobgoblinEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look for the Hobgoblin: a bigger green humanoid. Replace with a real model later. */
public class HobgoblinRenderer extends HumanoidMobRenderer<HobgoblinEntity, HumanoidModel<HobgoblinEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/hobgoblin.png");

    public HobgoblinRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.6F);
    }

    @Override
    protected void scale(HobgoblinEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(HobgoblinEntity entity) {
        return TEXTURE;
    }
}
