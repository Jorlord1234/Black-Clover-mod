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
 * Asta's Devil Union: two curved horns, a spiked crown and a pair of bat-like wings (arm, four fingers with a membrane
 * strip on each). Pure code model, texture is textures/entity/devil_union.png (128x64).
 * The wings are drawn relative to the player's body and the horns/crown relative to the head.
 */
public class DevilUnionModel {
    private final ModelPart wings;
    private final ModelPart gear;
    private final ModelPart wingR;
    private final ModelPart wingL;

    public DevilUnionModel(ModelPart root) {
        this.wings = root.getChild("wings");
        this.gear = root.getChild("gear");
        this.wingR = wings.getChild("wing_r");
        this.wingL = wings.getChild("wing_l");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition wings = root.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.ZERO);
        wing(wings, "wing_r", 1);
        wing(wings, "wing_l", -1);

        PartDefinition gear = root.addOrReplaceChild("gear", CubeListBuilder.create(), PartPose.ZERO);
        horn(gear, "horn_r", 1);
        horn(gear, "horn_l", -1);
        // spiked crown: a thin red band around the head with small spikes (red area of the texture)
        CubeListBuilder crown = CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.4F, -7.2F, -4.5F, 8.8F, 1.0F, 0.5F)   // front
                .addBox(-4.4F, -7.2F, 4.0F, 8.8F, 1.0F, 0.5F)    // back
                .addBox(-4.5F, -7.2F, -4.0F, 0.5F, 1.0F, 8.0F)   // left
                .addBox(4.0F, -7.2F, -4.0F, 0.5F, 1.0F, 8.0F)    // right
                .addBox(-0.5F, -8.6F, -4.6F, 1.0F, 1.6F, 0.6F)   // spikes
                .addBox(-2.6F, -8.2F, -4.6F, 0.8F, 1.2F, 0.6F)
                .addBox(1.8F, -8.2F, -4.6F, 0.8F, 1.2F, 0.6F);
        gear.addOrReplaceChild("crown", crown, PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    /** One horn in three curved segments, curling outward and back. side = +1 right, -1 left. */
    private static void horn(PartDefinition gear, String name, int side) {
        PartDefinition a = gear.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 24)
                .addBox(-1.2F, -3.0F, -1.2F, 2.4F, 3.0F, 2.4F), PartPose.offsetAndRotation(side * 3.0F, -7.6F, -0.5F, 0.15F, 0F, side * 0.45F));
        PartDefinition b = a.addOrReplaceChild("b", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-0.9F, -3.0F, -0.9F, 1.8F, 3.0F, 1.8F), PartPose.offsetAndRotation(0F, -3.0F, 0F, 0.25F, 0F, side * 0.35F));
        b.addOrReplaceChild("c", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-0.55F, -3.0F, -0.55F, 1.1F, 3.0F, 1.1F), PartPose.offsetAndRotation(0F, -3.0F, 0F, 0.4F, 0F, side * 0.4F));
    }

    /** One wing: arm, then four fingers fanning out from the wrist, each with a hanging membrane strip. */
    private static void wing(PartDefinition wings, String name, int side) {
        float x0 = side > 0 ? 0F : -1F;       // boxes grow outward from the pivot on both sides
        PartDefinition root = wings.addOrReplaceChild(name, CubeListBuilder.create(),
                PartPose.offsetAndRotation(side * 1.5F, 2.5F, 2.2F, 0F, side * 0.55F, 0F));
        float armLen = 11F;
        PartDefinition arm = root.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 40)
                        .addBox(side > 0 ? 0F : -armLen, -1.0F, -0.5F, armLen, 2.0F, 1.0F),
                PartPose.rotation(0F, 0F, side * -0.75F));
        float[] len = {19F, 17F, 14F, 10F};
        float[] ang = {0.05F, 0.55F, 1.05F, 1.55F};
        float[] membrane = {9F, 9F, 8F, 6F};
        for (int i = 0; i < 4; i++) {
            PartDefinition finger = arm.addOrReplaceChild("finger" + i, CubeListBuilder.create().texOffs(0, 40)
                            .addBox(side > 0 ? 0F : -len[i], -0.5F, -0.5F, len[i], 1.0F, 1.0F),
                    PartPose.offsetAndRotation(side * armLen, 0F, 0F, 0F, 0F, side * ang[i]));
            finger.addOrReplaceChild("membrane", CubeListBuilder.create().texOffs(0, 44)
                            .addBox(side > 0 ? 0F : -len[i], 0.5F, -0.25F, len[i], membrane[i], 0.5F),
                    PartPose.ZERO);
        }
    }

    /** Flaps the wings gently; crouching folds them back a little. */
    public void animate(float ageInTicks, boolean crouching) {
        float flap = (float) Math.sin(ageInTicks * 0.18F) * 0.18F;
        float fold = crouching ? 0.35F : 0F;
        wingR.zRot = flap;
        wingL.zRot = -flap;
        wingR.yRot = 0.55F - fold;
        wingL.yRot = -0.55F + fold;
    }

    public void renderWings(PoseStack ps, VertexConsumer vc, int light, int overlay) {
        wings.render(ps, vc, light, overlay, -1);
    }

    public void renderGear(PoseStack ps, VertexConsumer vc, int light, int overlay) {
        gear.render(ps, vc, light, overlay, -1);
    }
}
