package com.valuedapecx.creativegachamachine.forge;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.client.renderer.GachaMachineRenderer;
import com.valuedapecx.creativegachamachine.registry.ModBlockEntities;
import com.valuedapecx.creativegachamachine.registry.ModBlocks;
import com.valuedapecx.creativegachamachine.registry.ModMenus;
import com.valuedapecx.creativegachamachine.screen.GachaMachineScreen;
import com.valuedapecx.creativegachamachine.screen.GachaPaymentScreen;
import com.valuedapecx.creativegachamachine.screen.GachaTicketPaymentScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = CreativeGachaMachine.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class CreativeGachaMachineForgeClient {
    @SubscribeEvent
    @SuppressWarnings("removal")
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(
                ModMenus.GACHA_MACHINE.get(),
                GachaMachineScreen::new
        );
        MenuScreens.register(
                ModMenus.GACHA_PAYMENT.get(),
                GachaPaymentScreen::new
        );
        MenuScreens.register(
                ModMenus.GACHA_TICKET_PAYMENT.get(),
                GachaTicketPaymentScreen::new
        );
        ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.GACHA_MACHINE.get(),
                RenderType.translucent()
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.GACHA_MACHINE.get(),
                GachaMachineRenderer::new
        );
    }
}
