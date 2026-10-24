package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server -> client: the four stat values. */
public record StatsPayload(int power, int mana, int control, int vitality) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StatsPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "stats"));

    public static final StreamCodec<ByteBuf, StatsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StatsPayload::power,
            ByteBufCodecs.VAR_INT, StatsPayload::mana,
            ByteBufCodecs.VAR_INT, StatsPayload::control,
            ByteBufCodecs.VAR_INT, StatsPayload::vitality,
            StatsPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
