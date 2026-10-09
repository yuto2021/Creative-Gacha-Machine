package com.valuedapecx.creativegachamachine.menu;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GachaTicketPaymentMenu extends AbstractContainerMenu {
    private final GachaMachineBlockEntity blockEntity;

    public GachaTicketPaymentMenu(int containerId, Inventory inventory) {
        super(ModMenus.GACHA_TICKET_PAYMENT.get(), containerId);

        this.blockEntity = null;
    }

    public GachaTicketPaymentMenu(int containerId, Inventory inventory, GachaMachineBlockEntity blockEntity) {
        super(ModMenus.GACHA_TICKET_PAYMENT.get(), containerId);

        this.blockEntity = blockEntity;
    }

    public String getTicketName() {
        if (blockEntity == null) {
            return "";
        }

        return blockEntity.getRequiredTicketName();
    }

    public int getTicketType() {
        if (blockEntity == null) {
            return 0;
        }

        return blockEntity.getRequiredTicketType();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    public GachaMachineBlockEntity getBlockEntity() {
        return blockEntity;
    }
}