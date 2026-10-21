package com.clovermagic.client;

import com.clovermagic.client.vfx.Ctx;
import com.clovermagic.entity.FireballProjectile;
import com.clovermagic.vfx.Fx;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/** The flying fireball: a layered 3D fire orb with a flame tail pointing away from its direction of travel. */
public class FireballRenderer extends EntityRenderer<FireballProjectile> {
    public FireballRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FireballProjectile entity) {
        return VfxDraw.GLOW_TEX;
    }

    @Override
    public boolean shouldRender(FireballProjectile entity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(FireballProjectile entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        Vec3 v = entity.getDeltaMovement();
        Ctx c = new Ctx();
        c.t = entity.tickCount + partialTick;
        c.life = 80;
        c.len = entity.getVisualScale();
        c.seed = entity.getId();
        double h = Math.sqrt(v.x * v.x + v.z * v.z);
        if (v.lengthSqr() > 1.0E-6) {
            c.yaw = (float) Math.toDegrees(Math.atan2(-v.x, v.z));
            c.pitch = (float) Math.toDegrees(-Math.atan2(v.y, h));
        }
        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() / 2.0, 0.0);
        VfxDraw.draw(Fx.FIREBALL_PROJECTILE, c, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
