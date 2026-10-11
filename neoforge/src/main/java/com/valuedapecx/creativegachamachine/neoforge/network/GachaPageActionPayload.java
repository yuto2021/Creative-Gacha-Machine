package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.platform.PageAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GachaPageActionPayload(
        PageAction action
) implements CustomPacketPayload {
    public static final Type<GachaPageActionPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "gacha_page_action"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            GachaPageActionPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) ->
                            buf.writeEnum(payload.action()),
                    buf ->
                            new GachaPageActionPayload(
                                    buf.readEnum(PageAction.class)
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
