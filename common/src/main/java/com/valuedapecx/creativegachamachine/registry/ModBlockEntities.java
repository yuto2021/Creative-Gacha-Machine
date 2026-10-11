package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(CreativeGachaMachine.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<GachaMachineBlockEntity>> GACHA_MACHINE =
            BLOCK_ENTITY_TYPES.register("gacha_machine", () -> BlockEntityType.Builder.of(
                    GachaMachineBlockEntity::new, ModBlocks.GACHA_MACHINE.get()
            ).build(null));

    public static void init() {
        BLOCK_ENTITY_TYPES.register();
    }

    private ModBlockEntities() {
    }
}