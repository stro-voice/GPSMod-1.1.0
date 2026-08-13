package com.example.gpsmod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gpsmod", value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (KeyBindings.openGpsMenuKey.consumeClick()) {
            mc.setScreen(new GpsMapScreen());
        }
    }
}