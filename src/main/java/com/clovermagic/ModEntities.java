package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.entity.FireballProjectile;
import com.clovermagic.entity.HobgoblinEntity;
import com.clovermagic.entity.MagicNpcEntity;
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

    public static final Supplier<EntityType<HobgoblinEntity>> HOBGOBLIN = ENTITIES.register("hobgoblin",
            () -> EntityType.Builder.<HobgoblinEntity>of(HobgoblinEntity::new, MobCategory.MONSTER)
                    .sized(0.7f, 1.9f)
                    .clientTrackingRange(8)
                    .build("hobgoblin"));

    public static final Supplier<EntityType<MagicNpcEntity>> MAGIC_NPC = ENTITIES.register("magic_npc",
            () -> EntityType.Builder.<MagicNpcEntity>of(MagicNpcEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build("magic_npc"));

    public static void attributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(HOBGOBLIN.get(), HobgoblinEntity.createAttributes().build());
        event.put(MAGIC_NPC.get(), MagicNpcEntity.createAttributes().build());
    }

    public static void spawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        event.register(HOBGOBLIN.get(), net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                net.minecraft.world.entity.monster.Monster::checkMonsterSpawnRules,
                net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation.OR);
    }
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
