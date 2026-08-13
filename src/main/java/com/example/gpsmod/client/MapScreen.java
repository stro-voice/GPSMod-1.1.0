package com.example.gpsmod.client;

import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
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

        PlayerEntity player = this.minecraft != null ? this.minecraft.player : null;
        if (player != null) {
        mc.font.drawShadow(matrixStack, String.format("До цели: %d м", (int) remainingBlocks), 10, 46, 0x55FF55)
            drawString(matrixStack, this.font, coords, 10, this.height - 20, 0xAAAAAA);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.player != null) {
            PlayerEntity player = this.minecraft.player;

            int targetX = (int) player.getX() + (int) ((mouseX - (this.width / 2.0)) * 2);
            int targetZ = (int) player.getZ() + (int) ((mouseY - (this.height / 2.0)) * 2);

            BlockPos targetPos = new BlockPos(targetX, player.getY(), targetZ);
            currentPath = RoadPathfinder.findPath(player.level, player.blockPosition(), targetPos);

            if (currentPath != null && !currentPath.isEmpty()) {
                player.displayClientMessage(new StringTextComponent("§a[GPSMOD]: Маршрут проложен!"), false);
                this.onClose();
            } else {
                player.displayClientMessage(new StringTextComponent("§c[GPSMOD]: Дорога не найдена!"), false);
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