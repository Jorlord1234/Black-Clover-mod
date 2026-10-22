package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.DevilEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look for the devils: a skin per devil on the player model (Zagred is bigger). */
public class DevilRenderer extends HumanoidMobRenderer<DevilEntity, PlayerModel<DevilEntity>> {
    public DevilRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.6F);
    }

    @Override
    protected void scale(DevilEntity entity, PoseStack poseStack, float partialTick) {
        float s = entity.devilType().equals("zagred") ? 1.5F : 1.1F;
        poseStack.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(DevilEntity entity) {
        String type = switch (entity.devilType()) {
            case "zagred", "lucifugus" -> entity.devilType();
            default -> "megicula";
        };
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/devil_" + type + ".png");
    }
}
