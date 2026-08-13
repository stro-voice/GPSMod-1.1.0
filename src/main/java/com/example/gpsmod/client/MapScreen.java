package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class MapScreen extends Screen {

    public static BlockPos targetPos = null;

    public MapScreen() {
        super(new StringTextComponent("Яндекс / Google GPS"));
    }

    @Override
    protected void init() {
        super.init();

        // Кнопка сброса маршрута
        this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20, 
                new StringTextComponent("Сбросить маршрут"), (button) -> {
            targetPos = null;
            this.onClose();
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredString(matrixStack, this.font, "=== НАВИГАТОР ===", this.width / 2, 15, 0x00FF00);

        if (this.minecraft != null && this.minecraft.player != null) {
            BlockPos playerPos = this.minecraft.player.blockPosition();
            String info = String.format("Вы здесь: X: %d | Y: %d | Z: %d", playerPos.getX(), playerPos.getY(), playerPos.getZ());
            drawCenteredString(matrixStack, this.font, info, this.width / 2, 40, 0xFFFFFF);

            drawCenteredString(matrixStack, this.font, "Кликните ЛКМ по экрану, чтобы задать цель (50 блоков вперед)", 
                    this.width / 2, 70, 0xAAAAAA);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.player != null) {
            // Клик задает точку впереди
            targetPos = this.minecraft.player.blockPosition().north(50);
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}