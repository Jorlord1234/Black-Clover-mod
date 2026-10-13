package com.clovermagic;

import com.clovermagic.armor.Squad;
import com.clovermagic.client.ClientSetup;
import com.clovermagic.net.ModNetwork;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CloverMagic.MODID)
public class CloverMagic {
    public static final String MODID = "clovermagic";

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.clovermagic"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.GRIMOIRE_ALTAR.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.GRIMOIRE_ALTAR.get());
                        output.accept(ModItems.MANA_CRYSTAL.get());
                        output.accept(ModItems.GRIMOIRE_SIGIL.get());
                        output.accept(ModItems.MANA_CRYSTAL_ORE.get());
                        output.accept(ModItems.DEEPSLATE_MANA_CRYSTAL_ORE.get());
                        output.accept(ModItems.MANA_CLOTH.get());
                        for (Squad squad : Squad.values()) {
                            output.accept(ModItems.HOODS.get(squad).get());
                            output.accept(ModItems.ROBES.get(squad).get());
                            if (ModItems.TROUSERS.containsKey(squad)) {
                                output.accept(ModItems.TROUSERS.get(squad).get());
                                output.accept(ModItems.BOOTS.get(squad).get());
                            }
                        }
                    }).build());

    public CloverMagic(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModArmorMaterials.MATERIALS.register(modEventBus);
        ModComponents.COMPONENTS.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(ModNetwork::register);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init(modEventBus);
        }

        NeoForge.EVENT_BUS.addListener(ModEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ModEvents::onDeath);
        NeoForge.EVENT_BUS.addListener(ModEvents::onLogin);
        NeoForge.EVENT_BUS.addListener(ModEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(ModCommands::register);
    }
}
