package com.valuedapecx.creativegachamachine.neoforge.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class AddGroupPayloadHandler {

    private AddGroupPayloadHandler() {
    }

    public static void handle(
            AddGroupPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            System.out.println(
                    "AddGroupPayload RECEIVED"
            );
        });
    }
}