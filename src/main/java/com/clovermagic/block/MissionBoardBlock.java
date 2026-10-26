package com.clovermagic.block;

import com.clovermagic.ModAttachments;
import com.clovermagic.magic.MagicData;
import com.clovermagic.magic.Missions;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** The Mission Board: right-click for a mission scroll (one mission at a time). */
public class MissionBoardBlock extends Block {
    public MissionBoardBlock(Properties props) {
        super(props);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide() || !(player instanceof ServerPlayer sp)) return InteractionResult.SUCCESS;
        MagicData data = sp.getData(ModAttachments.MAGIC.get());
        if (data.leaves == 0) {
            sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.only_mages_with_a_grimoire_can_take_miss_814d", "Only mages with a grimoire can take missions.").withStyle(ChatFormatting.RED), true);
            return InteractionResult.CONSUME;
        }
        if (Missions.hasMission(sp)) {
            sp.displayClientMessage(Component.translatableWithFallback("msg.clovermagic.you_already_have_a_mission_finish_it_fir_ac5a", "You already have a mission. Finish it first.").withStyle(ChatFormatting.RED), true);
            return InteractionResult.CONSUME;
        }
        sp.getInventory().placeItemBackInInventory(Missions.create(sp.getRandom(), data.level(), sp.isShiftKeyDown() && data.squad != null && !data.squad.isEmpty()));
        level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, 1.0f);
        sp.sendSystemMessage(Component.translatableWithFallback("msg.clovermagic.you_take_a_mission_from_the_board_hover_50cc", "You take a mission from the board. Hover over the scroll to read it. (Sneak + click as a squad member for a bigger squad mission.)").withStyle(ChatFormatting.GOLD));
        return InteractionResult.CONSUME;
    }
}
