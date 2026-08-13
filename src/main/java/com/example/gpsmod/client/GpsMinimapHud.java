package com.example.gpsmod.client;

import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.List;

@Mod.EventBusSubscriber(modid = "gpsmod", value = Dist.CLIENT)
public class GpsMinimapHud {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!GPSManager.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MatrixStack matrixStack = event.getMatrixStack();

        int mapSize = 90;
        int mapX = 15;
        int mapY = 15;
        int centerX = mapX + mapSize / 2;
        int centerY = mapY + mapSize / 2;

        AbstractGui.fill(matrixStack, mapX - 2, mapY - 2, mapX + mapSize + 2, mapY + mapSize + 2, 0xFF333333);
        AbstractGui.fill(matrixStack, mapX, mapY, mapX + mapSize, mapY + mapSize, 0xDD1A1A1A);

        double scaleFactor = mc.getWindow().getGuiScale();
        RenderSystem.enableScissor(
                (int) (mapX * scaleFactor),
                (int) (mc.getWindow().getHeight() - (mapY + mapSize) * scaleFactor),
                (int) (mapSize * scaleFactor),
                (int) (mapSize * scaleFactor)
        );

        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY, 0);

        float playerYaw = mc.player.yRot;
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-playerYaw - 180));

        Vector3d playerPos = mc.player.position();
        List<Vector3d> path = GPSManager.getCurrentPath(playerPos);

        float scale = 1.2f;
        for (int i = 0; i < path.size() - 1; i++) {
            Vector3d p1 = path.get(i);
            Vector3d p2 = path.get(i + 1);

            float x1 = (float) (p1.x - playerPos.x) * scale;
            float y1 = (float) (p1.z - playerPos.z) * scale;
            float x2 = (float) (p2.x - playerPos.x) * scale;
            float y2 = (float) (p2.z - playerPos.z) * scale;

            AbstractGui.fill(matrixStack, (int)x1 - 1, (int)y1 - 1, (int)x2 + 1, (int)y2 + 1, 0xFF00AAFF);
        }

        matrixStack.popPose();
        RenderSystem.disableScissor();

        AbstractGui.fill(matrixStack, centerX - 2, centerY - 5, centerX + 2, centerY + 3, 0xFFFF2222);
        AbstractGui.fill(matrixStack, centerX - 4, centerY + 1, centerX + 4, centerY + 3, 0xFFFF2222);

        double dist = GPSManager.getDistanceToTarget(playerPos);
        String instruction = GPSManager.getTurnInstruction(playerYaw, playerPos);

        mc.font.draw(matrixStack, String.format("%.0f м", dist), mapX + 4, mapY + mapSize + 4, 0xFFFFFF);
        mc.font.draw(matrixStack, instruction, mapX + 4, mapY + mapSize + 16, 0x55FF55);
    }
}