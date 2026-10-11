package com.valuedapecx.creativegachamachine.neoforge.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AddGroupPayload() implements CustomPacketPayload {

    public static final Type<AddGroupPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            "creative_gacha_machine",
                            "add_group"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            AddGroupPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {},
                    buf -> new AddGroupPayload()
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
