package com.example.gpsmod;

import com.example.gpsmod.init.KeyBindings;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("gpsmod")
public class GPSMod {

    public GPSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Регистрируем клавиши управления при старте клиента
        KeyBindings.init();
    }
}