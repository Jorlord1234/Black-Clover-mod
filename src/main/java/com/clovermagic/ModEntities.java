package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.entity.FireballProjectile;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CloverMagic.MODID);

    public static final Supplier<EntityType<FireballProjectile>> FIREBALL = ENTITIES.register("fireball",
            () -> EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(2)
                    .fireImmune()
                    .noSave()
                    .build("fireball"));

    private ModEntities() {
    }
}
