package com.clovermagic.net;

import com.clovermagic.client.ClientMagicState;
import com.clovermagic.magic.CastHandler;
import com.clovermagic.magic.SkillLogic;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("4");
        registrar.playToServer(CastPayload.TYPE, CastPayload.STREAM_CODEC, ModNetwork::handleCast);
        registrar.playToServer(UnlockPayload.TYPE, UnlockPayload.STREAM_CODEC, ModNetwork::handleUnlock);
        registrar.playToServer(RespecPayload.TYPE, RespecPayload.STREAM_CODEC, ModNetwork::handleRespec);
        registrar.playToServer(SpendStatPayload.TYPE, SpendStatPayload.STREAM_CODEC, ModNetwork::handleSpendStat);
        registrar.playToClient(StatsPayload.TYPE, StatsPayload.STREAM_CODEC, ModNetwork::handleStats);
        registrar.playToClient(SyncPayload.TYPE, SyncPayload.STREAM_CODEC, ModNetwork::handleSync);
        registrar.playToClient(CutscenePayload.TYPE, CutscenePayload.STREAM_CODEC, ModNetwork::handleCutscene);
    }

    private static void handleCast(CastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                CastHandler.handle(sp, payload.slot());
            }
        });
    }

    private static void handleUnlock(UnlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                SkillLogic.tryUnlock(sp, payload.node());
            }
        });
    }

    private static void handleRespec(RespecPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                SkillLogic.tryRespec(sp, payload.branch());
            }
        });
    }

    private static void handleCutscene(CutscenePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.clovermagic.magic.MagicAttribute attr = com.clovermagic.magic.MagicAttribute.fromId(payload.attribute());
            if (attr != null) {
                net.minecraft.client.Minecraft.getInstance().setScreen(
                        new com.clovermagic.client.GrimoireCutsceneScreen(attr, payload.leaves()));
            }
        });
    }

    private static void handleSpendStat(SpendStatPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp) {
                com.clovermagic.magic.StatLogic.spend(sp, payload.index());
            }
        });
    }

    private static void handleStats(StatsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientMagicState.stats[0] = payload.power();
            ClientMagicState.stats[1] = payload.mana();
            ClientMagicState.stats[2] = payload.control();
            ClientMagicState.stats[3] = payload.vitality();
            ClientMagicState.squad = payload.squad();
            ClientMagicState.squadPoints = payload.squadPoints();
        });
    }

    private static void handleSync(SyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientMagicState.apply(payload));
    }
}
