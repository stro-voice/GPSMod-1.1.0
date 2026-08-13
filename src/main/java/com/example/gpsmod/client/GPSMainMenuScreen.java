package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class GPSMainMenuScreen extends Screen {

    private final int xSize = 200;
    private final int ySize = 140;

    public GPSMainMenuScreen() {
        super(new StringTextComponent("GPS Navigator"));
    }

    @Override
    protected void init() {
        super.init();
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        // 1. Открыть 3D карту
        this.addButton(new Button(guiLeft + 15, guiTop + 30, 170, 20,
            new StringTextComponent("1. Открыть 3D карту"), (button) -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(new Map3DScreen());
                }
            }));

        // 2. Проложить маршрут (Интерактивный выбор точки мышкой)
        this.addButton(new Button(guiLeft + 15, guiTop + 60, 170, 20,
            new StringTextComponent("2. Проложить маршрут"), (button) -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(new RouteSelectionScreen());
                }
            }));

        // 3. Назад
        this.addButton(new Button(guiLeft + 15, guiTop + 90, 170, 20,
            new StringTextComponent("3. Назад"), (button) -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(null);
                }
            }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        fill(matrixStack, guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xEE1E1E1E);
        fill(matrixStack, guiLeft, guiTop, guiLeft + xSize, guiTop + 2, 0xFF00AAFF);

        if (this.font != null) {
            this.font.draw(matrixStack, "Бортовой Компьютер", guiLeft + 10, guiTop + 10, 0xFFFFFFFF);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}