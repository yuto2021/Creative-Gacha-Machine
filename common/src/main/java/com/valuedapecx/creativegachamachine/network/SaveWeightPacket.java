package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaPage;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class SaveWeightPacket {
    private final int page;
    private final int slot;
    private final float weight;

    public SaveWeightPacket(int page, int slot, float weight) {
        this.page = page;
        this.slot = slot;
        this.weight = weight;
    }

    public static void encode(SaveWeightPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.page);
        buf.writeInt(packet.slot);
        buf.writeFloat(packet.weight);
    }

    public static SaveWeightPacket decode(FriendlyByteBuf buf) {
        return new SaveWeightPacket(buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public static void handle(SaveWeightPacket packet, Supplier<NetworkManager.PacketContext> context) {
        System.out.println("SAVE WEIGHT PACKET RECEIVED: Slot: " + packet.slot + ", Weight: " + packet.weight);

        context.get().queue(() -> {
            ServerPlayer player = (ServerPlayer) context.get().getPlayer();

            if (!(player.containerMenu instanceof GachaMachineMenu menu)) {
                return;
            }

            GachaMachineBlockEntity blockEntity = menu.getBlockEntity();

            if (blockEntity == null) {
                CreativeGachaMachine.LOGGER.warn("Weight is not saved: BlockEntity is null!");
                return;
            }

            GachaPage page = blockEntity.getCurrentPageData();

            page.setWeight(packet.slot, packet.weight);

            blockEntity.setChanged();

            ModPackets.CHANNEL.sendToPlayer(player, new SyncPagePacket(blockEntity.getCurrentPage(), page.save()));

            System.out.println(blockEntity.saveWithoutMetadata());
        });
    }
}
