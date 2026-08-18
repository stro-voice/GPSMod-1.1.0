package com.example.gpsmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    public static void init() {
        // Подписываемся на события шины мода для клиента
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onClientSetup);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        // Здесь можно регистрировать KeyBindings, клиентские бинды или диспетчеры экранов
    }
}