package com.valuedapecx.creativegachamachine.screen;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.menu.GachaTicketPaymentMenu;

import com.valuedapecx.creativegachamachine.platform.PlatformNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GachaTicketPaymentScreen extends AbstractContainerScreen<GachaTicketPaymentMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CreativeGachaMachine.MOD_ID,
            "textures/gui/ticket_payment.png"
    );

    private EditBox ticketNameBox;
    private EditBox ticketTypeBox;

    private Button normalPaymentButton;

    public GachaTicketPaymentScreen(GachaTicketPaymentMenu menu,
            Inventory inventory,
            Component title
    ) {

        super(
                menu,
                inventory,
                title
        );

        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {

        super.init();

        ticketNameBox =
                new EditBox(
                        font,
                        leftPos + 34,
                        topPos + 45,
                        110,
                        18,
                        Component.empty()
                );

        ticketNameBox.setMaxLength(
                64
        );

        ticketNameBox.setValue(
                menu.getTicketName()
        );

        addRenderableWidget(
                ticketNameBox
        );

        ticketTypeBox =
                new EditBox(
                        font,
                        leftPos + 34,
                        topPos + 82,
                        50,
                        18,
                        Component.empty()
                );

        ticketTypeBox.setMaxLength(
                6
        );

        ticketTypeBox.setValue(
                String.valueOf(
                        menu.getTicketType()
                )
        );

        addRenderableWidget(
                ticketTypeBox
        );

        normalPaymentButton =
                Button.builder(

                                Component.translatable(
                                        "gui.creative_gacha_machine.normal_payment"
                                ),

                                button -> {

                                    // サーバーへパケット送信
                                    // createPaymentMenu() を開く
                                    PlatformNetworking.sendOpenNormalPayment();

                                }

                        )

                        .bounds(
                                leftPos + 92,
                                topPos + 120,
                                75,
                                20
                        )

                        .build();

        addRenderableWidget(
                normalPaymentButton
        );
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
                        "gui.creative_gacha_machine.ticket_payment"
                ),
                8,
                6,
                0x404040,
                false
        );

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.creative_gacha_machine.ticket_name"
                ),
                34,
                33,
                0x404040,
                false
        );

        graphics.drawString(
                font,
                Component.translatable(
                        "gui.creative_gacha_machine.ticket_type"
                ),
                34,
                70,
                0x404040,
                false
        );
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {

        int x =
                (width - imageWidth) / 2;

        int y =
                (height - imageHeight) / 2;

        graphics.blit(
                TEXTURE,
                x,
                y,
                0,
                0,
                imageWidth,
                imageHeight
        );
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

        renderTooltip(
                graphics,
                mouseX,
                mouseY
        );
    }

    @Override
    public void onClose() {
        try {
            System.out.println(
                    "SCREEN CLOSED"
            );

            PlatformNetworking.sendSaveTicketSettings(
                            ticketNameBox.getValue(),
                            Integer.parseInt(
                                    ticketTypeBox.getValue()
                            )
                    );

        } catch (
                NumberFormatException ignored
        ) {
        }

        super.onClose();
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        // Enter
        if (keyCode == 257
                || keyCode == 335) {

            saveSettings();

            return true;
        }

        // ESC
        if (keyCode == 256) {

            saveSettings();

            onClose();

            return true;
        }

        if (ticketNameBox != null
                && ticketNameBox.isFocused()) {

            return ticketNameBox.keyPressed(
                    keyCode,
                    scanCode,
                    modifiers
            );
        }

        if (ticketTypeBox != null
                && ticketTypeBox.isFocused()) {

            return ticketTypeBox.keyPressed(
                    keyCode,
                    scanCode,
                    modifiers
            );
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }

    private void saveSettings() {
        try {
            PlatformNetworking.sendSaveTicketSettings(
                            ticketNameBox.getValue(),
                            Integer.parseInt(
                                    ticketTypeBox.getValue()
                            )
                    );

        } catch (NumberFormatException ignored) {
        }
    }
}
