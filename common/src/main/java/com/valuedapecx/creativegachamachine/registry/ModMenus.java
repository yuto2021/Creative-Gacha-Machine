package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.menu.GachaPaymentMenu;
import com.valuedapecx.creativegachamachine.menu.GachaTicketPaymentMenu;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
            CreativeGachaMachine.MOD_ID, Registries.MENU
    );

    public static final RegistrySupplier<MenuType<GachaMachineMenu>> GACHA_MACHINE =
            MENUS.register("gacha_machine", () -> new MenuType<>(
                    GachaMachineMenu::new,
                    FeatureFlags.DEFAULT_FLAGS
            ));

    public static final RegistrySupplier<MenuType<GachaPaymentMenu>> GACHA_PAYMENT =
            MENUS.register("gacha_payment", () -> new MenuType<>(
                    GachaPaymentMenu::new,
                    FeatureFlags.DEFAULT_FLAGS
            ));

    public static final RegistrySupplier<MenuType<GachaTicketPaymentMenu>> GACHA_TICKET_PAYMENT =
            MENUS.register("gacha_ticket_payment", () -> new MenuType<>(
                    GachaTicketPaymentMenu::new,
                    FeatureFlags.DEFAULT_FLAGS
            ));

    public static void init() {
        MENUS.register();
    }

    private ModMenus() {
    }
}