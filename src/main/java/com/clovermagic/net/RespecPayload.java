package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client -> server: reset this branch of the skill tree (costs a Page of Forgetting). */
public record RespecPayload(String branch) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RespecPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "respec"));

    public static final StreamCodec<ByteBuf, RespecPayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(RespecPayload::new, RespecPayload::branch);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
