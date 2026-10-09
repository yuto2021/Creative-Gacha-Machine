package com.valuedapecx.creativegachamachine.fabric.client;

import com.valuedapecx.creativegachamachine.registry.ModMenus;
import com.valuedapecx.creativegachamachine.screen.GachaMachineScreen;
import com.valuedapecx.creativegachamachine.screen.GachaPaymentScreen;
import com.valuedapecx.creativegachamachine.screen.GachaTicketPaymentScreen;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public final class CreativeGachaMachineFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(
                ModMenus.GACHA_MACHINE.get(),
                GachaMachineScreen::new
        );
        MenuScreens.register(
                ModMenus.GACHA_PAYMENT.get(),
                GachaPaymentScreen::new
        );
        MenuRegistry.registerScreenFactory(
                ModMenus.GACHA_TICKET_PAYMENT.get(),
                GachaTicketPaymentScreen::new
        );
    }
}
