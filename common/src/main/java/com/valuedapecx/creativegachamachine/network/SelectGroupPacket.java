package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class SelectGroupPacket {
    private final int groupIndex;

    public SelectGroupPacket(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public static void encode(SelectGroupPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.groupIndex);
    }

    public static SelectGroupPacket decode(FriendlyByteBuf buf) {
        return new SelectGroupPacket(buf.readInt());
    }

    public static void handle(SelectGroupPacket packet, Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            blockEntity.setCurrentGroup(packet.groupIndex);

            blockEntity.setChanged();

            blockEntity.setCurrentGroup(packet.groupIndex);

            blockEntity.setChanged();

            ModPackets.CHANNEL.sendToPlayer(player, SyncGroupPacket.create(blockEntity.getGroups()));
        });
    }
}
