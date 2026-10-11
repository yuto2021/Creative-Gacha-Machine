package com.valuedapecx.creativegachamachine.neoforge;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.registry.ModMenus;
import com.valuedapecx.creativegachamachine.screen.GachaMachineScreen;
import com.valuedapecx.creativegachamachine.screen.GachaPaymentScreen;
import com.valuedapecx.creativegachamachine.screen.GachaTicketPaymentScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
        modid = CreativeGachaMachine.MOD_ID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD
)
public final class CreativeGachaMachineNeoForgeClient {

    @SubscribeEvent
    public static void registerScreens(
            RegisterMenuScreensEvent event
    ) {

        event.register(
                ModMenus.GACHA_MACHINE.get(),
                GachaMachineScreen::new
        );

        event.register(
                ModMenus.GACHA_PAYMENT.get(),
                GachaPaymentScreen::new
        );

        event.register(
                ModMenus.GACHA_TICKET_PAYMENT.get(),
                GachaTicketPaymentScreen::new
        );
    }
}
