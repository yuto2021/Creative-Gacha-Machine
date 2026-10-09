package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class GachaPageActionPacket {
    public enum Action {
        PREVIOUS,
        NEXT,
        ADD,
        REMOVE
    }

    private final Action action;

    public GachaPageActionPacket(Action action) {
        this.action = action;
    }

    public static void encode(GachaPageActionPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.action);
    }

    public static GachaPageActionPacket decode(FriendlyByteBuf buf) {
        return new GachaPageActionPacket(
                buf.readEnum(Action.class)
        );
    }

    public static void handle(GachaPageActionPacket packet, Supplier<NetworkManager.PacketContext> context) {
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

            switch (packet.action) {
                case PREVIOUS -> {
                    blockEntity.previousPage();
                    player.containerMenu.broadcastChanges();
                }

                case NEXT -> {
                    blockEntity.nextPage();
                    player.containerMenu.broadcastChanges();
                }

                case ADD -> {
                    blockEntity.addPage();
                    player.containerMenu.broadcastChanges();
                }

                case REMOVE -> {
                    GachaMachineBlockEntity.RemovePageResult result = blockEntity.removePage();

                    switch (result) {
                        case LAST_PAGE -> player.displayClientMessage(Component.translatable(
                                                "message.creative_gacha_machine.cannot_delete_last_page"
                                        ).withStyle(ChatFormatting.RED), true);

                        case NOT_EMPTY -> player.displayClientMessage(Component.translatable(
                                                "message.creative_gacha_machine.page_not_empty"
                                        ).withStyle(ChatFormatting.RED), true);
                    }
                }
            }

            blockEntity.setChanged();

            System.out.println("PAGE ACTION: " + packet.action);
        });
    }
}