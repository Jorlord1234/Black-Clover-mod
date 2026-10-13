package com.clovermagic.armor;

import java.util.function.Consumer;

import com.clovermagic.client.SquadArmorRenderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Netherite-level squad armor piece rendered with a GeckoLib 3D model (one model per squad). */
public class SquadArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Squad squad;

    public SquadArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, Squad squad) {
        super(material, type, properties);
        this.squad = squad;
    }

    public Squad squad() {
        return squad;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        final Squad s = this.squad;
        consumer.accept(new GeoRenderProvider() {
            private SquadArmorRenderer renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack,
                    EquipmentSlot equipmentSlot, HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = new SquadArmorRenderer(s);
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // static armor: no animations
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
