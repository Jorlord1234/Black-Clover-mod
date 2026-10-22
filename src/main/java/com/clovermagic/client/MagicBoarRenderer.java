package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.MagicBoarEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** The Magic Boar with its own model (MagicBoarModel): tusks, hump, mane spikes. */
public class MagicBoarRenderer extends MobRenderer<MagicBoarEntity, MagicBoarModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/magic_boar.png");

    public MagicBoarRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MagicBoarModel(ctx.bakeLayer(MagicBoarModel.LAYER)), 0.7F);
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
