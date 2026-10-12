package com.valuedapecx.creativegachamachine.item;

import com.valuedapecx.creativegachamachine.registry.ModDataComponents;
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

        String ticketName =
                stack.get(
                        ModDataComponents.TICKET_NAME.get()
                );

        Integer ticketType =
                stack.get(
                        ModDataComponents.TICKET_TYPE.get()
                );

        if (ticketName == null && ticketType == null) {
            return;
        }

        tooltip.add(
                Component.literal("Ticket Information")
                        .withStyle(ChatFormatting.GOLD)
        );

        if (ticketName != null)
            tooltip.add(
                    Component.translatable(
                            "tooltip.gacha_ticket.name",
                            ticketName
                    )
            );

        if (ticketType != null)
            tooltip.add(
                    Component.translatable(
                            "tooltip.gacha_ticket.type",
                            ticketType
                    )
            );
    }
}
