package com.valuedapecx.creativegachamachine;

import com.valuedapecx.creativegachamachine.registry.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CreativeGachaMachine {

    public static final String MOD_ID = "creative_gacha_machine";

    public static final Logger LOGGER =
            LogManager.getLogger(
                    MOD_ID
            );

    public static void init() {
        ModBlocks.init();
        ModBlockEntities.init();
        ModMenus.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModPackets.init();
    }
}