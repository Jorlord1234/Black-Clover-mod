package com.clovermagic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Asta's Devil Union (v2, after the anime): big ram horns that curl around the ears, spiky black hair, a red thorn crown,
 * shoulder plates with spikes, a tail, and large ragged bat wings (arm + five fingers with membrane strips).
 * Pure code model, texture is textures/entity/devil_union.png (128x64). Wings, plates and tail are drawn relative
 * to the body, horns, hair and crown relative to the head. The model artist can replace all of it later.
 */
public class DevilUnionModel {
    private final ModelPart body;
    private final ModelPart gear;
    private final ModelPart wingR;
    private final ModelPart wingL;
    private final ModelPart tail;

    public DevilUnionModel(ModelPart root) {
        this.body = root.getChild("body");
        this.gear = root.getChild("gear");
        this.wingR = body.getChild("wing_r");
        this.wingL = body.getChild("wing_l");
        this.tail = body.getChild("tail");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        wing(body, "wing_r", 1);
        wing(body, "wing_l", -1);
        shoulder(body, "plate_r", 1);
        shoulder(body, "plate_l", -1);
        // tail: four segments curling up behind the player
        PartDefinition t = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-0.7F, -0.7F, 0F, 1.4F, 1.4F, 4F), PartPose.offsetAndRotation(0F, 11F, 2F, -0.2F, 0F, 0F));
        PartDefinition cur = t;
        for (int i = 0; i < 3; i++) {
            cur = cur.addOrReplaceChild("t" + i, CubeListBuilder.create().texOffs(0, i == 2 ? 0 : 24)
                    .addBox(-0.6F + i * 0.05F, -0.6F, 0F, 1.2F - i * 0.2F, 1.2F - i * 0.2F, 4F), PartPose.offsetAndRotation(0F, 0F, 4F, -0.35F, 0F, 0F));
        }

        PartDefinition gear = root.addOrReplaceChild("gear", CubeListBuilder.create(), PartPose.ZERO);
        horn(gear, "horn_r", 1);
        horn(gear, "horn_l", -1);
        // spiky black hair
        float[][] spikes = {{0F, -8.6F, -1.5F, 0F, 0F}, {-2.4F, -8.4F, -1F, 0.1F, -0.35F}, {2.4F, -8.4F, -1F, 0.1F, 0.35F},
                {-1.2F, -8.5F, 1.8F, -0.3F, -0.2F}, {1.2F, -8.5F, 1.8F, -0.3F, 0.2F}, {0F, -8.5F, 3.2F, -0.6F, 0F}};
        for (int i = 0; i < spikes.length; i++) {
            gear.addOrReplaceChild("hair" + i, CubeListBuilder.create().texOffs(0, 24)
                    .addBox(-0.9F, -4.2F, -0.9F, 1.8F, 4.2F, 1.8F),
                    PartPose.offsetAndRotation(spikes[i][0], spikes[i][1], spikes[i][2], spikes[i][3], 0F, spikes[i][4]));
        }
        // red thorn crown band with spikes (red area of the texture)
        CubeListBuilder crown = CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.5F, -7.0F, -4.6F, 9.0F, 1.2F, 0.6F)
                .addBox(-4.5F, -7.0F, 4.0F, 9.0F, 1.2F, 0.6F)
                .addBox(-4.6F, -7.0F, -4.0F, 0.6F, 1.2F, 8.0F)
                .addBox(4.0F, -7.0F, -4.0F, 0.6F, 1.2F, 8.0F)
                .addBox(-0.5F, -8.8F, -4.7F, 1.0F, 1.9F, 0.7F)
                .addBox(-2.8F, -8.3F, -4.7F, 0.9F, 1.4F, 0.7F)
                .addBox(1.9F, -8.3F, -4.7F, 0.9F, 1.4F, 0.7F)
                .addBox(-4.7F, -8.3F, -1.0F, 0.7F, 1.4F, 0.9F)
                .addBox(4.0F, -8.3F, -1.0F, 0.7F, 1.4F, 0.9F);
        gear.addOrReplaceChild("crown", crown, PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    /** Ram horn: five segments, thick at the base, curling out, back and then down around the ear. */
    private static void horn(PartDefinition gear, String name, int side) {
        float[] w = {2.6F, 2.2F, 1.8F, 1.4F, 1.0F};
        float[] zr = {0.9F, 0.7F, 0.6F, 0.6F, 0.5F};   // outward curl
        float[] xr = {0.0F, 0.15F, 0.45F, 0.6F, 0.7F};  // forward curl (down in front of the ear)
        PartDefinition cur = gear.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 24)
                        .addBox(-w[0] / 2, -2.6F, -w[0] / 2, w[0], 2.6F, w[0]),
                PartPose.offsetAndRotation(side * 2.6F, -7.8F, -0.8F, 0.1F, 0F, side * 0.7F));
        for (int i = 1; i < 5; i++) {
            cur = cur.addOrReplaceChild("s" + i, CubeListBuilder.create().texOffs(0, 24)
                            .addBox(-w[i] / 2, -2.8F, -w[i] / 2, w[i], 2.8F, w[i]),
                    PartPose.offsetAndRotation(0F, -2.6F, 0F, xr[i], 0F, side * zr[i]));
        }
    }

    /** Shoulder plate with a red spike (body-space, sits on top of the shoulders). */
    private static void shoulder(PartDefinition body, String name, int side) {
        PartDefinition plate = body.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 24)
                        .addBox(-2.4F, -0.6F, -3.0F, 4.8F, 1.6F, 6.0F),
                PartPose.offsetAndRotation(side * 5.6F, -0.8F, 0F, 0F, 0F, side * 0.25F));
        plate.addOrReplaceChild("spike", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.8F, -3.4F, -0.8F, 1.6F, 3.4F, 1.6F),
                PartPose.offsetAndRotation(0F, -0.4F, 0F, 0F, 0F, side * 0.3F));
    }

    /** One big ragged wing: arm, then five fingers fanning out from the wrist, each with a hanging membrane strip. */
    private static void wing(PartDefinition body, String name, int side) {
        PartDefinition root = body.addOrReplaceChild(name, CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * 1.5F, 2.0F, 2.4F, 0F, side * 0.5F, 0F));
        float armLen = 14F;
        PartDefinition arm = root.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 40)
                        .addBox(side > 0 ? 0F : -armLen, -1.2F, -0.6F, armLen, 2.4F, 1.2F),
                PartPose.rotation(0F, 0F, side * -0.8F));
        float[] len = {27F, 24F, 20F, 15F, 10F};
        float[] ang = {-0.05F, 0.4F, 0.8F, 1.2F, 1.6F};
        float[] membrane = {10F, 13F, 11F, 8F, 5F};
        for (int i = 0; i < 5; i++) {
            PartDefinition finger = arm.addOrReplaceChild("finger" + i, CubeListBuilder.create().texOffs(0, 40)
                            .addBox(side > 0 ? 0F : -len[i], -0.6F, -0.6F, len[i], 1.2F, 1.2F),
                    PartPose.offsetAndRotation(side * armLen, 0F, 0F, 0F, 0F, side * ang[i]));
            finger.addOrReplaceChild("membrane", CubeListBuilder.create().texOffs(0, 44)
                            .addBox(side > 0 ? 0F : -len[i], 0.6F, -0.3F, len[i], membrane[i], 0.6F),
                    PartPose.ZERO);
        }
    }

    /** Slow wing beats, a swaying tail; crouching folds the wings back. */
    public void animate(float ageInTicks, boolean crouching) {
        float flap = (float) Math.sin(ageInTicks * 0.15F) * 0.16F;
        float fold = crouching ? 0.4F : 0F;
        wingR.zRot = flap;
        wingL.zRot = -flap;
        wingR.yRot = 0.5F - fold;
        wingL.yRot = -0.5F + fold;
        tail.yRot = (float) Math.sin(ageInTicks * 0.1F) * 0.3F;
    }

    public void renderBody(PoseStack ps, VertexConsumer vc, int light, int overlay) {
        body.render(ps, vc, light, overlay, -1);
    }

    public void renderGear(PoseStack ps, VertexConsumer vc, int light, int overlay) {
        gear.render(ps, vc, light, overlay, -1);
    }
}
