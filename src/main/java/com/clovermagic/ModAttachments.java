package com.clovermagic;

import java.util.function.Supplier;

import com.clovermagic.magic.MagicData;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CloverMagic.MODID);

    public static final Supplier<AttachmentType<MagicData>> MAGIC = ATTACHMENTS.register("magic",
            () -> AttachmentType.builder(() -> new MagicData())
                    .serialize(MagicData.CODEC)
                    .copyOnDeath()
                    .build());

    private ModAttachments() {
    }
}
