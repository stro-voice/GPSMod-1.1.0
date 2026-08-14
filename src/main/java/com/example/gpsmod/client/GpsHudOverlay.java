package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
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
        if (player == null) return;

        // ПРОВЕРКА ВТОРОЙ РУКИ (Offhand)
        ItemStack stack = player.getOffhandItem(); // <-- Карта проверяется во второй руке!

        if (stack.getItem() == Items.FILLED_MAP && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.getBoolean("HasGPS")) {
                int targetX = tag.getInt("TargetX");
                int targetZ = tag.getInt("TargetZ");

                // Вычисляем дистанцию до цели
                double dx = targetX - player.getX();
                double dz = targetZ - player.getZ();
                int distance = (int) Math.sqrt(dx * dx + dz * dz);

                // Вычисляем угол направления относительно взгляда игрока
                double targetAngle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
                double relativeAngle = MathHelper.wrapDegrees(targetAngle - player.yRot);

                // Отрисовываем интерфейс навигатора
                renderGpsPanel(event.getMatrixStack(), mc, distance, targetX, targetZ, relativeAngle);
            }
        }
    }

    private static void renderGpsPanel(MatrixStack matrix, Minecraft mc, int distance, int targetX, int targetZ, double angle) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        
        // Размеры и позиция виджета (верхний правый угол)
        int width = 130;
        int height = 50;
        int x = screenWidth - width - 10;
        int y = 10;

        // 1. Фон бортового компьютера
        AbstractGui.fill(matrix, x, y, x + width, y + height, 0xDD0F172A);
        
        // 2. Неоновая акцентная рамка
        AbstractGui.fill(matrix, x, y, x + width, y + 2, 0xFF00E5FF);
        AbstractGui.fill(matrix, x, y + height - 1, x + width, y + height, 0x4400E5FF);

        // 3. Заголовок
        mc.font.draw(matrix, new StringTextComponent("● ").withStyle(TextFormatting.GREEN)
                .append(new StringTextComponent("GPS NAVIGATOR").withStyle(TextFormatting.BOLD, TextFormatting.WHITE)), 
                x + 8, y + 6, 0xFFFFFFFF);

        // 4. Указатель направления и дистанция
        String directionArrow = getArrowSymbol(angle);
        String distText = distance + " м";
        mc.font.draw(matrix, new StringTextComponent(directionArrow + " ").withStyle(TextFormatting.AQUA)
                .append(new StringTextComponent(distText).withStyle(TextFormatting.YELLOW, TextFormatting.BOLD)), 
                x + 10, y + 20, 0xFFFFFFFF);

        // 5. Координаты цели
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