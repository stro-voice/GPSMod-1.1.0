package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
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

        // Позиция: ВНИЗУ СПРАВА
        int mapW = 160;
        int mapH = 100;
        int margin = 12;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int x1 = screenW - mapW - margin;
        int y1 = screenH - mapH - margin;
        int x2 = screenW - margin;
        int y2 = screenH - margin;

        // Рамка планшета
        AbstractGui.fill(matrixStack, x1 - 3, y1 - 3, x2 + 3, y2 + 3, 0xFF101010);
        AbstractGui.fill(matrixStack, x1 - 1, y1 - 1, x2 + 1, y2 + 1, 0xFF333333);
        AbstractGui.fill(matrixStack, x1, y1, x2, y2, 0xFF050505);

        ResourceLocation mapTex = WorldScanner.updateAndGetMapTexture();

        int centerX = x1 + mapW / 2;
        int centerY = y1 + mapH / 2;

        // --- 3D ИЗОМЕТРИЯ КАРТЫ ---
        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY, 50);

        // Наклон карты для эффекта 3D
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(60.0f));

        // Поворот карты за игроком
        float playerYaw = mc.player.yRot;
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-playerYaw));

        mc.getTextureManager().bind(mapTex);
        RenderSystem.enableBlend();
        AbstractGui.blit(matrixStack, -WorldScanner.RADIUS, -WorldScanner.RADIUS, 0, 0, 
                WorldScanner.MAP_SIZE, WorldScanner.MAP_SIZE, WorldScanner.MAP_SIZE, WorldScanner.MAP_SIZE);

        matrixStack.popPose();

        // --- 3D РЕНДЕР МОДЕЛИ МАШИНКИ ---
        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY - 5, 120);

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(60.0f));

        // Объемный кузов машинки + фары
        AbstractGui.fill(matrixStack, -6, -10, 6, 10, 0xFFD32F2F); // Кузов
        AbstractGui.fill(matrixStack, -4, -4, 4, 4, 0xFF212121);   // Салон
        AbstractGui.fill(matrixStack, -4, -2, 4, 2, 0xFFB71C1C);   // Крыша
        AbstractGui.fill(matrixStack, -5, -10, -2, -8, 0xFFFFEB3B); // Фара Л
        AbstractGui.fill(matrixStack, 2, -10, 5, -8, 0xFFFFEB3B);  // Фара П

        matrixStack.popPose();

        // Надпись статуса
        if (MapScreen.targetPos != null) {
            if (MapScreen.isOffroad) {
                mc.font.drawShadow(matrixStack, "[!] БЕЗДОРОЖЬЕ", x1 + 6, y1 + 6, 0xFF5555);
            } else {
                mc.font.drawShadow(matrixStack, "GPS: МАРШРУТ", x1 + 6, y1 + 6, 0x55FF55);
            }
        }
    }
}