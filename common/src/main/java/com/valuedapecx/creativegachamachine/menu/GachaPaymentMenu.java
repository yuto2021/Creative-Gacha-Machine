package com.valuedapecx.creativegachamachine.menu;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.container.PaymentContainer;
import com.valuedapecx.creativegachamachine.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GachaPaymentMenu extends AbstractContainerMenu {
    private final Container container;
    private final GachaMachineBlockEntity blockEntity;

    public GachaPaymentMenu(int containerId, Inventory inventory) {
        super(ModMenus.GACHA_PAYMENT.get(), containerId);

        this.container = new SimpleContainer(1);
        this.blockEntity = null;

        addSlot(new Slot(container, 0, 80, 40));

        addPlayerInventory(inventory);
    }

    public GachaPaymentMenu(int containerId, Inventory inventory, GachaMachineBlockEntity blockEntity) {
        super(ModMenus.GACHA_PAYMENT.get(), containerId);

        this.container = new PaymentContainer(blockEntity);
        this.blockEntity = blockEntity;

        addSlot(new Slot(container, 0, 80, 40));

        addPlayerInventory(inventory);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result;
        Slot slot = slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();

        result = stack.copy();

        // PAYMENT → プレイヤー
        if (slotIndex == 0) {
            if (!moveItemStackTo(stack, 1, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }

        // プレイヤー → PAYMENT
        else {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void addPlayerInventory(Inventory inventory) {
        // プレイヤーインベントリ
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // ホットバー
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }
    public GachaMachineBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
