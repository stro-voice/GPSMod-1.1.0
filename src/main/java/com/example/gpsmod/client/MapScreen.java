package com.gpsmod.client;

import com.gpsmod.navigation.RoadPathfinder;
import com.mojang.blurbs.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class MapScreen extends Screen {

    public static List<BlockPos> currentPath = null;

    public MapScreen() {
        super(new StringTextComponent("GPSMOD - World Map"));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "GPSMOD 1.0.0 — Нажмите ЛКМ для выбора цели", this.width / 2, 15, 0xFFFFFF);

        PlayerEntity player = this.minecraft.player;
        if (player != null) {
            String coords = String.format("Вы здесь: X: %d, Z: %d", player.getBlockX(), player.getBlockZ());
            drawString(matrixStack, this.font, coords, 10, this.height - 20, 0xAAAAAA);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.player != null) { // ЛКМ
            PlayerEntity player = this.minecraft.player;
            // Простая проекция клика в координаты мира вокруг игрока
            int targetX = player.getBlockX() + (int) ((mouseX - (this.width / 2.0)) * 2);
            int targetZ = player.getBlockZ() + (int) ((mouseY - (this.height / 2.0)) * 2);

            BlockPos targetPos = new BlockPos(targetX, player.getBlockY(), targetZ);
            currentPath = RoadPathfinder.findPath(player.level, player.blockPosition(), targetPos);

            if (!currentPath.isEmpty()) {
                player.displayClientMessage(new StringTextComponent("§a[GPSMOD]: Маршрут успешно проложен!"), false);
                this.onClose();
            } else {
                player.displayClientMessage(new StringTextComponent("§c[GPSMOD]: Ошибка! Дорога не найдена."), false);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}