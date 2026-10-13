package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.magic.GrimoireData;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CloverMagic.MODID);

    public static final Supplier<DataComponentType<GrimoireData>> GRIMOIRE_DATA = COMPONENTS.register("grimoire",
            () -> DataComponentType.<GrimoireData>builder()
                    .persistent(GrimoireData.CODEC)
                    .networkSynchronized(GrimoireData.STREAM_CODEC)
                    .build());

    private ModComponents() {
    }
}
