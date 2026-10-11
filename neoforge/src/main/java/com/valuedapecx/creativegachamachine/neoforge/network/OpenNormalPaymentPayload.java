package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenNormalPaymentPayload()
        implements CustomPacketPayload {

    public static final Type<OpenNormalPaymentPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "open_normal_payment"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            OpenNormalPaymentPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                    },
                    buf -> new OpenNormalPaymentPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}