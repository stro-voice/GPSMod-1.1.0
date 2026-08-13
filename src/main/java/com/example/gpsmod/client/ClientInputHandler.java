package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.GPSManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT)
public class ClientInputHandler {

    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (mc.player == null || mc.level == null) return;

        // Нажатие '0' -> Открыть меню
        if (event.getKey() == GLFW.GLFW_KEY_0 && event.getAction() == GLFW.GLFW_PRESS) {
            if (mc.screen == null) {
                mc.setScreen(new GPSMainMenuScreen());
            }
        }

        // Нажатие '-' -> Показать / скрыть мини-карту
        if (event.getKey() == GLFW.GLFW_KEY_MINUS && event.getAction() == GLFW.GLFW_PRESS) {
            GPSManager.getInstance().toggleMinimap();
        }

        // Нажатие '=' -> Сбросить маршрут
        if (event.getKey() == GLFW.GLFW_KEY_EQUAL && event.getAction() == GLFW.GLFW_PRESS) {
            GPSManager.getInstance().clearPath();
        }
    }
}