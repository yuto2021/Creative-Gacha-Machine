package com.valuedapecx.creativegachamachine.screen;

import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.platform.PlatformNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GroupEditScreen extends Screen {

    private final int groupIndex;
    private final GachaGroup group;

    private final String backupName;
    private final int backupRollCount;

    private EditBox nameBox;
    private EditBox rollCountBox;

    public GroupEditScreen(
            int groupIndex,
            GachaGroup group
    ) {
        super(
                Component.translatable(
                        "gui.gacha_group_library.group_check"
                )
        );

        this.groupIndex = groupIndex;
        this.group = group;

        this.backupName =
                group.getName();

        this.backupRollCount =
                group.getRollCount();
    }

    @Override
    protected void init() {

        int centerX =
                width / 2;

        int centerY =
                height / 2;

        nameBox =
                new EditBox(
                        font,
                        centerX - 80,
                        centerY - 40,
                        160,
                        20,
                        Component.empty()
                );

        nameBox.setValue(
                group.getName()
        );

        addRenderableWidget(
                nameBox
        );

        rollCountBox =
                new EditBox(
                        font,
                        centerX - 80,
                        centerY,
                        60,
                        20,
                        Component.empty()
                );

        rollCountBox.setValue(
                String.valueOf(
                        group.getRollCount()
                )
        );

        addRenderableWidget(
                rollCountBox
        );

        addRenderableWidget(
                Button.builder(
                                Component.translatable(
                                        "gui.gacha_group_library.ok"
                                ),
                                button -> save()
                        )
                        .bounds(
                                centerX - 80,
                                centerY + 40,
                                60,
                                20
                        )
                        .build()
        );

        addRenderableWidget(
                Button.builder(
                                Component.translatable(
                                        "gui.gacha_group_library.cancel"
                                ),
                                button -> cancel()
                        )
                        .bounds(
                                centerX + 20,
                                centerY + 40,
                                60,
                                20
                        )
                        .build()
        );
    }

    private void save() {

        try {

            int rollCount =
                    Integer.parseInt(
                            rollCountBox.getValue()
                    );

            group.setName(
                    nameBox.getValue()
            );

            group.setRollCount(
                    rollCount
            );

            PlatformNetworking.sendSaveGroupSettings(
                    groupIndex,
                    group.getName(),
                    rollCount
            );

            onClose();

        } catch (
                NumberFormatException ignored
        ) {
        }
    }

    private void cancel() {

        group.setName(
                backupName
        );

        group.setRollCount(
                backupRollCount
        );

        onClose();
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

        graphics.drawCenteredString(
                font,
                Component.translatable("gui.gacha_group_library.group_check"),
                width / 2,
                height / 2 - 70,
                0xFFFFFF
        );

        graphics.drawString(
                font,
                Component.translatable("gui.gacha_group_library.name"),
                width / 2 - 80,
                height / 2 - 55,
                0xFFFFFF
        );

        graphics.drawString(
                font,
                Component.translatable("gui.gacha_group_library.roll_count"),
                width / 2 - 80,
                height / 2 - 15,
                0xFFFFFF
        );
    }
}