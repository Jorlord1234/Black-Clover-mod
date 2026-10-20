package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server -> client: play the "a grimoire chooses you" cutscene for this attribute and leaf count. */
public record CutscenePayload(String attribute, int leaves) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CutscenePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "cutscene"));

    public static final StreamCodec<ByteBuf, CutscenePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CutscenePayload::attribute,
            ByteBufCodecs.VAR_INT, CutscenePayload::leaves,
            CutscenePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
