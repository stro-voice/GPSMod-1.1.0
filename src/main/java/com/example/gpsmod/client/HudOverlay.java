package com.example.gpsmod.client;

import com.example.gpsmod.assistant.CopilotManager;
import com.example.gpsmod.init.KeyBindings;
import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gpsmod")
public class HudOverlay {

    private static boolean showHud = true;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (KeyBindings.openMapKey.consumeClick()) {
            mc.setScreen(new MapScreen());
        } else if (KeyBindings.toggleHudKey.consumeClick()) {
            showHud = !showHud;
        } else if (KeyBindings.resetPathKey.consumeClick()) {
            MapScreen.currentPath = null;
            mc.player.displayClientMessage(new StringTextComponent("§e[GPSMOD]: Маршрут сброшен."), true);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!showHud || event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return;

        MatrixStack matrix = event.getMatrixStack();

        double vx = player.getDeltaMovement().x;
        double vz = player.getDeltaMovement().z;
        double speed = Math.sqrt(vx * vx + vz * vz) * 20;

        boolean isOnRoad = RoadPathfinder.findRoadBlockUnder(player.level, player.blockPosition()) != null;

        mc.font.drawShadow(matrix, "§b[ GPSMOD 1.0.0 — Advisor ]", 10, 10, 0xFFFFFF);
        mc.font.drawShadow(matrix, String.format("Скорость: %.1f б/с", speed), 10, 22, 0xFFFFFF);
        mc.font.drawShadow(matrix, "Покрытие: " + (isOnRoad ? "§aЖелезо" : "§cБездорожье"), 10, 34, 0xFFFFFF);

        if (MapScreen.currentPath != null && !MapScreen.currentPath.isEmpty()) {
            BlockPos target = MapScreen.currentPath.get(MapScreen.currentPath.size() - 1);
            double dist = player.blockPosition().distSqr(target);
            double remainingBlocks = Math.sqrt(dist);

            mc.font.drawShadow(matrix, String.format("До цели: %.0f м", remainingBlocks), 10, 46, 0x55FF55);
            mc.font.drawShadow(matrix, "Полоса: [ ⬆️ ] [ ↗️ ]", 10, 58, 0xFFFF55);

            CopilotManager.update(player, isOnRoad, speed, remainingBlocks);
        } else {
            mc.font.drawShadow(matrix, "Маршрут: Не задан (Клавиша 0)", 10, 46, 0xAAAAAA);
        }
    }
}