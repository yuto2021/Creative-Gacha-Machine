package com.valuedapecx.creativegachamachine.screen;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.menu.GachaPaymentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class GachaPaymentScreen
        extends AbstractContainerScreen<
                GachaPaymentMenu> {

    private static final ResourceLocation
            TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    CreativeGachaMachine.MOD_ID,
                    "textures/gui/payment.png"
            );

    private Button normalPaymentButton;

    public GachaPaymentScreen(GachaPaymentMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        imageWidth = 184;
        imageHeight = 170;
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.creative_gacha_machine.payment"
                ),
                8,
                6,
                0x404040,
                false
        );
        graphics.drawString(
                font,
                Component.translatable(
                        "container.inventory"
                ),
                8,
                73,
                0x404040,
                false
        );
        graphics.drawString(
                font,
                Component.translatable(
                        "gui.creative_gacha_machine.consumed_per_roll"
                ),
                40,
                60,
                0x808080,
                false
        );
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {

        int x =
                (width - imageWidth) / 2;

        int y =
                (height - imageHeight) / 2;

        guiGraphics.blit(
                TEXTURE,
                x,
                y,
                4,
                4,
                imageWidth,
                imageHeight,
                imageWidth,
                imageHeight
        );
        guiGraphics.fill(
                x + 78,
                y + 38,
                x + 98,
                y + 58,
                0x55FFFF00
        );
    }

    @Override
    protected void init() {
        super.init();

        /*
        Button ticketButton =
                Button.builder(
                        Component.translatable(
                                "gui.creative_gacha_machine.ticket_mode"
                        ),
                        button -> {
                            // パケット送信
                            // Ticket GUIへ切替
                            PlatformNetworking.sendOpenTicketPayment();
                        }
                )
                .bounds(
                    leftPos + 120,
                    topPos + 16,
                    55,
                    20
                )
                .build();
        addRenderableWidget(
                ticketButton
        );
         */
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        Slot slot =
                hoveredSlot;

        if (slot != null
                && slot.hasItem()) {

            graphics.renderTooltip(
                    font,
                    slot.getItem(),
                    mouseX,
                    mouseY
            );
        }
    }
}