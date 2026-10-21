package com.clovermagic.client;

import com.clovermagic.CloverMagic;
import com.clovermagic.entity.MagicNpcEntity;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Placeholder look for the story characters: a skin per character on the player model. */
public class MagicNpcRenderer extends HumanoidMobRenderer<MagicNpcEntity, PlayerModel<MagicNpcEntity>> {
    public MagicNpcRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicNpcEntity entity) {
        String type = switch (entity.npcType()) {
            case "asta", "julius" -> entity.npcType();
            default -> "orsi";
        };
        return ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "textures/entity/npc_" + type + ".png");
    }
}
