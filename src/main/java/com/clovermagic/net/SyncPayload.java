package com.clovermagic.net;

import com.clovermagic.CloverMagic;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server -> client: everything the Grimoire Mode HUD needs. */
public record SyncPayload(String attribute, int leaves, int mana, int maxMana, int xp, boolean hasGrimoire, int[] cooldowns,
                          java.util.List<String> unlocked, int maxLevel, int points)
        implements CustomPacketPayload {
    public static final int SLOTS = 12;

    public static final CustomPacketPayload.Type<SyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CloverMagic.MODID, "sync"));

    public static final StreamCodec<FriendlyByteBuf, SyncPayload> STREAM_CODEC =
            StreamCodec.of(SyncPayload::write, SyncPayload::read);

    private static void write(FriendlyByteBuf buf, SyncPayload p) {
        buf.writeUtf(p.attribute);
        buf.writeVarInt(p.leaves);
        buf.writeVarInt(p.mana);
        buf.writeVarInt(p.maxMana);
        buf.writeVarInt(p.xp);
        buf.writeBoolean(p.hasGrimoire);
        for (int i = 0; i < SLOTS; i++) {
            buf.writeVarInt(i < p.cooldowns.length ? p.cooldowns[i] : 0);
        }
        buf.writeVarInt(p.unlocked.size());
        for (String id : p.unlocked) buf.writeUtf(id);
        buf.writeVarInt(p.maxLevel);
        buf.writeVarInt(p.points);
    }

    private static SyncPayload read(FriendlyByteBuf buf) {
        String attribute = buf.readUtf();
        int leaves = buf.readVarInt();
        int mana = buf.readVarInt();
        int maxMana = buf.readVarInt();
        int xp = buf.readVarInt();
        boolean has = buf.readBoolean();
        int[] cds = new int[SLOTS];
        for (int i = 0; i < SLOTS; i++) cds[i] = buf.readVarInt();
        int n = buf.readVarInt();
        java.util.List<String> unlocked = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) unlocked.add(buf.readUtf());
        int maxLevel = buf.readVarInt();
        int points = buf.readVarInt();
        return new SyncPayload(attribute, leaves, mana, maxMana, xp, has, cds, unlocked, maxLevel, points);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
