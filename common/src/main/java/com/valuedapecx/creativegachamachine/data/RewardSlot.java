package com.valuedapecx.creativegachamachine.data;

import net.minecraft.world.item.ItemStack;

public class RewardSlot {
    private ItemStack stack = ItemStack.EMPTY;

    private float weight = 1.0F;

    private boolean miss;

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = Math.max(0.0001F, weight);
    }

    public boolean isMiss() {
        return miss;
    }

    public void setMiss(boolean miss) {
        this.miss = miss;
    }
}
