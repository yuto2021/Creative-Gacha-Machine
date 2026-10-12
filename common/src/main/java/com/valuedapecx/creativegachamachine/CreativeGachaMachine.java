package com.valuedapecx.creativegachamachine;

import com.valuedapecx.creativegachamachine.registry.*;

public final class CreativeGachaMachine {

    public static final String MOD_ID = "creative_gacha_machine";

    public static void init() {
        ModBlocks.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModPackets.init();
        ModDataComponents.init();
    }
}