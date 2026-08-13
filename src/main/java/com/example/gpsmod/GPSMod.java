package com.example.gpsmod;

import com.example.gpsmod.network.PacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GPSMod.MOD_ID)
public class GPSMod {
    public static final String MOD_ID = "gpsmod";

    public GPSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }
}