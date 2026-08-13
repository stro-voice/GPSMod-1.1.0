package com.example.gpsmod.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding openMapKey;
    public static KeyBinding toggleHudKey;
    public static KeyBinding resetPathKey;

    public static void init() {
        openMapKey = new KeyBinding("key.gpsmod.open_map", GLFW.GLFW_KEY_0, "GPSMOD 1.0.0");
        toggleHudKey = new KeyBinding("key.gpsmod.toggle_hud", GLFW.GLFW_KEY_MINUS, "GPSMOD 1.0.0");
        resetPathKey = new KeyBinding("key.gpsmod.reset_path", GLFW.GLFW_KEY_EQUAL, "GPSMOD 1.0.0");

        ClientRegistry.registerKeyBinding(openMapKey);
        ClientRegistry.registerKeyBinding(toggleHudKey);
        ClientRegistry.registerKeyBinding(resetPathKey);
    }
}