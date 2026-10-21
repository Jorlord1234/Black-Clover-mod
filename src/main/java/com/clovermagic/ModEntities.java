package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.entity.FireballProjectile;
import com.clovermagic.entity.SpellVfxEntity;

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

    /** Visual-only entity that carries a spell's 3D model (see SpellVfxEntity). */
    public static final Supplier<EntityType<SpellVfxEntity>> SPELL_VFX = ENTITIES.register("spell_vfx",
            () -> EntityType.Builder.<SpellVfxEntity>of(SpellVfxEntity::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .build("spell_vfx"));

    private ModEntities() {
    }
}
