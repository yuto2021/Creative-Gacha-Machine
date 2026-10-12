package com.valuedapecx.creativegachamachine.screen;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.client.ClientPageCache;
import com.valuedapecx.creativegachamachine.data.GachaPage;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.platform.PageAction;
import com.valuedapecx.creativegachamachine.platform.PlatformNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class GachaMachineScreen extends AbstractContainerScreen<GachaMachineMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CreativeGachaMachine.MOD_ID,
            "textures/gui/gacha_machine.png"
    );

    private Button prevButton;
    private Button nextButton;
    private Button addButton;
    private Button removeButton;

    private Button libraryButton;

    private Component message;
    private int messageTicks;

    private boolean editingWeight;
    private int editingSlot = -1;
    private EditBox weightBox;
    private Button saveWeightButton;

    public GachaMachineScreen(GachaMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        imageWidth = 184;
        imageHeight = 252;

        titleLabelX = 8;
        titleLabelY = 6;

        inventoryLabelX = 8;
        inventoryLabelY = 153;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 4, 4, imageWidth, imageHeight, imageWidth, imageHeight);

        for (int slotIndex = 0; slotIndex < 54; slotIndex++) {
            int slotX = x + 8 + (slotIndex % 9) * 18;
            int slotY = y + 43 + (slotIndex / 9) * 18;

            if (!menu.isMissSlot(slotIndex)) {
                continue;
            }

            guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x88FF0000);
        }
    }

    @Override
    protected void init() {
        super.init();

        int x = leftPos;
        int y = topPos;

        removeButton = addRenderableWidget(Button.builder(Component.literal("-"),
                button -> {
            System.out.println("REMOVE CLICK");
            PlatformNetworking.sendGachaPageAction(
                    PageAction.REMOVE
            );
        }).bounds(x + 8, y + 18, 20, 20).build());

        prevButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            PlatformNetworking.sendGachaPageAction(
                    PageAction.PREVIOUS
            );
        }).bounds(x + 40, y + 18, 20, 20).build());

        nextButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            PlatformNetworking.sendGachaPageAction(
                    PageAction.NEXT
            );
        }).bounds(x + 120, y + 18, 20, 20).build());

        addButton = addRenderableWidget(Button.builder(Component.literal("+"), button -> {
            System.out.println("ADD CLICK");
            PlatformNetworking.sendGachaPageAction(
                    PageAction.ADD
            );
        }).bounds(x + 120, y + 18, 20, 20).build());

        weightBox = new EditBox(font, leftPos + 60, topPos + 35, 60, 18, Component.empty());

        weightBox.setValue("1.0");

        weightBox.visible = false;

        addRenderableWidget(weightBox);

        saveWeightButton = Button.builder(Component.literal("gui.creative_gacha_machine.save"), button -> {
            try {
                float weight = Float.parseFloat(weightBox.getValue());
                PlatformNetworking.sendSaveWeight(
                        menu.getCurrentPage(),
                        editingSlot,
                        weight
                );;

                editingWeight = false;
                weightBox.visible = false;
                saveWeightButton.visible = false;
            } catch (NumberFormatException ignored) {

            }
        }).bounds(leftPos + 60, topPos + 60, 60, 20).build();

        saveWeightButton.visible = false;

        addRenderableWidget(saveWeightButton);

        libraryButton = addRenderableWidget(Button.builder(Component.literal("L"), button -> {
            if (minecraft != null) {
                minecraft.setScreen(new LibraryScreen());
            }
        }).bounds(leftPos + 148, topPos + 18, 20, 20).build());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        guiGraphics.drawString(font, Component.translatable(
                "gui.creative_gacha_machine.page", menu.getPage(), menu.getMaxPage()
        ), 66, 24, 0x404040, false);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (messageTicks > 0) {
            messageTicks--;
        }

        boolean lastPage = menu.getPage() == menu.getMaxPage();

        addButton.visible = lastPage;
        nextButton.visible = !lastPage;
        nextButton.active = !lastPage;
        prevButton.active = menu.getPage() > 1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // ページボタン系ツールチップ
        if (removeButton.isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "tooltip.creative_gacha_machine.remove_page"
                    ),
                    mouseX,
                    mouseY
            );
        }
        else if (prevButton.isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "tooltip.creative_gacha_machine.previous_page"
                    ),
                    mouseX,
                    mouseY
            );
        }
        else if (nextButton.isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "tooltip.creative_gacha_machine.next_page"
                    ),
                    mouseX,
                    mouseY
            );
        }
        else if (addButton.isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "tooltip.creative_gacha_machine.add_page"
                    ),
                    mouseX,
                    mouseY
            );
        }

        if (messageTicks > 0 && message != null) guiGraphics.renderTooltip(
                font, message, removeButton.getX(), removeButton.getY() - 15
        );

        boolean renderedCustomTooltip = false;

        for (int slotIndex = 0; slotIndex < 54; slotIndex++) {
            int slotX = leftPos + 8 + (slotIndex % 9) * 18;
            int slotY = topPos + 43 + (slotIndex / 9) * 18;

            boolean hovered = (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16);

            if (!hovered) {
                continue;
            }

            if (editingWeight) {
                renderedCustomTooltip = true;
                break;
            }

            Slot slot = menu.slots.get(slotIndex);
            GachaPage page = ClientPageCache.getPage(menu.getCurrentPage());

            System.out.println(
                    "TOOLTIP PAGE=" + menu.getCurrentPage()
                            + " SLOT=" + slotIndex
                            + " CACHE="
                            + (page == null
                            ? "NULL"
                            : page.getWeight(slotIndex))
            );

            float weight = page != null ? page.getWeight(slotIndex) : 1.0F;

            if (menu.getCarried().isEmpty() && !slot.hasItem()) {
                if (menu.isMissSlot(slotIndex)){
                    guiGraphics.renderComponentTooltip(font, List.of(
                            Component.translatable("tooltip.creative_gacha_machine.miss"),
                            Component.translatable("tooltip.creative_gacha_machine.weight", weight),
                            Component.translatable("tooltip.creative_gacha_machine.edit_weight"),
                            Component.translatable("tooltip.creative_gacha_machine.enable_slot")
                    ), mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(font, Component.translatable("tooltip.creative_gacha_machine.disable_slot"), mouseX, mouseY);
                }
                renderedCustomTooltip = true;
            } else if (slot.hasItem()) {
                if (minecraft == null || minecraft.player == null) {
                    break;
                }

                List<Component> tooltip =
                        new ArrayList<>(
                                Screen.getTooltipFromItem(
                                        minecraft,
                                        slot.getItem()
                                )
                        );

                tooltip.add(Component.empty());

                tooltip.add(
                        Component.translatable(
                                "tooltip.creative_gacha_machine.weight",
                                weight
                        )
                );

                tooltip.add(
                        Component.translatable(
                                "tooltip.creative_gacha_machine.edit_item_weight"
                        )
                );

                guiGraphics.renderTooltip(
                        font,
                        tooltip,
                        slot.getItem().getTooltipImage(),
                        mouseX,
                        mouseY
                );

                renderedCustomTooltip = true;
            }

            break;
        }

        if (editingWeight) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0,0,300);

            guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0x88000000);
            guiGraphics.fill(leftPos + 40, topPos + 20, leftPos + 140, topPos + 90, 0xFF202020);

            guiGraphics.drawString(font, Component.translatable("gui.creative_gacha_machine.weight"),
                    leftPos + 65, topPos + 25, 0xFFFFFF);

            weightBox.visible = true;
            saveWeightButton.visible = true;

            weightBox.render(guiGraphics, mouseX, mouseY, partialTick);

            saveWeightButton.render(guiGraphics, mouseX, mouseY, partialTick);

            guiGraphics.pose().popPose();
        } else {
            weightBox.visible = false;
            saveWeightButton.visible = false;
        }

        if (!renderedCustomTooltip) {
            renderTooltip(
                    guiGraphics,
                    mouseX,
                    mouseY
            );
        }
    }

    public void showMessage(Component component) {
        this.message = component;
        this.messageTicks = 60; // 3秒
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (editingWeight) {
            return weightBox.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            ) || saveWeightButton.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        System.out.println("BUTTON = " + button);

        Slot slot = hoveredSlot;
        if (slot != null && slot.index < 54 && menu.getCarried().isEmpty() && !editingWeight) { // 右クリック

            if (!slot.hasItem() && button == 1) {
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                PlatformNetworking.sendGachaMissSlot(
                        slot.index
                );

                return true;
            }
            if ((slot.hasItem() && button == 1) || (menu.isMissSlot(slot.index) && button == 0)) {
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                editingWeight = true;
                editingSlot = slot.index;

                GachaPage page =
                        ClientPageCache.getPage(
                                menu.getCurrentPage()
                        );

                if (page != null) {
                    weightBox.setValue(
                            String.valueOf(
                                    page.getWeight(slot.index)
                            )
                    );
                }

                weightBox.setFocused(true);
                setFocused(weightBox);

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (editingWeight) {

            if (keyCode == 257 || keyCode == 335) {
                confirmWeight();
                return true;
            }

            if (keyCode == 256) {
                return true;
            }

            if (weightBox.isFocused()) {
                return weightBox.keyPressed(
                        keyCode,
                        scanCode,
                        modifiers
                );
            }

            return true;
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (editingWeight) {
            return weightBox.charTyped(codePoint, modifiers);
        }

        return super.charTyped(codePoint, modifiers);
    }

    private void confirmWeight() {
        try {
            float weight = Float.parseFloat(weightBox.getValue());

            PlatformNetworking.sendSaveWeight(
                    menu.getCurrentPage(),
                    editingSlot,
                    weight
            );

            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            editingWeight = false;
        } catch (NumberFormatException ignored) {

        }
    }
}