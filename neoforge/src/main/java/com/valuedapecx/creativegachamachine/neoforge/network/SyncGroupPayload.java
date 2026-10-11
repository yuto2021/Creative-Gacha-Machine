package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncGroupPayload(
        CompoundTag tag
) implements CustomPacketPayload {

    public static final Type<SyncGroupPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            CreativeGachaMachine.MOD_ID,
                            "sync_group"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SyncGroupPayload
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) ->
                            buf.writeNbt(payload.tag()),
                    buf ->
                            new SyncGroupPayload(
                                    buf.readNbt()
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static SyncGroupPayload create(
            List<GachaGroup> groups,
            HolderLookup.Provider registries
    ) {
        CompoundTag tag = new CompoundTag();

        ListTag list = new ListTag();

        for (GachaGroup group : groups) {
            list.add(
                    group.save(registries)
            );
        }

        tag.put(
                "Groups",
                list
        );

        return new SyncGroupPayload(tag);
    }
}