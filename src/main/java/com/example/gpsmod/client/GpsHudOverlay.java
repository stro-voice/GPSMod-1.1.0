package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GpsHudOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || ClientGpsState.activeTarget == null) return;

        BlockPos target = ClientGpsState.activeTarget;

        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        int distance = (int) Math.sqrt(dx * dx + dz * dz);

        // АВТО-СБРОС И ЗВУК ПРИ ПРИБЫТИИ (<= 3 блоков)
        if (distance <= 3) {
            ClientGpsState.activeTarget = null;
            
            player.sendMessage(
                new StringTextComponent("🏁 Вы прибыли в пункт назначения!")
                    .withStyle(TextFormatting.GOLD, TextFormatting.BOLD),
                player.getUUID()
            );
            
            if (mc.level != null) {
                mc.level.playSound(player, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            return;
        }

        double targetAngle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double relativeAngle = MathHelper.wrapDegrees(targetAngle - player.yRot);

        renderGpsPanel(event.getMatrixStack(), mc, distance, target.getX(), target.getZ(), relativeAngle);
    }

    private static void renderGpsPanel(MatrixStack matrix, Minecraft mc, int distance, int targetX, int targetZ, double angle) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        int width = 130;
        int height = 50;

        // ПРАВЫЙ НИЖНИЙ УГОЛ
        int x = screenWidth - width - 10;
        int y = screenHeight - height - 10;

        // Фон
        AbstractGui.fill(matrix, x, y, x + width, y + height, 0xDD0F172A);
        // Неоновые рамки
        AbstractGui.fill(matrix, x, y, x + width, y + 2, 0xFF00E5FF);
        AbstractGui.fill(matrix, x, y + height - 1, x + width, y + height, 0x4400E5FF);

        // Заголовок
        mc.font.draw(matrix, new StringTextComponent("● ").withStyle(TextFormatting.GREEN)
                .append(new StringTextComponent("GPS NAVIGATOR").withStyle(TextFormatting.BOLD, TextFormatting.WHITE)), 
                x + 8, y + 6, 0xFFFFFFFF);

        // Стрелка и дистанция
        String directionArrow = getArrowSymbol(angle);
        String distText = distance + " м";
        mc.font.draw(matrix, new StringTextComponent(directionArrow + " ").withStyle(TextFormatting.AQUA)
                .append(new StringTextComponent(distText).withStyle(TextFormatting.YELLOW, TextFormatting.BOLD)), 
                x + 10, y + 20, 0xFFFFFFFF);

        // Координаты цели
        String coordsText = "X: " + targetX + " | Z: " + targetZ;
        mc.font.draw(matrix, new StringTextComponent(coordsText).withStyle(TextFormatting.GRAY), 
                x + 10, y + 34, 0xFFAAAAAA);
    }

    private static String getArrowSymbol(double angle) {
        if (angle >= -22.5 && angle < 22.5) return "⬆ ПРЯМО";
        if (angle >= 22.5 && angle < 67.5) return "↗ ПРАВЕЕ";
        if (angle >= 67.5 && angle < 112.5) return "➡ ВПРАВО";
        if (angle >= 112.5 && angle < 157.5) return "↘ НАЗАД-ПРАВО";
        if (angle >= 157.5 || angle < -157.5) return "⬇ НАЗАД";
        if (angle >= -157.5 && angle < -112.5) return "↙ НАЗАД-ЛЕВО";
        if (angle >= -112.5 && angle < -67.5) return "⬅ ВЛЕВО";
        if (angle >= -67.5 && angle < -22.5) return "↖ ЛЕВЕЕ";
        return "⬆";
    }
}