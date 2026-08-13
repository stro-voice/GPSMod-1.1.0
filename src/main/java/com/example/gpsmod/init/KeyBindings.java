package com.example.gpsmod.init;

import com.example.gpsmod.client.MapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "gpsmod", value = Dist.CLIENT)
public class KeyBindings {

    public static KeyBinding keyMenu;
    public static KeyBinding keyMinus;
    public static KeyBinding keyEquals;

    public static void init() {
        // Регистрируем клавиши 0, -, =
        keyMenu = new KeyBinding("key.gpsmod.menu", GLFW.GLFW_KEY_0, "key.categories.gpsmod");
        keyMinus = new KeyBinding("key.gpsmod.minus", GLFW.GLFW_KEY_MINUS, "key.categories.gpsmod");
        keyEquals = new KeyBinding("key.gpsmod.equals", GLFW.GLFW_KEY_EQUAL, "key.categories.gpsmod");

        ClientRegistry.registerKeyBinding(keyMenu);
        ClientRegistry.registerKeyBinding(keyMinus);
        ClientRegistry.registerKeyBinding(keyEquals);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyMenu.isDown()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new MapScreen());
            } else if (mc.screen instanceof MapScreen) {
                mc.setScreen(null);
            }
        }
    }
}