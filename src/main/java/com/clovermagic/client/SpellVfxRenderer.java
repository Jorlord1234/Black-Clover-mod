package com.clovermagic.client;

import com.clovermagic.client.vfx.Ctx;
import com.clovermagic.entity.SpellVfxEntity;
import com.clovermagic.vfx.Fx;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Draws the 3D model of a spell effect entity. */
public class SpellVfxRenderer extends EntityRenderer<SpellVfxEntity> {
    public SpellVfxRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SpellVfxEntity entity) {
        return VfxDraw.GLOW_TEX;
    }

    /** The entity itself is tiny; the model can be big, so never cull it by the entity's own box. */
    @Override
    public boolean shouldRender(SpellVfxEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return entity.getFx() != null;
    }

    @Override
    public void render(SpellVfxEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        Fx fx = entity.getFx();
        if (fx == null || entity.getLife() <= 0) return;

        Ctx c = new Ctx();
        c.t = entity.tickCount + partialTick;
        c.life = entity.getLife();
        c.len = entity.getLen();
        c.aux = entity.getAux();
        c.seed = entity.getId();
        c.yaw = entity.getYRot();
        c.pitch = entity.getXRot();
        c.bodyYaw = c.yaw;

        poseStack.pushPose();
        int followId = entity.getFollowId();
        if (followId >= 0) {
            Entity follow = entity.level().getEntity(followId);
            if (follow != null) {
                double fx0 = Mth.lerp(partialTick, follow.xo, follow.getX());
                double fy0 = Mth.lerp(partialTick, follow.yo, follow.getY());
                double fz0 = Mth.lerp(partialTick, follow.zo, follow.getZ());
                double ex = Mth.lerp(partialTick, entity.xo, entity.getX());
                double ey = Mth.lerp(partialTick, entity.yo, entity.getY());
                double ez = Mth.lerp(partialTick, entity.zo, entity.getZ());
                poseStack.translate(fx0 - ex, fy0 - ey, fz0 - ez);
                c.fw = follow.getBbWidth();
                c.fh = follow.getBbHeight();
                if (follow instanceof LivingEntity living) {
                    c.bodyYaw = Mth.rotLerp(partialTick, living.yBodyRotO, living.yBodyRot);
                }
                Minecraft mc = Minecraft.getInstance();
                c.firstPerson = follow == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson();
            }
        }
        VfxDraw.draw(fx, c, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
