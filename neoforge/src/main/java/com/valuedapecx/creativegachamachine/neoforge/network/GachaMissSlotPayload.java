package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GachaMissSlotPayload(int slot)
        implements CustomPacketPayload {

    public static final Type<GachaMissSlotPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "gacha_miss_slot"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            GachaMissSlotPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) ->
                            buf.writeInt(payload.slot()),
                    buf ->
                            new GachaMissSlotPayload(
                                    buf.readInt()
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
