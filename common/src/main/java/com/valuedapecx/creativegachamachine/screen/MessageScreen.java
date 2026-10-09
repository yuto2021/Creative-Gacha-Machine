package com.valuedapecx.creativegachamachine.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MessageScreen extends Screen {

    private final Screen parent;

    private final Component message;

    public MessageScreen(
            Screen parent,
            Component message
    ) {

        super(Component.literal("Message"));

        this.parent = parent;
        this.message = message;
    }

    @Override
    protected void init() {

        addRenderableWidget(
                Button.builder(
                                Component.literal("OK"),
                                button -> {

                                    if (minecraft != null) {

                                        minecraft.setScreen(
                                                parent
                                        );
                                    }
                                }
                        )
                        .bounds(
                                width / 2 - 40,
                                height / 2 + 20,
                                80,
                                20
                        )
                        .build()
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        renderBackground(graphics);

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        graphics.drawCenteredString(
                font,
                message,
                width / 2,
                height / 2 - 10,
                0xFFFFFF
        );
    }
}