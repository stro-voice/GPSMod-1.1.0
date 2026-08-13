package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.GPSManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
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
        if (mc.player == null || mc.world == null) return;

        // Клавиша '-'
        if (event.getKey() == GLFW.GLFW_KEY_MINUS && event.getAction() == GLFW.GLFW_PRESS) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new GPSNpcUi());
            }
        }

        // Клавиша '='
        if (event.getKey() == GLFW.GLFW_KEY_EQUAL && event.getAction() == GLFW.GLFW_PRESS) {
            RayTraceResult ray = mc.objectMouseOver;
            if (ray != null && ray.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockRay = (BlockRayTraceResult) ray;
                BlockPos playerPos = mc.player.getPosition();
                GPSManager.getInstance().buildPath(mc.world, playerPos, blockRay.getPos());
            }
        }
    }
}