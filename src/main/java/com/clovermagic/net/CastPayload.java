package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: cast the spell in this slot (0-based). */
public record CastPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CastPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "cast"));

    public static final StreamCodec<ByteBuf, CastPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(CastPayload::new, CastPayload::slot);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
