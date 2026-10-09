package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.client.ClientGroupCache;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncGroupPacket {
    private final CompoundTag tag;

    public SyncGroupPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public static void encode(SyncGroupPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.tag);
    }

    public static SyncGroupPacket decode(FriendlyByteBuf buf) {
        return new SyncGroupPacket(buf.readNbt());
    }

    public static void handle(SyncGroupPacket packet, Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            List<GachaGroup> groups = new ArrayList<>();

            if (packet.tag == null) {
                return;
            }

            ListTag list = packet.tag.getList("Groups", 10);

            for (int i = 0; i < list.size(); i++) {
                groups.add(GachaGroup.load(list.getCompound(i)));
            }

            ClientGroupCache.setGroups(groups);
        });
    }

    public static SyncGroupPacket create(List<GachaGroup> groups) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (GachaGroup group : groups) {
            list.add(group.save());
        }

        tag.put("Groups", list);

        return new SyncGroupPacket(tag);
    }
}