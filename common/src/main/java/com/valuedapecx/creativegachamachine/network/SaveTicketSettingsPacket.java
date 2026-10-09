package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaTicketPaymentMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class SaveTicketSettingsPacket {
    private final String ticketName;
    private final int ticketType;

    public SaveTicketSettingsPacket(String ticketName, int ticketType) {
        this.ticketName = ticketName;
        this.ticketType = ticketType;
    }

    public static void encode(SaveTicketSettingsPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.ticketName);
        buf.writeInt(packet.ticketType);
    }

    public static SaveTicketSettingsPacket decode(FriendlyByteBuf buf) {
        return new SaveTicketSettingsPacket(buf.readUtf(), buf.readInt());
    }

    public static void handle(SaveTicketSettingsPacket packet, Supplier<NetworkManager.PacketContext> context) {
        System.out.println("SAVE PACKET RECEIVED");

        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaTicketPaymentMenu menu)) {
                CreativeGachaMachine.LOGGER.warn(
                        "Ticket settings were not saved: failed to convert containerMenu to GachaTicketPaymentMenu"
                );

                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                CreativeGachaMachine.LOGGER.warn("Ticket settings were not saved: Block Entity is null!");
                return;
            }

            blockEntity.setRequiredTicketName(packet.ticketName);

            blockEntity.setRequiredTicketType(packet.ticketType);

            blockEntity.setTicketOnlyPayment(true);

            System.out.println("Ticket settings were saved! ticketName:" + packet.ticketName +
                    ", ticketType:" + packet.ticketType);

            blockEntity.setChanged();

            CreativeGachaMachine.LOGGER.warn(
                    "AFTER SAVE -> name={}, type={}, ticket={}",
                    blockEntity.getRequiredTicketName(),
                    blockEntity.getRequiredTicketType(),
                    blockEntity.isTicketOnlyPayment()
            );
        });
    }
}
