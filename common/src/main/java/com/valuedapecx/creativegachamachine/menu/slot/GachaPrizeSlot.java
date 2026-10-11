package com.valuedapecx.creativegachamachine.menu.slot;

import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GachaPrizeSlot extends Slot {

    private final GachaMachineMenu menu;
    private final int prizeSlot;

    public GachaPrizeSlot(GachaMachineMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);

        this.menu = menu;
        this.prizeSlot = slot;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !menu.isMissSlot(prizeSlot);
    }
}