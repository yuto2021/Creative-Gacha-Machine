package com.valuedapecx.creativegachamachine.fabric.client;

import com.valuedapecx.creativegachamachine.client.renderer.GachaMachineRenderer;
import com.valuedapecx.creativegachamachine.registry.ModBlockEntities;
import com.valuedapecx.creativegachamachine.registry.ModBlocks;
import com.valuedapecx.creativegachamachine.registry.ModMenus;
import com.valuedapecx.creativegachamachine.screen.GachaMachineScreen;
import com.valuedapecx.creativegachamachine.screen.GachaPaymentScreen;
import com.valuedapecx.creativegachamachine.screen.GachaTicketPaymentScreen;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

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
        BlockRenderLayerMap.INSTANCE.putBlock(
                ModBlocks.GACHA_MACHINE.get(),
                RenderType.translucent()
        );
        BlockEntityRenderers.register(
                ModBlockEntities.GACHA_MACHINE.get(),
                GachaMachineRenderer::new
        );
    }
}
