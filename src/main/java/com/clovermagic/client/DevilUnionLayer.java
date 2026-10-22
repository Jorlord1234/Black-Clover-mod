package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.ModEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

/** Draws the Devil Union horns, crown and wings on every player that has the Devil Union effect. */
public class DevilUnionLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/devil_union.png");
    private static final ResourceLocation SUIT =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/devil_union_suit.png");
    private static final ResourceLocation SUIT_SLIM =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/devil_union_suit_slim.png");
    /** Red markings only (rest transparent), drawn again at full brightness so they glow. */
    private static final ResourceLocation SUIT_GLOW =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/devil_union_suit_glow.png");
    private static final ResourceLocation SUIT_SLIM_GLOW =
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/devil_union_suit_slim_glow.png");
    private final DevilUnionModel model;

    public DevilUnionLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, DevilUnionModel model) {
        super(parent);
        this.model = model;
    }

    @Override
    public void render(PoseStack ps, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!player.hasEffect(ModEffects.DEVIL_UNION) || player.isInvisible()) return;
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        int overlay = LivingEntityRenderer.getOverlayCoords(player, 0.0F);
        model.animate(ageInTicks, player.isCrouching());

        // the black armored suit with red markings: the player's own model drawn again with the suit texture
        // (head is transparent in the texture, so the face stays visible)
        boolean slim = player.getSkin().model() == PlayerSkin.Model.SLIM;   // slim arms need their own texture layout
        this.getParentModel().renderToBuffer(ps, buffers.getBuffer(RenderType.armorCutoutNoCull(slim ? SUIT_SLIM : SUIT)), light, overlay, -1);
        // the red markings glow
        this.getParentModel().renderToBuffer(ps, buffers.getBuffer(RenderType.eyes(slim ? SUIT_SLIM_GLOW : SUIT_GLOW)),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);

        ps.pushPose();
        this.getParentModel().body.translateAndRotate(ps);
        model.renderBody(ps, vc, light, overlay);
        ps.popPose();

        ps.pushPose();
        this.getParentModel().head.translateAndRotate(ps);
        model.renderGear(ps, vc, LightTexture.FULL_BRIGHT, overlay);   // horns and crown glow
        ps.popPose();
    }
}
