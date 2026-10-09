package com.valuedapecx.creativegachamachine.forge;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreativeGachaMachine.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CreativeGachaMachine.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        CreativeGachaMachine.init();
    }
}
