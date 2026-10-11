package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab>
            CREATIVE_TABS =
            DeferredRegister.create(
                    CreativeGachaMachine.MOD_ID,
                    Registries.CREATIVE_MODE_TAB
            );

    public static final RegistrySupplier<CreativeModeTab>
            GACHA_TAB =
            CREATIVE_TABS.register(
                    "gacha_tab",
                    () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                            .title(
                                    Component.literal(
                                            "Creative Gacha Machine"
                                    )
                            )
                            .icon(
                                    () -> new ItemStack(
                                            ModBlocks.GACHA_MACHINE_ITEM.get()
                                    )
                            )
                            .displayItems(
                                    (parameters, output) -> {
                                        output.accept(
                                                ModBlocks.GACHA_MACHINE_ITEM.get()
                                        );
                                        output.accept(
                                                ModItems.EMPTY_REWARD.get()
                                        );
                                        output.accept(
                                                ModItems.GACHA_WRENCH.get()
                                        );
                                    }
                            )
                            .build()
            );

    public static void init() {
        CREATIVE_TABS.register();
    }

    private ModCreativeTabs() {
    }
}