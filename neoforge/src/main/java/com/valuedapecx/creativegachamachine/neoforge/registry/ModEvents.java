package com.valuedapecx.creativegachamachine.neoforge.registry;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.block.GachaMachineBlock;
import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaMachineMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = CreativeGachaMachine.MOD_ID)
public final class ModEvents {

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();

        BlockState state = event.getState();

        if (!(state.getBlock() instanceof GachaMachineBlock)) {
            return;
        }

        BlockPos pos = state.getValue(GachaMachineBlock.HALF)
                == DoubleBlockHalf.UPPER
                ? event.getPos().below()
                : event.getPos();

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof GachaMachineBlockEntity machine
                && machine.getMode() == GachaMachineMode.RUNNING) {

            event.setCanceled(true);
        }
    }
}
