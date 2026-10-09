package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class SaveGroupSettingsPacket {

    private final int groupIndex;
    private final String name;
    private final int rollCount;

    public SaveGroupSettingsPacket(int groupIndex, String name, int rollCount) {
        this.groupIndex = groupIndex;
        this.name = name;
        this.rollCount = rollCount;
    }

    public static void encode(SaveGroupSettingsPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.groupIndex);
        buf.writeUtf(packet.name);
        buf.writeInt(packet.rollCount);
    }

    public static SaveGroupSettingsPacket decode(FriendlyByteBuf buf) {
        return new SaveGroupSettingsPacket(buf.readInt(), buf.readUtf(), buf.readInt());
    }

    public static void handle(SaveGroupSettingsPacket packet, Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            if (packet.groupIndex < 0 || packet.groupIndex >= blockEntity.getGroups().size()) {
                return;
            }

            GachaGroup group = blockEntity.getGroups().get(packet.groupIndex);

            group.setName(packet.name);

            group.setRollCount(Math.max(1, packet.rollCount));

            blockEntity.setChanged();

            ModPackets.CHANNEL.sendToPlayer(player, SyncGroupPacket.create(blockEntity.getGroups()));
        });
    }
}
