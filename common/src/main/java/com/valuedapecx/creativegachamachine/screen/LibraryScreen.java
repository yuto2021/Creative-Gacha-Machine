package com.valuedapecx.creativegachamachine.screen;

import com.valuedapecx.creativegachamachine.client.ClientGroupCache;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.platform.PlatformNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.List;

public class LibraryScreen extends Screen {

    private int scrollOffset;

    private static final int VISIBLE_COUNT = 8;

    private final List<GachaGroup> groups;

    private int contextMenuIndex = -1;

    private int contextMenuX;
    private int contextMenuY;

    public LibraryScreen() {

        super(
                Component.translatable("gui.gacha_group_library")
        );

        this.groups =
                ClientGroupCache.getGroups();
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double scrollX,
            double scrollY
    ) {
        if (scrollY > 0) {
            scrollOffset--;
        }

        if (scrollY < 0) {
            scrollOffset++;
        }

        scrollOffset =
                Mth.clamp(
                        scrollOffset,
                        0,
                        Math.max(
                                0,
                                (groups.size() + 1) - VISIBLE_COUNT
                        )
                );

        return true;
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
                Component.translatable("gui.gacha_group_library"),
                width / 2,
                20,
                0xFFFFFF
        );

        int y = 50;

        int totalEntries = groups.size() + 1;

        for (int i = 0; i < VISIBLE_COUNT; i++) {
            int index = scrollOffset + i;

            if (index >= totalEntries) {
                break;
            }

            int x = width / 2 - 100;
            int boxY = y + i * 24;

            if (index == groups.size()) {

                graphics.fill(
                        x,
                        boxY,
                        x + 200,
                        boxY + 20,
                        0xAA006600
                );

                graphics.drawCenteredString(
                        font,
                        Component.literal("+ ").append(Component.translatable(
                                "gui.gacha_group_library.add_count"
                        )),
                        width / 2,
                        boxY + 6,
                        0xFFFFFF
                );

                continue;
            }

            GachaGroup group = groups.get(index);

            graphics.fill(
                    x,
                    boxY,
                    x + 200,
                    boxY + 20,
                    0xAA202020
            );

            graphics.drawString(
                    font,
                    group.getName(),
                    x + 5,
                    boxY + 6,
                    0xFFFFFF
            );

            graphics.drawString(
                    font,
                    Component.translatable("gui.gacha_group_library.roll_total", group.getRollCount()),
                    x + 140,
                    boxY + 6,
                    0xAAAAAA
            );
        }

        int visibleGroups = Math.min(groups.size(), VISIBLE_COUNT);

        if (groups.size() + 1 > VISIBLE_COUNT) {

            int barX = width / 2 + 110;

            int barY = 50;

            int barHeight = VISIBLE_COUNT * 24;

            graphics.fill(
                    barX,
                    barY,
                    barX + 6,
                    barY + barHeight,
                    0xFF303030
            );

            int knobHeight =
                    Math.max(
                            12,
                            barHeight * VISIBLE_COUNT / totalEntries
                    );

            int maxScroll =
                    totalEntries - VISIBLE_COUNT;

            int knobY =
                    barY +
                            (
                                    maxScroll == 0
                                            ? 0
                                            : (barHeight - knobHeight)
                                            * scrollOffset
                                            / maxScroll
                            );

            graphics.fill(
                    barX,
                    knobY,
                    barX + 6,
                    knobY + knobHeight,
                    0xFFFFFFFF
            );
        }

