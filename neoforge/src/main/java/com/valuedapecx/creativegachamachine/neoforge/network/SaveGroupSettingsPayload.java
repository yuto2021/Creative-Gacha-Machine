package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SaveGroupSettingsPayload(
        int groupIndex,
        String name,
        int rollCount
) implements CustomPacketPayload {

    public static final Type<SaveGroupSettingsPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "save_group_settings"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SaveGroupSettingsPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.groupIndex());
                        buf.writeUtf(payload.name());
                        buf.writeInt(payload.rollCount());
                    },
                    buf -> new SaveGroupSettingsPayload(
                            buf.readInt(),
                            buf.readUtf(),
                            buf.readInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}