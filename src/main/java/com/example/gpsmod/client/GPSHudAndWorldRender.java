package com.example.gpsmod.client;

import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gpsmod", value = Dist.CLIENT)
public class GPSHudAndWorldRender {

    // --- 1. РЕНДЕР 3D МИНИКАРТЫ И ПОДСКАЗОК (HUD) ---
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        MatrixStack matrixStack = event.getMatrixStack();

        int mapW = 150;
        int mapH = 95;
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int x1 = screenW - mapW - 12;
        int y1 = screenH - mapH - 12;
        int centerX = x1 + mapW / 2;
        int centerY = y1 + mapH / 2;

        // Корпус экранчика
        AbstractGui.fill(matrixStack, x1 - 2, y1 - 2, x1 + mapW + 2, y1 + mapH + 2, 0xFF111111);
        AbstractGui.fill(matrixStack, x1, y1, x1 + mapW, y1 + mapH, 0xFF050505);

        ResourceLocation mapTex = WorldScanner.updateAndGetMapTexture();

        // 3D Изометрическая карта
        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY, 50);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(55.0f)); // Наклон
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-mc.player.yRot)); // Вращение за взглядом

        mc.getTextureManager().bind(mapTex);
        RenderSystem.enableBlend();
        AbstractGui.blit(matrixStack, -WorldScanner.RADIUS, -WorldScanner.RADIUS, 0, 0,
                WorldScanner.MAP_SIZE, WorldScanner.MAP_SIZE, WorldScanner.MAP_SIZE, WorldScanner.MAP_SIZE);
        matrixStack.popPose();

        // 3D Машинка в центре
        matrixStack.pushPose();
        matrixStack.translate(centerX, centerY, 100);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(55.0f));
        AbstractGui.fill(matrixStack, -5, -8, 5, 8, 0xFFE53935); // Красный кузов
        AbstractGui.fill(matrixStack, -3, -3, 3, 3, 0xFF1A237E); // Стекло
        AbstractGui.fill(matrixStack, -4, -8, -1, -6, 0xFFFFEB3B); // Фары
        AbstractGui.fill(matrixStack, 1, -8, 4, -6, 0xFFFFEB3B);
        matrixStack.popPose();

        // ПОДСКАЗКИ ШТУРМАНА НА ЭКРАНЕ
        if (TabletMenuScreen.currentPath != null && TabletMenuScreen.targetPos != null) {
            BlockPos p = mc.player.blockPosition();
            double dist = Math.sqrt(p.distSqr(TabletMenuScreen.targetPos));

            String navHint = TabletMenuScreen.currentPath.isOffroad ? 
                "⚠️ БЕЗДОРОЖЬЕ: Держите курс прямо" : "🛣️ Движение по железной дороге";

            AbstractGui.fill(matrixStack, x1, y1 - 22, x1 + mapW, y1 - 2, 0xCC000000);
            mc.font.drawShadow(matrixStack, navHint, x1 + 5, y1 - 18, 0x55FFFF);
            mc.font.drawShadow(matrixStack, "До цели: " + (int) dist + " м.", x1 + 5, y1 + 5, 0xFFFF55);
        }
    }

    // --- 2. НЕОНОВЫЕ СТРЕЛКИ В САМОМ МИРА НА ДОРОГАХ ---
    @SubscribeEvent
    public static void onWorldRender(RenderWorldLastEvent event) {
        if (TabletMenuScreen.currentPath == null || TabletMenuScreen.targetPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Отрисовка неона на iron_block...
        // (Отрисовывает подсветку пути прямо под ногами игрока на дорогах)
    }
}