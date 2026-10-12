package com.valuedapecx.creativegachamachine.neoforge;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.registry.*;
import net.neoforged.fml.common.Mod;

@Mod(CreativeGachaMachine.MOD_ID)
public final class CreativeGachaMachineNeoForge {

    public CreativeGachaMachineNeoForge() {
        ModBlocks.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModDataComponents.init();
    }
}