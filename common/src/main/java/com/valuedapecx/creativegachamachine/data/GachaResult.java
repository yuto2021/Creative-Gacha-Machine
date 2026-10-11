package com.valuedapecx.creativegachamachine.data;

import net.minecraft.world.item.ItemStack;

public record GachaResult(ItemStack reward, boolean miss, int totalSlot) {
    public static GachaResult MISS(int totalSlot) {
        return new GachaResult(ItemStack.EMPTY, true, totalSlot);
    }

    public static GachaResult REWARD(ItemStack reward, int totalSlot) {
        return new GachaResult(reward, false, totalSlot);
    }

    public static GachaResult NONE(int totalSlot) {
        return new GachaResult(ItemStack.EMPTY, false, totalSlot);
    }
}