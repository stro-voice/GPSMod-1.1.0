package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// КРИТИЧЕСКИ ВАЖНО: value = Dist.CLIENT говорит Forge полностью проигнорировать этот класс на сервере
@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientHUDHandler {

    @SubscribeEvent
    public static void onRenderHUD(RenderGameOverlayEvent.Post event) {
        // Безопасное обращение к клиенту
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Minecraft mc = Minecraft.getInstance();

            // Проверка текущего экрана
            Screen currentScreen = mc.screen;
            
            if (mc.player != null && currentScreen == null) {
                // Код отрисовки компаса / GPS метки на экране игрока
            }
        }
    }
}