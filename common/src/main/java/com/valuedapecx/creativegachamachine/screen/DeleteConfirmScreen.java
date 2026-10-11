package com.valuedapecx.creativegachamachine.screen;

import com.valuedapecx.creativegachamachine.platform.PlatformNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DeleteConfirmScreen extends Screen {
    private final int groupIndex;
    private final String groupName;
    private final Screen parent;

    public DeleteConfirmScreen(Screen parent, int groupIndex, String groupName) {
        super(Component.translatable("gui.gacha_group_library.delete_group"));

        this.parent = parent;
        this.groupIndex = groupIndex;
        this.groupName = groupName;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        addRenderableWidget(Button.builder(Component.translatable("gui.gacha_group_library.delete"),
                        button -> {
            PlatformNetworking.sendDeleteGroup(groupIndex);
            if (minecraft != null) {
                minecraft.setScreen(parent);
            }
        }).bounds(centerX - 90, centerY + 30, 80, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.gacha_group_library.cancel"),
                        button -> {
            if (minecraft != null) {
                minecraft.setScreen(parent);
            }
        }).bounds(centerX + 10, centerY + 30, 80, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(font, Component.translatable("gui.gacha_group_library.delete_confirm"),
                width / 2, height / 2 - 50, 0xFFFFFF);

        graphics.drawCenteredString(font, groupName, width / 2, height / 2 - 20, 0xFFFF55);

        graphics.drawCenteredString(font, Component.translatable("gui.gacha_group_library.delete_check"),
                width / 2, height / 2, 0xFFFFFF);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}