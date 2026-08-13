package com.example.gpsmod.client;

import com.example.gpsmod.block.GPSBeaconBlock;
import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class TabletListScreen extends Screen {

    public static BlockPos selectedTarget = null;

    public TabletListScreen() {
        super(new StringTextComponent("GPS Планшет"));
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 50;

        if (GPSBeaconBlock.BEACONS.isEmpty()) {
            this.addButton(new Button(centerX - 60, startY + 60, 120, 20, new StringTextComponent("Закрыть"), b -> this.onClose()));
            return;
        }

        int index = 0;
        for (BlockPos beaconPos : GPSBeaconBlock.BEACONS) {
            if (index >= 5) break;

            String label = "Маяк #" + (index + 1) + " [" + beaconPos.getX() + ", " + beaconPos.getZ() + "]";
            int yPos = startY + (index * 25);

            this.addButton(new Button(centerX - 80, yPos, 160, 20, new StringTextComponent(label), b -> {
                selectedTarget = beaconPos;
                if (this.minecraft.player != null) {
                    RoadPathfinder.calculatePath(this.minecraft.level, this.minecraft.player.blockPosition(), beaconPos);
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

        fill(matrixStack, centerX - 110, centerY - 80, centerX + 110, centerY + 90, 0xFF1A1A1A);

        if (GPSBeaconBlock.BEACONS.isEmpty()) {
            drawCenteredString(matrixStack, this.font, "Маяки не найдены!", centerX, centerY - 20, 0xFF5555);
            drawCenteredString(matrixStack, this.font, "Установите 'GPS Маяк' в мире.", centerX, centerY, 0xAAAAAA);
        } else {
            drawCenteredString(matrixStack, this.font, "📱 Выберите точку назначения:", centerX, centerY - 70, 0x55FFFF);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}