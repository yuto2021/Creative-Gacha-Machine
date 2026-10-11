package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DuplicateGroupPayload(int groupIndex)
        implements CustomPacketPayload {

    public static final Type<DuplicateGroupPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "duplicate_group"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            DuplicateGroupPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) ->
                            buf.writeInt(payload.groupIndex()),
                    buf ->
                            new DuplicateGroupPayload(
                                    buf.readInt()
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}