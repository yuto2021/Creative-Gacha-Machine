package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class AddGroupPacket {

    public AddGroupPacket() {}

    public static void encode(AddGroupPacket packet, FriendlyByteBuf buf) {}

    public static AddGroupPacket decode(FriendlyByteBuf buf) {
        return new AddGroupPacket();
    }

    public static void handle(AddGroupPacket packet, Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            GachaGroup group = new GachaGroup();

            group.setName("New Group");

            group.setRollCount(1);

            blockEntity.getGroups().add(group);

            blockEntity.setChanged();

            ModPackets.CHANNEL.sendToPlayer(player, SyncGroupPacket.create(blockEntity.getGroups()));

            player.containerMenu.broadcastChanges();
        });
    }
}