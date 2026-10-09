package com.valuedapecx.creativegachamachine.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class GachaPage {
    private final RewardSlot[] slots = new RewardSlot[54];

    public GachaPage() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new RewardSlot();
        }
    }

    public ItemStack getItem(int slot) {
        return slots[slot].getStack();
    }

    public void setItem(int slot, ItemStack stack) {
        slots[slot].setStack(stack);
    }

    public float getWeight(int slot) {
        return slots[slot].getWeight();
    }

    public void setWeight(int slot, float weight) {
        slots[slot].setWeight(weight);
    }

    public boolean isMissSlot(int slot) {
        return slots[slot].isMiss();
    }

    public void toggleMissSlot(int slot) {
        slots[slot].setMiss(!slots[slot].isMiss());
    }

    public boolean isEmpty() {
        for (RewardSlot slot : slots) {
            if (!slot.getStack().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public CompoundTag save() {
        for (int i = 0; i < 54; i++) {
            if (!slots[i].getStack().isEmpty()) {
                System.out.println("SAVE SLOT " + i + " = " + slots[i].getStack());
            }
        }

        CompoundTag tag = new CompoundTag();
        ListTag slotsTag = new ListTag();

        for (RewardSlot slot : slots) {
            CompoundTag slotTag = new CompoundTag();

            slotTag.put("Item", slot.getStack().save(new CompoundTag()));
            slotTag.putFloat("Weight", slot.getWeight());
            slotTag.putBoolean("Miss", slot.isMiss());
            slotsTag.add(slotTag);
        }

        tag.put("Slots", slotsTag);

        return tag;
    }

    public static GachaPage load(CompoundTag tag) {
        GachaPage page = new GachaPage();

        ListTag slotsTag = tag.getList("Slots", Tag.TAG_COMPOUND);

        for (int i = 0; i < Math.min(54, slotsTag.size()); i++) {
            CompoundTag slotTag = slotsTag.getCompound(i);

            page.slots[i].setStack(ItemStack.of(slotTag.getCompound("Item")));
            page.slots[i].setWeight(slotTag.getFloat("Weight"));
            page.slots[i].setMiss(slotTag.getBoolean("Miss"));
        }

        return page;
    }
}