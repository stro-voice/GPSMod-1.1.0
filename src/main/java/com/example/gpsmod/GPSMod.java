package com.example.gpsmod;

import com.example.gpsmod.network.ModNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GPSMod.MOD_ID)
public class GPSMod {
    public static final String MOD_ID = "gpsmod";

    public GPSMod() {
        // 1. Подписка на шину событий жизненного цикла Forge
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);

        // 2. Инициализация сети (регистрация пакетов сохранения)
        ModNetwork.init();

        // 3. БЕЗОПАСНЫЙ ЗАПУСК КЛИЕНТА: Сервер полностью пропустит этот блок
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSetup::init);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Логика общей инициализации сервера и клиента (при необходимости)
    }
}