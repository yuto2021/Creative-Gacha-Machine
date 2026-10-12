package com.valuedapecx.creativegachamachine.platform.fabric;

import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.network.*;
import com.valuedapecx.creativegachamachine.platform.PageAction;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class PlatformNetworkingImpl {

    public static void sendAddGroup() {
        ModPackets.CHANNEL.sendToServer(
                new AddGroupPacket()
        );
    }

    public static void sendSelectGroup(
            int groupIndex
    ) {
        ModPackets.CHANNEL.sendToServer(
                new SelectGroupPacket(
                        groupIndex
                )
        );
    }

    public static void sendDuplicateGroup(
            int groupIndex
    ) {
        ModPackets.CHANNEL.sendToServer(
                new DuplicateGroupPacket(
                        groupIndex
                )
        );
    }

    public static void sendDeleteGroup(
            int groupIndex
    ) {
        ModPackets.CHANNEL.sendToServer(
                new DeleteGroupPacket(
                        groupIndex
                )
        );
    }

    public static void sendGachaMissSlot(
            int slot
    ) {
        ModPackets.CHANNEL.sendToServer(
                new GachaMissSlotPacket(
                        slot
                )
        );
    }

    public static void sendGachaPageAction(
            PageAction action
    ) {
        ModPackets.CHANNEL.sendToServer(
                new GachaPageActionPacket(
                        action
                )
        );
    }

    public static void sendOpenNormalPayment() {
        ModPackets.CHANNEL.sendToServer(
                new OpenNormalPaymentPacket()
        );
    }

    public static void sendOpenTicketPayment() {
        ModPackets.CHANNEL.sendToServer(
                new OpenTicketPaymentPacket()
        );
    }

    public static void sendSaveGroupSettings(
            int groupIndex,
            String name,
            int rollCount
    ) {
        ModPackets.CHANNEL.sendToServer(
                new SaveGroupSettingsPacket(
                        groupIndex,
                        name,
                        rollCount
                )
        );
    }

    public static void sendSaveTicketSettings(
            String ticketName,
            int ticketType
    ) {
        ModPackets.CHANNEL.sendToServer(
                new SaveTicketSettingsPacket(
                        ticketName,
                        ticketType
                )
        );
    }

    public static void sendSaveWeight(
            int page,
            int slot,
            float weight
    ) {
        ModPackets.CHANNEL.sendToServer(
                new SaveWeightPacket(
                        page,
                        slot,
                        weight
                )
        );
    }

    public static void syncGroups(
            ServerPlayer player,
            List<GachaGroup> groups,
            HolderLookup.Provider registries
    ) {
        ModPackets.CHANNEL.sendToPlayer(
                player,
                SyncGroupPacket.create(
                        groups,
                        registries
                )
        );
    }

    public static void sendSyncPage(
            ServerPlayer player,
            int page,
            CompoundTag pageTag
    ) {
        ModPackets.CHANNEL.sendToPlayer(
                player,
                new SyncPagePacket(
                        page,
                        pageTag
                )
        );
    }
}