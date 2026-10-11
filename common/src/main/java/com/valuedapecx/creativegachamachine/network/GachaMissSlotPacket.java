package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class GachaMissSlotPacket {
    private final int slot;

    public GachaMissSlotPacket(int slot) {
        this.slot = slot;
    }

    public static void encode(GachaMissSlotPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
    }

    public static GachaMissSlotPacket decode(FriendlyByteBuf buf) {
        return new GachaMissSlotPacket(buf.readInt());
    }

    public static void handle(GachaMissSlotPacket packet, Supplier<NetworkManager.PacketContext> context) {
        System.out.println("MISS PACKET RECEIVED");

        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            System.out.println("TOGGLE SLOT = " + packet.slot);

            blockEntity.toggleMissSlot(packet.slot);

            player.containerMenu.broadcastChanges();
        });
    }
}
