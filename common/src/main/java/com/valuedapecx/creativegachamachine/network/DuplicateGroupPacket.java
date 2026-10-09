package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class DuplicateGroupPacket {

    private final int groupIndex;

    public DuplicateGroupPacket(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public static void encode(
            DuplicateGroupPacket packet,
            FriendlyByteBuf buf
    ) {
        buf.writeInt(packet.groupIndex);
    }

    public static DuplicateGroupPacket decode(
            FriendlyByteBuf buf
    ) {
        return new DuplicateGroupPacket(
                buf.readInt()
        );
    }

    public static void handle(
            DuplicateGroupPacket packet,
            Supplier<NetworkManager.PacketContext> context
    ) {

        context.get().queue(() -> {

            ServerPlayer player =
                    (ServerPlayer) context.get()
                            .getPlayer();

            if (!(player.containerMenu
                    instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity =
                    menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            if (
                    packet.groupIndex < 0
                            ||
                            packet.groupIndex >= blockEntity.getGroups().size()
            ) {
                return;
            }

            GachaGroup original =
                    blockEntity.getGroups()
                            .get(packet.groupIndex);

            GachaGroup copy =
                    GachaGroup.load(
                            original.save()
                    );

            copy.setName(
                    original.getName()
                            + " Copy"
            );

            blockEntity.getGroups()
                    .add(copy);

            blockEntity.setChanged();

            ModPackets.CHANNEL.sendToPlayer(
                    player,
                    SyncGroupPacket.create(
                            blockEntity.getGroups()
                    )
            );
        });
    }
}