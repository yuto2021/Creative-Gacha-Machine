package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class DeleteGroupPacket {

    private final int groupIndex;

    public DeleteGroupPacket(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public static void encode(DeleteGroupPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.groupIndex);
    }

    public static DeleteGroupPacket decode(FriendlyByteBuf buf) {
        return new DeleteGroupPacket(
                buf.readInt()
        );
    }

    public static void handle(DeleteGroupPacket packet, Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (player == null) {
                return;
            }

            if (!(player.containerMenu instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            if (packet.groupIndex < 0 || packet.groupIndex >= blockEntity.getGroupCount()) {
                return;
            }

            if (!blockEntity.canDeleteGroup()) {
                return;
            }

            blockEntity.removeGroup(packet.groupIndex);

            blockEntity.setChanged();

            blockEntity.removeGroup(packet.groupIndex);

            blockEntity.setChanged();

            ModPackets.CHANNEL.sendToPlayer(player, SyncGroupPacket.create(blockEntity.getGroups()));

            player.containerMenu.broadcastChanges();
        });
    }
}