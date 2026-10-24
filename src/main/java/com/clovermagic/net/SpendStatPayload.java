package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: spend one stat point on stat 0..3 (power, mana, control, vitality). */
public record SpendStatPayload(int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpendStatPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "spend_stat"));

    public static final StreamCodec<ByteBuf, SpendStatPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(SpendStatPayload::new, SpendStatPayload::index);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
