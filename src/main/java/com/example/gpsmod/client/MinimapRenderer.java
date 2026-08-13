package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT)
public class MinimapRenderer {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final int MAP_SIZE = 90;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.player == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();
        int screenWidth = event.getWindow().getScaledWidth();
        int screenHeight = event.getWindow().getScaledHeight();

        int x = screenWidth - MAP_SIZE - 15;
        int y = screenHeight - MAP_SIZE - 15;

        // Рамка мини-карты
        AbstractGui.fill(matrixStack, x - 3, y - 3, x + MAP_SIZE + 3, y + MAP_SIZE + 3, 0xDD111111);
        AbstractGui.fill(matrixStack, x, y, x + MAP_SIZE, y + MAP_SIZE, 0xFF050505);

        // Отрисовка пути
        List<BlockPos> path = GPSManager.getInstance().getCurrentPath();
        BlockPos playerPos = mc.player.getPosition();

        if (path != null && !path.isEmpty()) {
            for (BlockPos pos : path) {
                int dx = pos.getX() - playerPos.getX();
                int dz = pos.getZ() - playerPos.getZ();

                int mapX = x + (MAP_SIZE / 2) + dx;
                int mapY = y + (MAP_SIZE / 2) + dz;

                if (mapX >= x && mapX < x + MAP_SIZE && mapY >= y && mapY < y + MAP_SIZE) {
                    AbstractGui.fill(matrixStack, mapX - 1, mapY - 1, mapX + 1, mapY + 1, 0xFFFF8C00);
                }
            }
        }

        // Игрок в центре
        int centerX = x + (MAP_SIZE / 2);
        int centerY = y + (MAP_SIZE / 2);
        AbstractGui.fill(matrixStack, centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFF00AAFF);
    }
}