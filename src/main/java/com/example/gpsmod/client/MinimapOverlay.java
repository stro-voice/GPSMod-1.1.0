package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.vector.Vector3f;
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

        // Позиция прямоугольника строго ВНИЗУ СПРАВА
        int mapW = 150;
        int mapH = 95;
        int margin = 12;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int x1 = screenW - mapW - margin;
        int y1 = screenH - mapH - margin;
        int x2 = screenW - margin;
        int y2 = screenH - margin;

        // Рамка и фон прямоугольной миникарты
        AbstractGui.fill(matrixStack, x1 - 2, y1 - 2, x2 + 2, y2 + 2, 0xFF0D0D0D);
        AbstractGui.fill(matrixStack, x1, y1, x2, y2, 0xDD181818);

        int centerX = x1 + mapW / 2;
        int centerY = y1 + mapH / 2;

        // РЕНДЕР 3D ИЗОМЕТРИИ И МАШИНКИ
        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY, 100);

        // Наклоняем плоскость карты для создания 3D-эффекта изометрии
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(55.0f));

        // Отрисовка дорожной полосы на 3D карте
        AbstractGui.fill(matrixStack, -5, -35, 5, 35, 0xFF555555);

        // Поворачиваем 3D Машинку за поворотом головы игрока
        float yaw = mc.player.yRot;
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-yaw));

        // 3D Моделька Машинки (Корпус + Капот)
        AbstractGui.fill(matrixStack, -4, -7, 4, 7, 0xFFCC0000);   // Красный кузов
        AbstractGui.fill(matrixStack, -3, -3, 3, 2, 0xFF111111);   // Лобовое стекло/салон
        AbstractGui.fill(matrixStack, -3, -7, 3, -5, 0xFFFFD700);  // Желтые фары впереди

        matrixStack.popPose();

        // Надпись статуса
        if (MapScreen.targetPos != null) {
            if (MapScreen.isOffroadMode) {
                mc.font.drawShadow(matrixStack, "БЕЗДОРОЖЬЕ", x1 + 5, y1 + 5, 0xFF5555);
            } else {
                mc.font.drawShadow(matrixStack, "GPS: ДОРОГА", x1 + 5, y1 + 5, 0x55FF55);
            }
        }
    }
}