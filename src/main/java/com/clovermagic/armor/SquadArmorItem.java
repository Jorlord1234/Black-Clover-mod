package com.clovermagic.armor;

import java.util.function.Consumer;

import com.clovermagic.client.SquadArmorModel;
import com.clovermagic.client.SquadArmorRenderer;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
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
        consumer.accept(new GeoRenderProvider() {
            private SquadArmorRenderer renderer;
            private boolean checked;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack,
                    EquipmentSlot equipmentSlot, HumanoidModel<T> original) {
                if (!this.checked) {
                    this.checked = true;
                    // Safety net: if the 3D model file is missing from the mod jar (for example an incomplete build),
                    // return null so GeckoLib falls back to the vanilla armor layer instead of crashing the game.
                    ResourceLocation geo = SquadArmorModel.modelLocation(SquadArmorItem.this);
                    if (Minecraft.getInstance().getResourceManager().getResource(geo).isPresent()) {
                        this.renderer = new SquadArmorRenderer();
                    } else {
                        LogUtils.getLogger().error("Clover Magic: missing 3D armor model {} - using the vanilla fallback", geo);
                    }
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
