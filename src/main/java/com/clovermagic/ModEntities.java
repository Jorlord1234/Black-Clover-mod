package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.entity.FireballProjectile;
import com.clovermagic.entity.BroomEntity;
import com.clovermagic.entity.DemonEntity;
import com.clovermagic.entity.DevilEntity;
import com.clovermagic.entity.MagicBoarEntity;
import com.clovermagic.entity.MagicNpcEntity;
import com.clovermagic.entity.MidnightMageEntity;
import com.clovermagic.entity.YultimSpiderEntity;
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

    public static final Supplier<EntityType<MagicNpcEntity>> MAGIC_NPC = ENTITIES.register("magic_npc",
            () -> EntityType.Builder.<MagicNpcEntity>of(MagicNpcEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build("magic_npc"));

    public static final Supplier<EntityType<MagicBoarEntity>> MAGIC_BOAR = ENTITIES.register("magic_boar",
            () -> EntityType.Builder.<MagicBoarEntity>of(MagicBoarEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.0f).clientTrackingRange(8).build("magic_boar"));

    public static final Supplier<EntityType<MidnightMageEntity>> MIDNIGHT_MAGE = ENTITIES.register("midnight_mage",
            () -> EntityType.Builder.<MidnightMageEntity>of(MidnightMageEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(10).build("midnight_mage"));

    public static final Supplier<EntityType<YultimSpiderEntity>> YULTIM_SPIDER = ENTITIES.register("yultim_spider",
            () -> EntityType.Builder.<YultimSpiderEntity>of(YultimSpiderEntity::new, MobCategory.MONSTER)
                    .sized(3.4f, 2.0f).clientTrackingRange(12).build("yultim_spider"));

    public static final Supplier<EntityType<BroomEntity>> BROOM = ENTITIES.register("broom",
            () -> EntityType.Builder.<BroomEntity>of(BroomEntity::new, MobCategory.MISC)
                    .sized(1.2f, 0.4f).clientTrackingRange(10).build("broom"));

    public static final Supplier<EntityType<DemonEntity>> DEMON = ENTITIES.register("demon",
            () -> EntityType.Builder.<DemonEntity>of(DemonEntity::new, MobCategory.MONSTER)
                    .sized(1.4f, 3.6f).clientTrackingRange(10).build("demon"));

    public static final Supplier<EntityType<DevilEntity>> DEVIL = ENTITIES.register("devil",
            () -> EntityType.Builder.<DevilEntity>of(DevilEntity::new, MobCategory.MONSTER)
                    .sized(0.7f, 2.1f).clientTrackingRange(12).fireImmune().build("devil"));

    public static void attributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(MAGIC_NPC.get(), MagicNpcEntity.createAttributes().build());
        event.put(MAGIC_BOAR.get(), MagicBoarEntity.createAttributes().build());
        event.put(BROOM.get(), BroomEntity.createAttributes().build());
        event.put(DEMON.get(), DemonEntity.createAttributes().build());
        event.put(DEVIL.get(), DevilEntity.createAttributes().build());
        event.put(MIDNIGHT_MAGE.get(), MidnightMageEntity.createAttributes().build());
        event.put(YULTIM_SPIDER.get(), YultimSpiderEntity.createAttributes().build());
    }

    public static void spawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        registerMonster(event, MAGIC_BOAR.get());
        registerMonster(event, MIDNIGHT_MAGE.get());
        registerMonster(event, DEMON.get());
    }

    private static <T extends net.minecraft.world.entity.monster.Monster> void registerMonster(
            net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event, EntityType<T> type) {
        event.register(type, net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
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
