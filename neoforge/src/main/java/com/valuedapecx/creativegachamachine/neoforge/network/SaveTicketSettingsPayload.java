package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SaveTicketSettingsPayload(
        String ticketName,
        int ticketType
) implements CustomPacketPayload {

    public static final Type<SaveTicketSettingsPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "save_ticket_settings"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SaveTicketSettingsPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUtf(payload.ticketName());
                        buf.writeInt(payload.ticketType());
                    },
                    buf -> new SaveTicketSettingsPayload(
                            buf.readUtf(),
                            buf.readInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}