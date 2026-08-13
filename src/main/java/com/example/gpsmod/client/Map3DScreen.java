package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class Map3DScreen extends Screen {

    private float yaw = 45.0f;
    private float pitch = 30.0f;
    private float zoom = 50.0f;

    public Map3DScreen() {
        super(new StringTextComponent("3D Map"));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 1) { // ПКМ
            this.yaw += (float) dragX * 0.5f;
            this.pitch += (float) dragY * 0.5f;
            this.pitch = Math.max(10.0f, Math.min(85.0f, this.pitch));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.zoom -= (float) delta * 5.0f;
        this.zoom = Math.max(10.0f, Math.min(150.0f, this.zoom));
        return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        if (this.font != null) {
            this.font.draw(matrixStack, "3D Карта - Зажмите ПКМ для вращения, Колесико для зума", 10, 10, 0xFFFFFF);
            this.font.draw(matrixStack, "Масштаб: " + (int) zoom + "% | Угол: " + (int) yaw + "°", 10, 25, 0x00FF00);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}