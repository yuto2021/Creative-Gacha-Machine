package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaTicketPaymentMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class OpenNormalPaymentPacket {

    public static void encode(OpenNormalPaymentPacket packet, FriendlyByteBuf buf) {

    }

    public static OpenNormalPaymentPacket decode(FriendlyByteBuf buf) {
        return new OpenNormalPaymentPacket();
    }

    public static void handle(OpenNormalPaymentPacket packet, Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaTicketPaymentMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                return;
            }

            player.openMenu(blockEntity.createPaymentMenu());
        });
    }
}