package com.valuedapecx.creativegachamachine.network;

import com.valuedapecx.creativegachamachine.client.ClientPageCache;
import com.valuedapecx.creativegachamachine.data.GachaPage;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SyncPagePacket {
    private final int page;
    private final CompoundTag pageTag;

    public SyncPagePacket(int page, CompoundTag pageTag) {
        this.page = page;
        this.pageTag = pageTag;
    }

    public static void encode(SyncPagePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.page);
        buf.writeNbt(packet.pageTag);
    }

    public static SyncPagePacket decode(FriendlyByteBuf buf) {
        return new SyncPagePacket(buf.readInt(), buf.readNbt());
    }

    public static void handle(SyncPagePacket packet, Supplier<NetworkManager.PacketContext> context) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        context.get().queue(() -> ClientPageCache.setPage(packet.page, GachaPage.load(
                packet.pageTag, minecraft.level.registryAccess()
        )));
    }
}
