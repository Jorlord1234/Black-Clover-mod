package com.clovermagic;

import com.clovermagic.block.GrimoireAltarBlock;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CloverMagic.MODID);

    public static final DeferredBlock<Block> MANA_CRYSTAL_ORE = BLOCKS.registerBlock("mana_crystal_ore",
            props -> new DropExperienceBlock(UniformInt.of(1, 4), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0f, 3.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 3));

    public static final DeferredBlock<Block> DEEPSLATE_MANA_CRYSTAL_ORE = BLOCKS.registerBlock("deepslate_mana_crystal_ore",
            props -> new DropExperienceBlock(UniformInt.of(2, 5), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5f, 3.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> 3));

    public static final DeferredBlock<Block> GRIMOIRE_ALTAR = BLOCKS.registerBlock("grimoire_altar",
            props -> new GrimoireAltarBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5f, 1200.0f)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE).lightLevel(state -> 8));

    private ModBlocks() {
    }
}
