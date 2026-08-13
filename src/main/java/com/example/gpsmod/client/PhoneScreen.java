package com.example.gpsmod.client;

import com.example.gpsmod.item.PhoneItem;
import com.example.gpsmod.tracker.IronBeaconTracker;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class PhoneScreen extends Screen {
    private final ItemStack phoneStack;

    public PhoneScreen(ItemStack phoneStack) {
        super(new StringTextComponent("GPS Смартфон"));
        this.phoneStack = phoneStack;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 40;

        if (IronBeaconTracker.IRON_BEACONS.isEmpty()) {
            this.addButton(new Button(centerX - 60, startY + 50, 120, 20, new StringTextComponent("Закрыть"), b -> this.onClose()));
            return;
        }

        int index = 0;
        for (BlockPos pos : IronBeaconTracker.IRON_BEACONS) {
            if (index >= 5) break;

            String label = "🧱 Блок Железа #" + (index + 1) + " [" + pos.getX() + ", " + pos.getZ() + "]";
            int yPos = startY + (index * 22);

            this.addButton(new Button(centerX - 90, yPos, 180, 20, new StringTextComponent(label), b -> {
                PhoneItem.setSelectedBeacon(this.phoneStack, pos);
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

        fill(matrixStack, centerX - 105, centerY - 85, centerX + 105, centerY + 85, 0xFF1E1E24);
        fill(matrixStack, centerX - 100, centerY - 80, centerX + 100, centerY + 80, 0xFF2B2D42);

        if (IronBeaconTracker.IRON_BEACONS.isEmpty()) {
            drawCenteredString(matrixStack, this.font, "Железные блоки не найдены!", centerX, centerY - 10, 0xFF5555);
        } else {
            drawCenteredString(matrixStack, this.font, "📱 Выберите Маяк (Блок Железа):", centerX, centerY - 70, 0x8D99AE);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}