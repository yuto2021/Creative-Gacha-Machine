package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.network.*;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public final class ModPackets {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(ResourceLocation.fromNamespaceAndPath(
            CreativeGachaMachine.MOD_ID, "main"
    ));

    public static final ResourceLocation OPEN_TICKET_PAYMENT = ResourceLocation.fromNamespaceAndPath(
            CreativeGachaMachine.MOD_ID,
            "open_ticket_payment"
    );

    private static boolean initialized = false;

    public static void init() {
        System.out.println("ModPackets.init CALLED");

    }

    private ModPackets() {
    }
}