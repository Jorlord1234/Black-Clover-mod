package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.YultimSpiderEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look: a huge dark purple spider (vanilla spider model, scaled up). */
public class YultimSpiderRenderer extends SpiderRenderer<YultimSpiderEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/yultim_spider.png");

    public YultimSpiderRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 1.6F;
    }

    @Override
    protected void scale(YultimSpiderEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.4F, 2.4F, 2.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(YultimSpiderEntity entity) {
        return TEXTURE;
    }
}
