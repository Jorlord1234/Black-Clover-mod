package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.MagicBoarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Magic Boar: a stocky boar with a shoulder hump, a ridge of mane spikes, curved tusks, pointed ears and a tail.
 * Texture textures/entity/magic_boar.png (64x64). Walks, sniffs when idle, lowers its head and bristles when it charges.
 */
public class MagicBoarModel extends EntityModel<MagicBoarEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "magic_boar"), "main");
    private static final int MANES = 5;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart legFr;
    private final ModelPart legFl;
    private final ModelPart legRr;
    private final ModelPart legRl;

    public MagicBoarModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.tail = root.getChild("tail");
        this.legFr = root.getChild("leg_fr");
        this.legFl = root.getChild("leg_fl");
        this.legRr = root.getChild("leg_rr");
        this.legRl = root.getChild("leg_rl");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5F, -5F, -9F, 10F, 10F, 18F), PartPose.offset(0F, 13F, 0F));
        body.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(0, 28)
                .addBox(-4.5F, -8F, -8F, 9F, 3F, 8F), PartPose.ZERO);
        for (int i = 0; i < MANES; i++) {
            body.addOrReplaceChild("mane" + i, CubeListBuilder.create().texOffs(36, 28)
                    .addBox(-1F, -3F, -1F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, -7.5F, -6F + i * 3F, 0.35F, 0F, 0F));
        }

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 40)
                .addBox(-3.5F, -3.5F, -6F, 7F, 7F, 6F), PartPose.offset(0F, 12F, -9F));
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(28, 40)
                .addBox(-2F, 0.5F, -9F, 4F, 3F, 3F), PartPose.ZERO);
        head.addOrReplaceChild("tusk_l", CubeListBuilder.create().texOffs(44, 40)
                .addBox(-0.5F, -3F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(2.3F, 3F, -6.5F, 0.5F, 0F, 0.2F));
        head.addOrReplaceChild("tusk_r", CubeListBuilder.create().texOffs(44, 40)
                .addBox(-0.5F, -3F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(-2.3F, 3F, -6.5F, 0.5F, 0F, -0.2F));
        head.addOrReplaceChild("ear_l", CubeListBuilder.create().texOffs(50, 40)
                .addBox(-1F, -3F, -0.5F, 2F, 3F, 1F), PartPose.offsetAndRotation(3.2F, -3F, -2F, 0F, 0F, 0.5F));
        head.addOrReplaceChild("ear_r", CubeListBuilder.create().texOffs(50, 40)
                .addBox(-1F, -3F, -0.5F, 2F, 3F, 1F), PartPose.offsetAndRotation(-3.2F, -3F, -2F, 0F, 0F, -0.5F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 54).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F);
        root.addOrReplaceChild("leg_fr", leg, PartPose.offset(-3.4F, 17F, -6F));
        root.addOrReplaceChild("leg_fl", leg, PartPose.offset(3.4F, 17F, -6F));
        root.addOrReplaceChild("leg_rr", leg, PartPose.offset(-3.4F, 17F, 6F));
        root.addOrReplaceChild("leg_rl", leg, PartPose.offset(3.4F, 17F, 6F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(14, 54)
                .addBox(-0.5F, 0F, 0F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 9F, 9F, 0.7F, 0F, 0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MagicBoarEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean charging = entity.isAggressive();
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        // sniffs while idle, lowers its head to charge
        this.head.xRot = headPitch * ((float) Math.PI / 180F) + Mth.sin(ageInTicks * 0.08F) * 0.04F + (charging ? 0.35F : 0F);
        float swing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legFr.xRot = swing;
        this.legRl.xRot = swing;
        this.legFl.xRot = -swing;
        this.legRr.xRot = -swing;
        this.tail.zRot = Mth.sin(ageInTicks * 0.25F) * (charging ? 0.5F : 0.2F);
        this.tail.xRot = charging ? 0.2F : 0.7F;
        for (int i = 0; i < MANES; i++) {
            ModelPart mane = this.body.getChild("mane" + i);
            mane.xRot = (charging ? 0.7F : 0.35F) + Mth.sin(ageInTicks * 0.1F + i) * 0.03F;
        }
        this.body.xRot = charging ? -0.05F : 0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
