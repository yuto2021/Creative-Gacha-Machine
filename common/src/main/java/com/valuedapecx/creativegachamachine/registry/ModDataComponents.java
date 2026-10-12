package com.valuedapecx.creativegachamachine.registry;

import com.mojang.serialization.Codec;
import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public final class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(
                    CreativeGachaMachine.MOD_ID,
                    Registries.DATA_COMPONENT_TYPE
            );

    public static final RegistrySupplier<DataComponentType<String>>
            TICKET_NAME =
            DATA_COMPONENTS.register(
                    "ticket_name",
                    () -> DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                            .build()
            );

    public static final RegistrySupplier<DataComponentType<Integer>>
            TICKET_TYPE =
            DATA_COMPONENTS.register(
                    "ticket_type",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
                            .build()
            );

    public static void init() {
        DATA_COMPONENTS.register();
    }

    private ModDataComponents() {
    }
}