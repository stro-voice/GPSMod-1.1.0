package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT)
public class GpsHudOverlay {

    // 1. Проверка прибытия в точку каждый тик
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || ClientGPSData.targetPos == null) return;

        BlockPos playerPos = mc.player.blockPosition();
        BlockPos targetPos = ClientGPSData.targetPos;

        double dx = playerPos.getX() - targetPos.getX();
        double dz = playerPos.getZ() - targetPos.getZ();
        double distanceSq = dx * dx + dz * dz;

        // Если игрок подошел ближе чем на 3 блока по горизонтали
        if (distanceSq <= 9.0) {
            mc.player.sendMessage(
                new StringTextComponent("✅ Вы прибыли в пункт назначения: ")
                    .append(new StringTextComponent(ClientGPSData.targetName).withStyle(TextFormatting.GREEN)),
                mc.player.getUUID()
            );
            ClientGPSData.clearTarget();
        }
    }

    // 2. Отрисовка HUD строго в правом нижнем углу
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (ClientGPSData.targetPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        MatrixStack matrixStack = event.getMatrixStack();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        BlockPos target = ClientGPSData.targetPos;
        int distance = (int) Math.sqrt(player.distanceToSqr(target.getX(), target.getY(), target.getZ()));

        String nameText = "📍 " + ClientGPSData.targetName;
        String distText = "📏 Дистанция: " + distance + "m";
        String posText = "🎯 X: " + target.getX() + " Z: " + target.getZ();

        int x = screenWidth - 150;
        int y = screenHeight - 45;

        mc.font.draw(matrixStack, nameText, x, y, 0xFFFF55);
        mc.font.draw(matrixStack, distText, x, y + 10, 0xFFFFFF);
        mc.font.draw(matrixStack, posText, x, y + 20, 0xAAAAAA);
    }
}