package com.clovermagic.client;

import com.clovermagic.entity.BroomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The flying broom: a long wooden handle with a bundle of straw at the back (code model, texture 64x32). */
public class BroomModel extends EntityModel<BroomEntity> {
    private final ModelPart root;

    public BroomModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition broom = root.addOrReplaceChild("broom", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-0.8F, -0.8F, -14F, 1.6F, 1.6F, 24F), PartPose.offset(0F, 20F, 0F));
        broom.addOrReplaceChild("bristles_a", CubeListBuilder.create().texOffs(0, 14)
                .addBox(-2.6F, -2.6F, 10F, 5.2F, 5.2F, 4F), PartPose.ZERO);
        broom.addOrReplaceChild("bristles_b", CubeListBuilder.create().texOffs(0, 14)
                .addBox(-3.4F, -3.4F, 14F, 6.8F, 6.8F, 5F), PartPose.ZERO);
        broom.addOrReplaceChild("bristles_c", CubeListBuilder.create().texOffs(0, 14)
                .addBox(-2.0F, -2.0F, 19F, 4F, 4F, 3F), PartPose.ZERO);
        broom.addOrReplaceChild("tie", CubeListBuilder.create().texOffs(30, 0)
                .addBox(-1.4F, -1.4F, 9.2F, 2.8F, 2.8F, 1.2F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(BroomEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart broom = root.getChild("broom");
        broom.zRot = (float) Math.sin(ageInTicks * 0.1F) * 0.03F;   // a little sway in the air
    }

    @Override
    public void renderToBuffer(PoseStack ps, VertexConsumer vc, int light, int overlay, int color) {
        root.render(ps, vc, light, overlay, color);
    }
}
