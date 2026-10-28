package com.clovermagic.magic;

import com.clovermagic.Config;
import com.clovermagic.ModAttachments;
import com.clovermagic.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * Devil's Contract: a craftable item you can use anywhere. Using it offers two deals as clickable chat buttons
 * (they run /devildeal): a second grimoire, or the fifth leaf. Each costs 2 hearts of max health, for ever.
 * In the series a contract gives you a share of a devil's attribute, and a devil can only live in a five-leaf grimoire.
 */
public final class DevilDeals {
    public static final String SECOND_GRIMOIRE = "second_grimoire";
    public static final String FIFTH_LEAF = "fifth_leaf";

    private DevilDeals() {
    }

    /** Shows the two choices. */
    public static void offer(ServerPlayer sp) {
        if (!Config.ALLOW_DEVIL_DEALS.get()) {
            sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.the_devils_are_not_listening_on_this_ser_03f7",
                    "The devils are not listening on this server.").withStyle(ChatFormatting.RED), true);
            return;
        }
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        if (data.leaves <= 0) {
            sp.displayClientMessage(Component.literal("You need a grimoire before a devil will listen.").withStyle(ChatFormatting.RED), true);
            return;
        }
        sp.sendSystemMessage(Component.literal("The contract burns warm in your hand. Choose your deal (each costs you 2 hearts, for ever):")
                .withStyle(ChatFormatting.DARK_RED));
        sp.sendSystemMessage(button("[ Second Grimoire ]", "/devildeal " + SECOND_GRIMOIRE,
                "A devil lends you a second grimoire with a new attribute. Sneak + right-click a grimoire to switch."));
        sp.sendSystemMessage(button("[ Fifth Leaf ]", "/devildeal " + FIFTH_LEAF,
                "A devil moves into your grimoire: 50% more mana and 25% stronger spells."));
    }

    private static MutableComponent button(String label, String command, String hover) {
        return Component.literal(label).withStyle(s -> s.withColor(ChatFormatting.RED).withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hover))));
    }

    /** Runs the chosen deal and spends one Devil's Contract from the inventory. Returns true if the deal was made. */
    public static boolean apply(ServerPlayer sp, String kind) {
        if (!Config.ALLOW_DEVIL_DEALS.get()) {
            sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.the_devils_are_not_listening_on_this_ser_03f7",
                    "The devils are not listening on this server.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        if (data.leaves <= 0) {
            sp.displayClientMessage(Component.literal("You need a grimoire before a devil will listen.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        boolean creative = sp.getAbilities().instabuild;
        ItemStack contract = ItemStack.EMPTY;
        for (ItemStack s : sp.getInventory().items) {
            if (s.is(ModItems.DEVIL_CONTRACT.get())) {
                contract = s;
                break;
            }
        }
        if (contract.isEmpty() && sp.getOffhandItem().is(ModItems.DEVIL_CONTRACT.get())) contract = sp.getOffhandItem();
        if (contract.isEmpty() && !creative) {
            sp.displayClientMessage(Component.literal("You need a Devil's Contract.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        ServerLevel sl = sp.serverLevel();
        if (SECOND_GRIMOIRE.equals(kind)) {
            if (data.leaves2 > 0) {
                sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.you_already_made_this_deal_d0c3",
                        "You already made this deal.").withStyle(ChatFormatting.RED), true);
                return false;
            }
            RandomSource r = sp.getRandom();
            MagicAttribute attr2;
            do {
                attr2 = MagicAttribute.roll(r);
            } while (attr2.id.equals(data.attribute));
            data.attribute2 = attr2.id;
            data.leaves2 = r.nextInt(Config.FOUR_LEAF_ONE_IN.get()) == 0 ? 4 : 3;
            data.xp2 = 0;
            data.generation2 = 1000 + r.nextInt(1_000_000);
            data.swapGrimoires();
            ItemStack second = GrimoireManager.create(sp, data);
            data.swapGrimoires();
            sp.getInventory().placeItemBackInInventory(second);
            if (!creative && !contract.isEmpty()) contract.shrink(1);
            price(sp, "second_grimoire");
            QuestHooks.award(sp, "second_grimoire");
            MagicSync.send(sp);
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(sp,
                    new com.clovermagic.net.CutscenePayload(attr2.id, data.leaves2));
            sp.sendSystemMessage(Component.literal("The devil grants you a second grimoire: " + attr2.displayName
                    + ". It costs you 2 hearts, for ever. Sneak + right-click a grimoire to switch.").withStyle(ChatFormatting.DARK_RED));
            fx(sl, sp);
            return true;
        }
        if (FIFTH_LEAF.equals(kind)) {
            if (data.devilMark) {
                sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.you_already_made_this_deal_d0c3",
                        "You already made this deal.").withStyle(ChatFormatting.RED), true);
                return false;
            }
            data.devilMark = true;
            data.mana = data.maxMana();
            if (!creative && !contract.isEmpty()) contract.shrink(1);
            price(sp, "devil_mark");
            QuestHooks.award(sp, "devil_mark");
            MagicSync.send(sp);
            sp.sendSystemMessage(Component.translatableWithFallback("msg.clovermagic.a_fifth_leaf_grows_on_your_grimoire_blac_5097",
                    "A fifth leaf grows on your grimoire, black as the devil's contract: 50% more mana and 25% stronger spells. It costs you 2 hearts, for ever.")
                    .withStyle(ChatFormatting.DARK_RED));
            fx(sl, sp);
            return true;
        }
        return false;
    }

    private static void price(ServerPlayer sp, String deal) {
        net.minecraft.world.entity.ai.attributes.AttributeInstance inst =
                sp.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (inst == null) return;
        net.minecraft.resources.ResourceLocation id =
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("clovermagic", "devil_price_" + deal);
        if (!inst.hasModifier(id)) {
            inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(id, -4.0,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
        }
        if (sp.getHealth() > sp.getMaxHealth()) sp.setHealth(sp.getMaxHealth());
    }

    private static void fx(ServerLevel sl, ServerPlayer sp) {
        sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, sp.getX(), sp.getY() + 1.2, sp.getZ(), 80, 0.8, 0.8, 0.8, 0.1);
        sl.sendParticles(ParticleTypes.SQUID_INK, sp.getX(), sp.getY() + 1.2, sp.getZ(), 40, 0.8, 0.8, 0.8, 0.05);
        sl.playSound(null, sp.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.7f, 0.6f);
    }
}
