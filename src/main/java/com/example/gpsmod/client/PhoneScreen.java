package com.example.gpsmod.client;

import com.example.gpsmod.block.GPSBeaconBlock;
import com.example.gpsmod.navigation.MapPathRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class PhoneScreen extends Screen {

    public PhoneScreen() {
        super(new StringTextComponent("Смартфон GPS"));
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 40;

        if (GPSBeaconBlock.BEACONS.isEmpty()) {
            this.addButton(new Button(centerX - 60, startY + 50, 120, 20, new StringTextComponent("Закрыть"), b -> this.onClose()));
            return;
        }

        int index = 0;
        for (BlockPos beaconPos : GPSBeaconBlock.BEACONS) {
            if (index >= 4) break;

            String label = "📍 Маяк #" + (index + 1) + " [" + beaconPos.getX() + ", " + beaconPos.getZ() + "]";
            int yPos = startY + (index * 24);

            this.addButton(new Button(centerX - 80, yPos, 160, 20, new StringTextComponent(label), b -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    MapPathRenderer.drawPathOnMap(this.minecraft.player, beaconPos);
                }
                this.onClose();
            }));
            index++;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Рисуем рамку корпуса телефона
        fill(matrixStack, centerX - 100, centerY - 80, centerX + 100, centerY + 80, 0xFF111111);
        fill(matrixStack, centerX - 95, centerY - 75, centerX + 95, centerY + 75, 0xFF222222);

        if (GPSBeaconBlock.BEACONS.isEmpty()) {
            drawCenteredString(matrixStack, this.font, "Маяки не найдены!", centerX, centerY - 10, 0xFF5555);
            drawCenteredString(matrixStack, this.font, "Установите 'GPS Маяк' в мире.", centerX, centerY + 10, 0xAAAAAA);
        } else {
            drawCenteredString(matrixStack, this.font, "📱 Выберите точку назначения:", centerX, centerY - 65, 0x55FFFF);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}