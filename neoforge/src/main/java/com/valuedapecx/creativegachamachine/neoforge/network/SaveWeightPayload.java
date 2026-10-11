package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SaveWeightPayload(
        int page,
        int slot,
        float weight
        ) implements CustomPacketPayload {

    public static final Type<SaveWeightPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "save_weight"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SaveWeightPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.page());
                        buf.writeInt(payload.slot());
                        buf.writeFloat(payload.weight());
                    },
                    buf -> new SaveWeightPayload(
                            buf.readInt(),
                            buf.readInt(),
                            buf.readFloat()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}