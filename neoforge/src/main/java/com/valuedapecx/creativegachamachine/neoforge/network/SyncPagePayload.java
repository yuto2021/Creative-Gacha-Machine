package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncPagePayload(
        int page,
        CompoundTag pageTag
) implements CustomPacketPayload {

    public static final Type<SyncPagePayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "sync_page"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SyncPagePayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.page());
                        buf.writeNbt(payload.pageTag());
                    },
                    buf -> new SyncPagePayload(
                            buf.readInt(),
                            buf.readNbt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}