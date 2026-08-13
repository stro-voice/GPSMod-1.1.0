package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class MapScreen extends Screen {

    public static List<BlockPos> currentPath = new ArrayList<>();

    public MapScreen() {
        super(new StringTextComponent("GPS Map"));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        double distance = 0.0;
        if (currentPath != null && !currentPath.isEmpty() && minecraft != null && minecraft.player != null) {
            BlockPos target = currentPath.get(currentPath.size() - 1);
            distance = Math.sqrt(minecraft.player.blockPosition().distSqr(target));
        }

        // Строка 27 — синтаксис исправлен и добавлены точки с запятой
        String text = String.format("Дистанция до цели: %.0f м", distance);
        this.font.drawShadow(matrixStack, text, 10, 10, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}