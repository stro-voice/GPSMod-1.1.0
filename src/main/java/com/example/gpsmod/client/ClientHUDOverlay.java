package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientHUDOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!ClientWaypointManager.hasTarget()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        BlockPos target = ClientWaypointManager.getTargetPos();
        if (target == null) return;

        // Расчет дистанции
        double dx = target.getX() + 0.5 - mc.player.getX();
        double dz = target.getZ() + 0.5 - mc.player.getZ();
        int distance = (int) MathHelper.sqrt(dx * dx + dz * dz);

        // Расчет угла для стрелки/компаса
        double targetAngle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double relativeAngle = MathHelper.wrapDegrees(targetAngle - mc.player.yRot);

        String arrow = getDirectionArrow(relativeAngle);
        String hudText = String.format("🧭 %s: %d м [%s]", ClientWaypointManager.getTargetName(), distance, arrow);

        MatrixStack matrixStack = event.getMatrixStack();
        int screenWidth = mc.getWindow().getGuiScaledWidth();

        // Отрисовка по центру верхней части экрана
        int x = (screenWidth - mc.font.width(hudText)) / 2;
        int y = 10;

        AbstractGui.fill(matrixStack, x - 5, y - 3, x + mc.font.width(hudText) + 5, y + 11, 0x80000000);
        mc.font.draw(matrixStack, hudText, x, y, 0x00FF00);
    }

    private static String getDirectionArrow(double angle) {
        if (angle >= -22.5 && angle < 22.5) return "↑";
        if (angle >= 22.5 && angle < 67.5) return "↗";
        if (angle >= 67.5 && angle < 112.5) return "→";
        if (angle >= 112.5 && angle < 157.5) return "↘";
        if (angle >= 157.5 || angle < -157.5) return "↓";
        if (angle >= -157.5 && angle < -112.5) return "↙";
        if (angle >= -112.5 && angle < -67.5) return "←";
        return "↖";
    }
}