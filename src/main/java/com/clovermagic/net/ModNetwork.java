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

    private static void handleSync(SyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientMagicState.apply(payload));
    }
}
