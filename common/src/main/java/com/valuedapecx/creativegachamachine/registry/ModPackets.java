package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.network.*;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public final class ModPackets {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(
            CreativeGachaMachine.MOD_ID, "main"
    ));

    public static final ResourceLocation OPEN_TICKET_PAYMENT = new ResourceLocation(
            CreativeGachaMachine.MOD_ID,
            "open_ticket_payment"
    );

    public static void init() {
        CHANNEL.register(
                GachaPageActionPacket.class, GachaPageActionPacket::encode,
                GachaPageActionPacket::decode, GachaPageActionPacket::handle
        );
        CHANNEL.register(
                GachaMissSlotPacket.class, GachaMissSlotPacket::encode,
                GachaMissSlotPacket::decode, GachaMissSlotPacket::handle
        );
        CHANNEL.register(
                OpenTicketPaymentPacket.class, OpenTicketPaymentPacket::encode,
                OpenTicketPaymentPacket::decode, OpenTicketPaymentPacket::handle
        );
        CHANNEL.register(
                OpenNormalPaymentPacket.class, OpenNormalPaymentPacket::encode,
                OpenNormalPaymentPacket::decode, OpenNormalPaymentPacket::handle
        );
        CHANNEL.register(
                SaveTicketSettingsPacket.class, SaveTicketSettingsPacket::encode,
                SaveTicketSettingsPacket::decode, SaveTicketSettingsPacket::handle
        );
        CHANNEL.register(
                SaveWeightPacket.class, SaveWeightPacket::encode, SaveWeightPacket::decode, SaveWeightPacket::handle
        );
        CHANNEL.register(
                SyncPagePacket.class, SyncPagePacket::encode, SyncPagePacket::decode, SyncPagePacket::handle
        );
        CHANNEL.register(
                SaveGroupSettingsPacket.class, SaveGroupSettingsPacket::encode,
                SaveGroupSettingsPacket::decode, SaveGroupSettingsPacket::handle
        );
        CHANNEL.register(
                DeleteGroupPacket.class, DeleteGroupPacket::encode, DeleteGroupPacket::decode, DeleteGroupPacket::handle
        );
        CHANNEL.register(
                SelectGroupPacket.class, SelectGroupPacket::encode, SelectGroupPacket::decode, SelectGroupPacket::handle
        );
        CHANNEL.register(
                SyncGroupPacket.class, SyncGroupPacket::encode, SyncGroupPacket::decode, SyncGroupPacket::handle
        );
        CHANNEL.register(
                AddGroupPacket.class, AddGroupPacket::encode, AddGroupPacket::decode, AddGroupPacket::handle
        );
        CHANNEL.register(
                DuplicateGroupPacket.class, DuplicateGroupPacket::encode,
                DuplicateGroupPacket::decode, DuplicateGroupPacket::handle
        );
    }

    private ModPackets() {
    }
}