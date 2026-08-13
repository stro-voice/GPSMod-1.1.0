package com.example.gpsmod;

import com.example.gpsmod.init.KeyBindings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GPSMod.MOD_ID)
public class GPSMod {
    public static final String MOD_ID = "gpsmod";
    public static final String VERSION = "1.0.0";

    public GPSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        KeyBindings.init();
    }
}