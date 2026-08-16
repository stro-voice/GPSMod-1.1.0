package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT)
public class GpsHudOverlay {

    // 1. Проверка авто-завершения маршрута при приближении (менее 3 блоков)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || ClientGPSData.targetPos == null) return;

        BlockPos playerPos = mc.player.blockPosition();
        BlockPos targetPos = ClientGPSData.targetPos;

        double dx = playerPos.getX() - targetPos.getX();
        double dz = playerPos.getZ() - targetPos.getZ();

        if ((dx * dx + dz * dz) <= 9.0) {
            mc.player.sendMessage(
                new StringTextComponent("✅ Вы прибыли в пункт назначения: ")
                    .append(new StringTextComponent(ClientGPSData.targetName).withStyle(TextFormatting.GREEN)),
                mc.player.getUUID()
            );
            ClientGPSData.clearTarget();
        }
    }

    // 2. Отрисовка плашки HUD с фоном, координатами и стрелкой направления
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (ClientGPSData.targetPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // Проверка компаса в руках (главная или левая рука) для маппингов Mojang
        boolean hasCompass = player.getMainHandItem().getItem() == Items.COMPASS 
                          || player.getOffhandItem().getItem() == Items.COMPASS;

        if (!hasCompass) return;

        MatrixStack matrixStack = event.getMatrixStack();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        BlockPos target = ClientGPSData.targetPos;
        int distance = (int) Math.sqrt(player.distanceToSqr(target.getX(), target.getY(), target.getZ()));

        String arrow = getDirectionArrow(player, target);

        int width = 160;
        int height = 50;
        int x = screenWidth - width - 10;
        int y = screenHeight - height - 10;

        // Отрисовка полупрозрачного фона и зеленой полосы
        AbstractGui.fill(matrixStack, x, y, x + width, y + height, 0xD0101010);
        AbstractGui.fill(matrixStack, x, y, x + 3, y + height, 0xFF55FF55);

        String titleText = "📍 " + ClientGPSData.targetName + " " + arrow;
        String distText = "📏 Дистанция: " + distance + "м";
        String posText = "🎯 [X: " + target.getX() + " | Z: " + target.getZ() + "]";

        mc.font.draw(matrixStack, titleText, x + 8, y + 6, 0xFFFF55);
        mc.font.draw(matrixStack, distText, x + 8, y + 20, 0xFFFFFF);
        mc.font.draw(matrixStack, posText, x + 8, y + 34, 0xAAAAAA);
    }

    private static String getDirectionArrow(ClientPlayerEntity player, BlockPos target) {
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();

        double angleToTarget = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double playerYaw = player.yRot;
        double diff = MathHelper.wrapDegrees(angleToTarget - playerYaw);

        if (diff >= -22.5 && diff < 22.5) return "⬆️";
        if (diff >= 22.5 && diff < 67.5) return "↗️";
        if (diff >= 67.5 && diff < 112.5) return "➡️";
        if (diff >= 112.5 && diff < 157.5) return "↘️";
        if (diff >= 157.5 || diff < -157.5) return "⬇️";
        if (diff >= -157.5 && diff < -112.5) return "↙️";
        if (diff >= -112.5 && diff < -67.5) return "⬅️";
        return "↖️";
    }
}