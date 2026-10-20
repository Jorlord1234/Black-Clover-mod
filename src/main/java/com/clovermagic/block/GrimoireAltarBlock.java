package com.clovermagic.block;

import com.clovermagic.Config;
import com.clovermagic.ModAttachments;
import com.clovermagic.ModItems;
import com.clovermagic.item.GrimoireItem;
import com.clovermagic.magic.GrimoireManager;
import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.MagicSync;
import com.mojang.serialization.MapCodec;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The altar inside a Grimoire Tower.
 * - No grimoire yet: the ceremony. The books fly out and one chooses you.
 * - Grimoire lost (it crumbled when you died): reclaim it for one Grimoire Sigil.
 */
public class GrimoireAltarBlock extends Block {
    public static final MapCodec<GrimoireAltarBlock> CODEC = simpleCodec(GrimoireAltarBlock::new);

    public GrimoireAltarBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    /** Page of Rebirth: reroll your grimoire. Enchanted book with Fortune III: a Three-Leaf becomes a Four-Leaf. */
    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                                  Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (!(player instanceof ServerPlayer sp) || level.isClientSide()) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ServerLevel sl = (ServerLevel) level;
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        boolean creative = sp.getAbilities().instabuild;

        if (stack.is(ModItems.PAGE_OF_REBIRTH.get()) && data.leaves > 0) {
            ItemStack old = GrimoireManager.find(sp, data);
            if (!old.isEmpty()) old.setCount(0);
            data.leaves = 0;
            data.attribute = "none";
            data.xp = 0;
            data.selected = 0;
            data.unlocked.clear();
            data.generation++;
            if (!creative) stack.shrink(1);
            sp.sendSystemMessage(Component.literal("Your old grimoire turns to dust. The altar chooses again...").withStyle(ChatFormatting.LIGHT_PURPLE));
            ceremony(sp, data, sl, pos);
            return net.minecraft.world.ItemInteractionResult.SUCCESS;
        }

        net.minecraft.world.item.enchantment.ItemEnchantments ench =
                stack.get(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS);
        if (ench != null && data.leaves == 3) {
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> fortune = level.registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getHolderOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE);
            if (ench.getLevel(fortune) >= 3) {
                ItemStack old = GrimoireManager.find(sp, data);
                if (!old.isEmpty()) old.setCount(0);
                if (!creative) stack.shrink(1);
                data.leaves = 4;
                data.mana = GrimoireItem.maxMana(data.level(), 4);
                GrimoireManager.issue(sp, data);
                MagicSync.send(sp);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(sp,
                        new com.clovermagic.net.CutscenePayload(data.attribute, 4));
                sp.sendSystemMessage(Component.literal("Within the four-leaf lies good luck. Your grimoire has grown a fourth leaf!")
                        .withStyle(ChatFormatting.GREEN));
                sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.4, pos.getZ() + 0.5, 60, 0.8, 0.8, 0.8, 0.1);
                sl.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0f, 1.4f);
                return net.minecraft.world.ItemInteractionResult.SUCCESS;
            }
        }
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide() || !(player instanceof ServerPlayer sp)) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel sl = (ServerLevel) level;
        MagicData data = sp.getData(ModAttachments.MAGIC.get());

        if (data.leaves == 0) {
            ceremony(sp, data, sl, pos);
            return InteractionResult.SUCCESS;
        }

        if (!GrimoireManager.find(sp, data).isEmpty()) {
            sp.displayClientMessage(Component.literal("Your grimoire is already with you.")
                    .withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.SUCCESS;
        }

        // Reclaim: costs one Grimoire Sigil
        Inventory inv = sp.getInventory();
        int slot = -1;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(ModItems.GRIMOIRE_SIGIL.get())) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            sp.displayClientMessage(Component.literal("Your grimoire is lost. Bring a Grimoire Sigil to reclaim it.")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.SUCCESS;
        }
        inv.getItem(slot).shrink(1);
        GrimoireManager.issue(sp, data);
        sl.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 60, 0.8, 0.8, 0.8, 0.8);
        sl.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
        sp.sendSystemMessage(Component.literal("Your grimoire returns to you.").withStyle(ChatFormatting.GREEN));
        MagicSync.send(sp);
        return InteractionResult.SUCCESS;
    }

    private static void ceremony(ServerPlayer sp, MagicData data, ServerLevel level, BlockPos pos) {
        RandomSource r = sp.getRandom();
        int leaves;
        MagicAttribute attr;
        if (r.nextInt(Config.FIVE_LEAF_ONE_IN.get()) == 0) {
            leaves = 5;
            attr = MagicAttribute.ANTI_MAGIC;
        } else {
            leaves = r.nextInt(Config.FOUR_LEAF_ONE_IN.get()) == 0 ? 4 : 3;
            attr = MagicAttribute.roll(r);
        }
        data.leaves = leaves;
        data.attribute = attr.id;
        data.xp = 0;
        data.selected = 0;
        data.mana = GrimoireItem.maxMana(1, leaves);
        GrimoireManager.issue(sp, data);
        MagicSync.send(sp);

        // the cutscene on the player's screen shows the name and attribute (it replaces the old title text)
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(sp,
                new com.clovermagic.net.CutscenePayload(attr.id, leaves));
        sp.sendSystemMessage(Component.literal("The grimoires fly out of the altar... one has chosen you."));

        double x = pos.getX() + 0.5, y = pos.getY() + 1.4, z = pos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 120, 1.2, 1.0, 1.2, 1.2);
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 40, 0.8, 0.8, 0.8, 0.15);
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, sp.getX(), sp.getY() + 1, sp.getZ(), 50, 0.5, 0.9, 0.5, 0.3);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.2f, 0.8f);
        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.8f, 1.2f);
        if (leaves == 5) {
            level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 0.6f, 1.6f);
        }
    }
}
