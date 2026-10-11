package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.item.GachaTicketItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(CreativeGachaMachine.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> EMPTY_REWARD =
            ITEMS.register("empty_reward", () -> new Item(
                    new Item.Properties()
            ));
    public static final RegistrySupplier<Item> GACHA_WRENCH =
            ITEMS.register("gacha_wrench", () -> new Item(
                    new Item.Properties().stacksTo(1)
            ));
    public static final RegistrySupplier<Item> GACHA_TICKET =
            ITEMS.register("gacha_ticket", () -> new GachaTicketItem(
                    new Item.Properties()
            ));


    public static void init() {
        ITEMS.register();
    }

    private ModItems() {
    }

    public static final CreativeModeTab GACHA_TAB = CreativeTabRegistry.create(
            Component.literal("Creative Gacha Machine"), () -> new ItemStack(ModBlocks.GACHA_MACHINE_ITEM.get())
    );
}