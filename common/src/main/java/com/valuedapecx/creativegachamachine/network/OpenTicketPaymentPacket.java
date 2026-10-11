package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaPaymentMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class OpenTicketPaymentPacket {
    public OpenTicketPaymentPacket() {
        System.out.println("SEND OPEN TICKET PAYMENT");
    }

    public static void encode(OpenTicketPaymentPacket packet, FriendlyByteBuf buf) {
    }

    public static OpenTicketPaymentPacket decode(FriendlyByteBuf buf) {
        return new OpenTicketPaymentPacket();
    }

    public static void handle(OpenTicketPaymentPacket packet, Supplier<NetworkManager.PacketContext> context) {
        System.out.println("HANDLE OPEN TICKET PAYMENT");

        context.get().queue(() -> {
            System.out.println("QUEUE OPEN TICKET PAYMENT");

            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaPaymentMenu menu)) {
                return;
            }

            System.out.println(player.containerMenu.getClass().getName());

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            player.openMenu(blockEntity.createTicketPaymentMenu());
        });
    }
}
