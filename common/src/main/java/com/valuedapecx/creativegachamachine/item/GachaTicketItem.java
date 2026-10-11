package com.valuedapecx.creativegachamachine.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class GachaTicketItem extends Item {
    public GachaTicketItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag tooltipFlag
    ) {

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return;
        }

        CompoundTag tag = data.copyTag();

        tooltip.add(
                Component.literal("Ticket Information")
                        .withStyle(ChatFormatting.GOLD)
        );

        tooltip.add(
                Component.translatable(
                        "tooltip.gacha_creative_machine.name",
                        tag.getString("TicketName")
                )
        );

        tooltip.add(
                Component.translatable(
                        "tooltip.gacha_creative_machine.type",
                        tag.getInt("TicketType")
                )
        );
    }
}
