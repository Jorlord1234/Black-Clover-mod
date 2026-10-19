package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: unlock this skill tree node. */
public record UnlockPayload(String node) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UnlockPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "unlock"));

    public static final StreamCodec<ByteBuf, UnlockPayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(UnlockPayload::new, UnlockPayload::node);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
