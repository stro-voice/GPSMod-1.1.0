package com.example.gpsmod.client;

import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class RouteSelectionScreen extends Screen {

    private BlockPos selectedTarget = null;

    public RouteSelectionScreen() {
        super(new StringTextComponent("Выбор точки назначения"));
    }

    @Override
    protected void init() {
        super.init();

        this.addButton(new Button(this.width / 2 - 80, this.height - 35, 160, 20,
            new StringTextComponent("Проложить сюда"), (button) -> {
                if (selectedTarget != null && this.minecraft != null && this.minecraft.player != null) {
                    GPSManager.getInstance().buildSmartPathToTarget(
                        this.minecraft.level, 
                        this.minecraft.player.blockPosition(), 
                        selectedTarget
                    );
                    this.minecraft.setScreen(null);
                }
            }));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.player != null) { // ЛКМ
            BlockPos playerPos = this.minecraft.player.blockPosition();
            
            int offsetX = (int) ((mouseX - (this.width / 2.0)) / 5.0);
            int offsetZ = (int) ((mouseY - (this.height / 2.0)) / 5.0);

            this.selectedTarget = playerPos.offset(offsetX, 0, offsetZ);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        int cx = this.width / 2;
        int cy = this.height / 2;

        fill(matrixStack, cx - 150, cy - 120, cx + 150, cy + 100, 0xDD111111);
        fill(matrixStack, cx - 148, cy - 118, cx + 148, cy + 98, 0xFF050505);

        // Позиция игрока (Синий квадрат)
        fill(matrixStack, cx - 3, cy - 3, cx + 3, cy + 3, 0xFF00AAFF);

        // Выбранная метка клика (Красный маркер)
        if (selectedTarget != null && this.minecraft != null && this.minecraft.player != null) {
            BlockPos pPos = this.minecraft.player.blockPosition();
            int targetScreenX = cx + (selectedTarget.getX() - pPos.getX()) * 5;
            int targetScreenY = cy + (selectedTarget.getZ() - pPos.getZ()) * 5;

            if (targetScreenX >= cx - 145 && targetScreenX <= cx + 145 &&
                targetScreenY >= cy - 115 && targetScreenY <= cy + 115) {
                fill(matrixStack, targetScreenX - 5, targetScreenY - 5, targetScreenX + 5, targetScreenY + 5, 0xFFFF0000);
            }
        }

        if (this.font != null) {
            this.font.draw(matrixStack, "Кликните ЛКМ по карте, чтобы выбрать финиш", 10, 10, 0xFFFFFF);
            if (selectedTarget != null) {
                this.font.draw(matrixStack, "Цель: X=" + selectedTarget.getX() + " Z=" + selectedTarget.getZ(), 10, 25, 0xFF00FF00);
            } else {
                this.font.draw(matrixStack, "Точка не выбрана!", 10, 25, 0xFFFFAA00);
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}