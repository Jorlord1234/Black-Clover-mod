package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.MagicBoarEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look: a dark, tusked boar on the vanilla pig model. */
public class MagicBoarRenderer extends MobRenderer<MagicBoarEntity, PigModel<MagicBoarEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/magic_boar.png");

    public MagicBoarRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PigModel<>(ctx.bakeLayer(ModelLayers.PIG)), 0.7F);
    }

    @Override
    protected void scale(MagicBoarEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.25F, 1.25F, 1.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicBoarEntity entity) {
        return TEXTURE;
    }
}
