package com.clovermagic.magic;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Optional Curios support, written with reflection so the mod builds and runs without Curios.
 * With Curios installed, the "grimoire" slot (data/curios/curios/slots/grimoire.json) holds a grimoire,
 * and a grimoire in that slot works like one in the inventory.
 */
public final class CuriosCompat {
    private CuriosCompat() {
    }

    public static ItemStack find(ServerPlayer sp, MagicData data) {
        if (!ModList.get().isLoaded("curios")) return ItemStack.EMPTY;
        try {
            Class<?> api = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            Object opt = api.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class).invoke(null, sp);
            Object handler = ((Optional<?>) opt).orElse(null);
            if (handler == null) return ItemStack.EMPTY;
            Class<?> handlerClass = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
            Map<?, ?> curios = (Map<?, ?>) handlerClass.getMethod("getCurios").invoke(handler);
            Class<?> stacksClass = Class.forName("top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler");
            Method getStacks = stacksClass.getMethod("getStacks");
            for (Object stacksHandler : curios.values()) {
                IItemHandler items = (IItemHandler) getStacks.invoke(stacksHandler);
                for (int i = 0; i < items.getSlots(); i++) {
                    ItemStack s = items.getStackInSlot(i);
                    if (GrimoireManager.isValid(s, sp, data)) return s;
                }
            }
        } catch (Throwable ignored) {
            // Curios missing or a different version: just no Curios support
        }
        return ItemStack.EMPTY;
    }
}
