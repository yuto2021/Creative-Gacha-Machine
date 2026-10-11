package com.valuedapecx.creativegachamachine.platform.neoforge;

import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.neoforge.network.*;
import com.valuedapecx.creativegachamachine.platform.PageAction;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class PlatformNetworkingImpl {

    public static void sendAddGroup() {
        PacketDistributor.sendToServer(
                new AddGroupPayload()
        );
    }

    public static void sendSelectGroup(
            int groupIndex
    ) {
        PacketDistributor.sendToServer(
                new SelectGroupPayload(
                        groupIndex
                )
        );
    }

    public static void sendDuplicateGroup(
            int groupIndex
    ) {
        PacketDistributor.sendToServer(
                new DuplicateGroupPayload(
                        groupIndex
                )
        );
    }

    public static void sendDeleteGroup(
            int groupIndex
    ) {
        PacketDistributor.sendToServer(
                new DeleteGroupPayload(
                        groupIndex
                )
        );
    }

    public static void sendGachaMissSlot(
            int slot
    ) {
        PacketDistributor.sendToServer(
                new GachaMissSlotPayload(
                        slot
                )
        );
    }

    public static void sendGachaPageAction(
            PageAction action
    ) {
        PacketDistributor.sendToServer(
                new GachaPageActionPayload(
                        action
                )
        );
    }

    public static void sendOpenNormalPayment() {
        PacketDistributor.sendToServer(
                new OpenNormalPaymentPayload()
        );
    }

    public static void sendOpenTicketPayment() {
        PacketDistributor.sendToServer(
                new OpenTicketPaymentPayload()
        );
    }

    public static void sendSaveGroupSettings(
            int groupIndex,
            String name,
            int rollCount
    ) {
        PacketDistributor.sendToServer(
                new SaveGroupSettingsPayload(
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
        PacketDistributor.sendToServer(
                new SaveTicketSettingsPayload(
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
        PacketDistributor.sendToServer(
                new SaveWeightPayload(
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
        PacketDistributor.sendToPlayer(
                player,
                SyncGroupPayload.create(
                        groups,
                        registries
                )
        );
    }
}