        if (contextMenuIndex >= 0 && contextMenuIndex < groups.size()) {
            // 外枠
            graphics.fill(
                    contextMenuX - 1,
                    contextMenuY - 1,
                    contextMenuX + 81,
                    contextMenuY + 61,
                    0xFFFFFFFF
            );

            // 背景
            graphics.fill(
                    contextMenuX,
                    contextMenuY,
                    contextMenuX + 80,
                    contextMenuY + 60,
                    0xFF202020
            );

            graphics.fill(
                    contextMenuX,
                    contextMenuY,
                    contextMenuX + 80,
                    contextMenuY + 60,
                    0xFF202020
            );

            graphics.hLine(
                    contextMenuX,
                    contextMenuX + 79,
                    contextMenuY + 20,
                    0xFF555555
            );

            graphics.hLine(
                    contextMenuX,
                    contextMenuX + 79,
                    contextMenuY + 40,
                    0xFF555555
            );

            graphics.drawString(
                    font,
                    Component.translatable("gui.gacha_group_library.edit"),
                    contextMenuX + 5,
                    contextMenuY + 5,
                    0xFFFFFF
            );

            graphics.drawString(
                    font,
                    Component.translatable("gui.gacha_group_library.duplicate"),
                    contextMenuX + 5,
                    contextMenuY + 25,
                    0xFFFFFF
            );

            graphics.drawString(
                    font,
                    Component.translatable("gui.gacha_group_library.delete"),
                    contextMenuX + 5,
                    contextMenuY + 45,
                    0xFFFFFF
            );
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (contextMenuIndex >= 0) {
            if (
                    mouseX >= contextMenuX
                            &&
                            mouseX <= contextMenuX + 80
            ) {

                // Edit
                if (
                        mouseY >= contextMenuY
                                &&
                                mouseY <= contextMenuY + 20
                ) {

                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                        minecraft.setScreen(
                                new GroupEditScreen(
                                        contextMenuIndex,
                                        groups.get(
                                                contextMenuIndex
                                        )
                                )
                        );
                    }

                    return true;
                }

                // Duplicate
                if (
                        mouseY >= contextMenuY + 20
                                &&
                                mouseY <= contextMenuY + 40
                ) {

                    if (
                            contextMenuIndex >= 0
                                    &&
                                    contextMenuIndex < groups.size()
                    ) {

                        PlatformNetworking.sendDuplicateGroup(
                                contextMenuIndex
                        );
                    }

                    contextMenuIndex = -1;

                    return true;
                }

                // Delete
                if (
                        mouseY >= contextMenuY + 40
                                &&
                                mouseY <= contextMenuY + 60
                ) {

                    if (
                            groups.size() <= 1
                    ) {

                        contextMenuIndex = -1;

                        if (minecraft != null) {
                            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                            minecraft.setScreen(
                                    new MessageScreen(
                                            this,
                                            Component.translatable("message.creative_gacha_machine.cannot_delete_last_group")
                                    )
                            );
                        }

                        return true;
                    }

                    if (
                            contextMenuIndex < 0
                                    ||
                                    contextMenuIndex >= groups.size()
                    ) {

                        contextMenuIndex = -1;
                        return true;
                    }

                    int deleteIndex =
                            contextMenuIndex;

                    String deleteName =
                            groups.get(deleteIndex)
                                    .getName();

                    contextMenuIndex = -1;

                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                        minecraft.setScreen(
                                new DeleteConfirmScreen(
                                        this,
                                        deleteIndex,
                                        deleteName
                                )
                        );
                    }

                    return true;
                }
            }

            contextMenuIndex = -1;

            return true;
        } else {
            int startY = 50;

            for (
                    int i = 0;
                    i < VISIBLE_COUNT;
                    i++
            ) {

                int index = scrollOffset + i;

                int totalEntries = groups.size() + 1;

                if (index >= totalEntries) {
                    break;
                }

                int x = width / 2 - 100;

                int y = startY + i * 24;

                if (
                        mouseX >= x
                                &&
                                mouseX <= x + 200
                                &&
                                mouseY >= y
                                &&
                                mouseY <= y + 20
                ) {
                    if (index == groups.size()) {
                        if (button == 0) {
                            System.out.println(
                                    "clicked add group button"
                            );

                            PlatformNetworking.sendAddGroup();
                            return true;
                        }

                        return true;
                    }

                    GachaGroup group =
                            groups.get(index);

                    if (button == 0) {
                        System.out.println(
                                "clicked index=" + index
                        );

                        if (minecraft != null) {
                            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        }

                        PlatformNetworking.sendSelectGroup(
                                index
                        );

                        onClose();

                        return true;
                    }

                    if (button == 1) {
                        System.out.println(
                                "right-clicked index=" + index
                        );

                        if (minecraft != null) {
                            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        }

                        contextMenuIndex = index;

                        contextMenuX = (int) mouseX;
                        contextMenuY = (int) mouseY;

                        return true;
                    }

                    return true;
                }
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public void onClose() {

        if (minecraft != null) {

            minecraft.setScreen(
                    null
            );
        }
    }
}