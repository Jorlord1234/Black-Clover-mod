package com.clovermagic.client;

import java.util.List;

import com.clovermagic.CloverMagic;
import com.clovermagic.ModEntities;
import com.clovermagic.magic.Spell;
import com.clovermagic.net.CastPayload;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Client only: Grimoire Mode toggle, spell list key, number-key casting and the HUD layer. */
public final class ClientSetup {
    public static final String CATEGORY = "key.categories.clovermagic";
    public static final KeyMapping MODE = new KeyMapping("key.clovermagic.mode",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping INFO = new KeyMapping("key.clovermagic.info",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);

    private ClientSetup() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerKeys);
        modBus.addListener(ClientSetup::registerLayers);
        modBus.addListener(ClientSetup::registerRenderers);
        modBus.addListener(ClientSetup::registerModelLayers);
        modBus.addListener(ClientSetup::addPlayerLayers);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onClientTickPre);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onRenderLayer);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onScroll);
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(MODE);
        event.register(INFO);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FIREBALL.get(), FireballRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_VFX.get(), SpellVfxRenderer::new);
        event.registerEntityRenderer(ModEntities.HOBGOBLIN.get(), HobgoblinRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_NPC.get(), MagicNpcRenderer::new);
    }

    public static final net.minecraft.client.model.geom.ModelLayerLocation DEVIL_UNION_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "devil_union"), "main");

    private static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DEVIL_UNION_LAYER, DevilUnionModel::createLayer);
    }

    /** Adds the Devil Union horns and wings layer to both player skin types (wide and slim arms). */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        DevilUnionModel model = new DevilUnionModel(event.getEntityModels().bakeLayer(DEVIL_UNION_LAYER));
        for (net.minecraft.client.resources.PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof net.minecraft.client.renderer.entity.player.PlayerRenderer renderer) {
                renderer.addLayer(new DevilUnionLayer(renderer, model));
            }
        }
    }

    private static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "magic_hud"), new MagicHud());
    }

    /** Runs before the game handles keys, so we can take the number keys while Grimoire Mode is on. */
    private static void onClientTickPre(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        boolean inGame = mc.screen == null;

        while (MODE.consumeClick()) {
            if (inGame) {
                if (!ClientMagicState.hasGrimoire) {
                    mc.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("You have no grimoire yet. Visit a Grimoire Tower."), true);
                } else {
                    ClientMagicState.mode = !ClientMagicState.mode;
                }
            }
        }
        while (INFO.consumeClick()) {
            if (inGame) {
                com.clovermagic.skill.SkillTree tree = ClientMagicState.tree();
                if (tree != null && ClientMagicState.hasGrimoire) {
                    mc.setScreen(new SkillTreeScreen(tree));
                } else {
                    mc.setScreen(new GrimoireScreen());
                }
            }
        }

        if (ClientMagicState.mode && inGame) {
            List<Spell> spells = ClientMagicState.spells();
            for (int i = 0; i < mc.options.keyHotbarSlots.length; i++) {
                while (mc.options.keyHotbarSlots[i].consumeClick()) {
                    if (i < spells.size()) {
                        PacketDistributor.sendToServer(new CastPayload(i));
                    }
                }
            }
        }
    }

    /** Hide the vanilla hotbar while Grimoire Mode is on. */
    private static void onRenderLayer(RenderGuiLayerEvent.Pre event) {
        if (ClientMagicState.mode && ClientMagicState.hasGrimoire
                && event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            event.setCanceled(true);
        }
    }

    /** The mouse wheel must not change the (hidden) hotbar slot in Grimoire Mode. */
    private static void onScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (ClientMagicState.mode && mc.screen == null) {
            event.setCanceled(true);
        }
    }
}
