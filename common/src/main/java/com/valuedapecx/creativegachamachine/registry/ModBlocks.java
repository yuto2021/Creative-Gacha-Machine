package com.valuedapecx.creativegachamachine.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.block.GachaMachineBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(CreativeGachaMachine.MOD_ID, Registries.BLOCK);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(CreativeGachaMachine.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Block> GACHA_MACHINE =
            BLOCKS.register("gacha_machine", () -> new GachaMachineBlock(
                    BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.METAL)
            ));

    public static final RegistrySupplier<Item> GACHA_MACHINE_ITEM =
            ITEMS.register("gacha_machine", () -> new BlockItem(
                    GACHA_MACHINE.get(),
                    new Item.Properties()
            ));

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
    }

    private ModBlocks() {
    }
}