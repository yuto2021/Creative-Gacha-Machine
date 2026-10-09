package com.valuedapecx.creativegachamachine.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GachaTicketItem extends Item {
    public GachaTicketItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (!stack.hasTag()) {
            return;
        }

        CompoundTag tag = stack.getTag();
        tooltip.add(Component.literal("Ticket Information").withStyle(ChatFormatting.GOLD));

        if (tag != null) {
            tooltip.add(Component.translatable("tooltip.gacha_creative_machine.name",
                    tag.getString("TicketName")));

            tooltip.add(Component.translatable("tooltip.gacha_creative_machine.type",
                    tag.getInt("TicketType")));
        }
    }
}
