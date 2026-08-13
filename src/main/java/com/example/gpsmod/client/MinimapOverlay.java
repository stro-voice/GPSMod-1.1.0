package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gpsmod", value = Dist.CLIENT)
public class MinimapOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        MatrixStack matrixStack = event.getMatrixStack();

        // Размеры и позиция прямоугольника в правом нижнем углу
        int mapWidth = 140;
        int mapHeight = 90;
        int margin = 10;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x1 = screenWidth - mapWidth - margin;
        int y1 = screenHeight - mapHeight - margin;
        int x2 = screenWidth - margin;
        int y2 = screenHeight - margin;

        // Отрисовка темной рамки-планшета (фон и границы)
        AbstractGui.fill(matrixStack, x1 - 2, y1 - 2, x2 + 2, y2 + 2, 0xFF111111); // Внешняя рамка
        AbstractGui.fill(matrixStack, x1, y1, x2, y2, 0xCC1E1E1E);             // Фон карты

        // Игрок в центре прямоугольной карты
        int playerCenterX = x1 + mapWidth / 2;
        int playerCenterY = y1 + mapHeight / 2;

        // Рисуем указатель игрока (зеленый квадрат в центре)
        AbstractGui.fill(matrixStack, playerCenterX - 2, playerCenterY - 2, playerCenterX + 2, playerCenterY + 2, 0xFF00FF00);

        // Информационная надпись внизу карты
        mc.font.drawShadow(matrixStack, "GPS ON", x1 + 5, y1 + 5, 0x55FF55);

        if (MapScreen.targetPos != null) {
            double dist = Math.sqrt(mc.player.blockPosition().distSqr(MapScreen.targetPos));
            String distText = String.format("%.0fm", dist);
            mc.font.drawShadow(matrixStack, distText, x2 - mc.font.width(distText) - 5, y1 + 5, 0xFFFF55);
        }
    }
}