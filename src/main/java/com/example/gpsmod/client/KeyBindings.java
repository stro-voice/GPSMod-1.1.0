package com.example.gpsmod.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding openGpsMenuKey;

    public static void register() {
        openGpsMenuKey = new KeyBinding(
            "key.gpsmod.open_menu",
            GLFW.GLFW_KEY_0,
            "key.categories.gpsmod"
        );
        ClientRegistry.registerKeyBinding(openGpsMenuKey);
    }
}