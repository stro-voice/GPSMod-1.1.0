package com.example.gpsmod.client;

import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gpsmod", value = Dist.CLIENT)
public class WorldRouteRenderer {

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent event) {
        if (!GPSManager.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Vector3d cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        MatrixStack matrixStack = event.getMatrixStack();

        matrixStack.pushPose();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        matrixStack.popPose();
    }
}