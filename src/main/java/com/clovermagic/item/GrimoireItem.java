package com.clovermagic.item;

import java.util.List;

import com.clovermagic.Config;
import com.clovermagic.ModAttachments;
import com.clovermagic.ModComponents;
import com.clovermagic.magic.GrimoireData;
import com.clovermagic.magic.GrimoireManager;
import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.MagicData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A grimoire. It carries a GrimoireData component (owner, attribute, serial).
 * You cast with the keybinds while carrying your own valid grimoire; right-click shows its status.
 */
public class GrimoireItem extends Item {
    private final int leaves;

    public GrimoireItem(Properties props, int leaves) {
        super(props);
        this.leaves = leaves;
    }

    public int leaves() {
        return leaves;
    }

    public static int maxMana(int level, int leaves) {
        double base = Config.BASE_MANA.get() + (double) Config.MANA_PER_LEVEL.get() * level;
        return (int) (leaves == 4 ? base * Config.FOUR_LEAF_MANA_MULTIPLIER.get() : base);
    }

    public static double power(int leaves) {
        return leaves == 4 ? 1.4 : 1.0;
    }

    public static double costMultiplier(int leaves) {
        return leaves == 4 ? 0.75 : 1.0;
    }

    public static String leafName(int leaves) {
        return switch (leaves) {
            case 4 -> "Four-Leaf Grimoire";
            case 5 -> "Five-Leaf Grimoire";
            default -> "Three-Leaf Grimoire";
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide() || !(player instanceof ServerPlayer sp)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        GrimoireData own = stack.get(ModComponents.GRIMOIRE_DATA.get());
        if (own != null && own.owner().equals(sp.getUUID()) && data.leaves2 > 0
                && (own.generation() == data.generation || own.generation() == data.generation2)) {
            if (sp.isShiftKeyDown()) {
                data.swapGrimoires();
                com.clovermagic.magic.MagicSync.send(sp);
                MagicAttribute now = data.leaves == 5 ? MagicAttribute.ANTI_MAGIC : MagicAttribute.fromId(data.attribute);
                sp.displayClientMessage(Component.literal("You switch to your " + (now == null ? "" : now.displayName + " ") + "grimoire.")
                        .withStyle(ChatFormatting.LIGHT_PURPLE), true);
                return InteractionResultHolder.success(stack);
            }
            if (own.generation() == data.generation2) {
                sp.displayClientMessage(Component.literal("This is your second grimoire. Sneak + right-click to switch to it.")
                        .withStyle(ChatFormatting.GRAY), true);
                return InteractionResultHolder.success(stack);
            }
        }
        if (!GrimoireManager.isValid(stack, sp, data)) {
            GrimoireData gd = stack.get(ModComponents.GRIMOIRE_DATA.get());
            String who = gd == null ? "nobody" : gd.ownerName();
            sp.displayClientMessage(Component.literal("This grimoire is bound to " + who + " and will not answer you.")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }
        sp.displayClientMessage(Component.literal(data.leaves2 > 0 ? "Press G for Grimoire Mode, K for your skill tree. Sneak + right-click: switch grimoire."
                : "Press G for Grimoire Mode, K for your spell list.")
                .withStyle(ChatFormatting.GRAY), true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(leafName(leaves)).withStyle(ChatFormatting.GREEN));
        GrimoireData gd = stack.get(ModComponents.GRIMOIRE_DATA.get());
        if (gd == null) {
            tooltip.add(Component.literal("Unbound").withStyle(ChatFormatting.GRAY));
        } else {
            MagicAttribute attr = leaves == 5 ? MagicAttribute.ANTI_MAGIC : MagicAttribute.fromId(gd.attribute());
            if (attr != null) {
                tooltip.add(Component.literal(attr.displayName + " Magic").withStyle(attr.color));
            }
            tooltip.add(Component.literal("Bound to " + gd.ownerName()).withStyle(ChatFormatting.GRAY));
        }
        if (leaves == 4) {
            tooltip.add(Component.literal("More mana, +40% power, -25% cost").withStyle(ChatFormatting.AQUA));
        } else if (leaves == 5) {
            tooltip.add(Component.literal("Anti-Magic: spells cost hunger, not mana").withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.add(Component.literal("G: Grimoire Mode, number keys cast").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
