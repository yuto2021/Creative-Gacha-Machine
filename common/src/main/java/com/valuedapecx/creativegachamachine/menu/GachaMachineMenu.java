package com.valuedapecx.creativegachamachine.menu;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.menu.slot.GachaPrizeSlot;
import com.valuedapecx.creativegachamachine.network.SyncGroupPacket;
import com.valuedapecx.creativegachamachine.platform.PlatformNetworking;
import com.valuedapecx.creativegachamachine.registry.ModMenus;
import com.valuedapecx.creativegachamachine.registry.ModPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GachaMachineMenu
        extends AbstractContainerMenu {

    private final GachaMachineBlockEntity blockEntity;
    private final ContainerData data;

    /*
    * Client
    */
    public GachaMachineMenu(
            int containerId,
            Inventory inventory
    ) {
        super(ModMenus.GACHA_MACHINE.get(), containerId);

        System.out.println("CLIENT MENU");

        this.blockEntity = null;

        this.data = new SimpleContainerData(56);

        addDataSlots(data);

        addMenuSlots(new SimpleContainer(54), inventory);
    }

    /*
     * SERVER
     */
    public GachaMachineMenu(int containerId, Inventory inventory, GachaMachineBlockEntity blockEntity) {
        super(ModMenus.GACHA_MACHINE.get(), containerId);

        System.out.println("SERVER MENU");

        this.blockEntity = blockEntity;
        this.data = new SimpleContainerData(56);

        addDataSlots(data);
        addMenuSlots(blockEntity, inventory);

        Objects.requireNonNull(inventory.player.level().getServer()).execute(() -> {
                    if (inventory.player instanceof ServerPlayer player) {
                        PlatformNetworking.syncGroups(player, blockEntity.getGroups(), player.serverLevel().registryAccess());
                    }
        });
    }

    private void addMenuSlots(Container container, Inventory inventory) {
        // 景品54枠
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col;

                addSlot(new GachaPrizeSlot(this, container, slotIndex, 8 + col * 18, 43 + row * 18));
            }
        }

        // inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new GachaPrizeSlot(this, inventory, col + row * 9 + 9, 8 + col * 18, 165 + row * 18));
            }
        }

        // hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new GachaPrizeSlot(this, inventory, col, 8 + col * 18, 223));
        }

        System.out.println("slot count=" + slots.size());
    }

    public GachaMachineBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack result;

        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();

        result = stack.copy();

        // プレイヤー→景品
        if (index >= 54) {
            if (!moveItemStackTo(stack, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
        }

        // 景品→プレイヤー
        else {
            if (!moveItemStackTo(stack, 54, slots.size(), true)) {
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
        return blockEntity == null || blockEntity.stillValid(player);
    }

    public int getPage() {
        return data.get(54) + 1;
    }

    public int getMaxPage() {
        return Math.max(1, data.get(55));
    }

    @Override
    public void broadcastChanges() {
        if (blockEntity != null) {
            for (int slot = 0; slot < 54; slot++) {
                data.set(slot, blockEntity.isMissSlot(slot) ? 1 : 0);
            }

            data.set(54, blockEntity.getCurrentPage());
            data.set(55, blockEntity.getPageCount());
        }

        super.broadcastChanges();
    }

    public boolean isMissSlot(int slot) {
        if (slot < 0 || slot >= 54) {
            return false;
        }

        return data.get(slot) == 1;
    }

    public int getCurrentPage() {
        if (blockEntity == null) {
            return 0;
        }

        return blockEntity.getCurrentPage();
    }

}