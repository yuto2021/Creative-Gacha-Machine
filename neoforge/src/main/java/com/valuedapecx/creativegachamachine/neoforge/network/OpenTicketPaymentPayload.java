package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenTicketPaymentPayload()
        implements CustomPacketPayload {

    public static final Type<OpenTicketPaymentPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "open_ticket_payment"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            OpenTicketPaymentPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {},
                    buf -> new OpenTicketPaymentPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}