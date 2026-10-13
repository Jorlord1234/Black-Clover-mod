package com.clovermagic.magic;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Stored on a grimoire item: who owns it, its attribute and its serial. */
public record GrimoireData(UUID owner, String ownerName, String attribute, int generation) {
    public static final Codec<GrimoireData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(GrimoireData::owner),
            Codec.STRING.fieldOf("owner_name").forGetter(GrimoireData::ownerName),
            Codec.STRING.fieldOf("attribute").forGetter(GrimoireData::attribute),
            Codec.INT.fieldOf("generation").forGetter(GrimoireData::generation)
    ).apply(inst, GrimoireData::new));

    public static final StreamCodec<ByteBuf, GrimoireData> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, GrimoireData::owner,
            ByteBufCodecs.STRING_UTF8, GrimoireData::ownerName,
            ByteBufCodecs.STRING_UTF8, GrimoireData::attribute,
            ByteBufCodecs.VAR_INT, GrimoireData::generation,
            GrimoireData::new);
}
