package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class MapBeaconScreen extends Screen {

    // ПРАВИЛЬНЫЙ КОНСТРУКТОР (без параметров)
    public MapBeaconScreen() {
        super(new StringTextComponent("GPS Меню"));
    }

    @Override
    protected void init() {
        super.init();
        
        // Кнопка сброса маршрута
        this.addButton(new net.minecraft.client.gui.widget.button.Button(
            this.width / 2 - 60, this.height / 2 + 40, 120, 20,
            new StringTextComponent("Сбросить маршрут"),
            (button) -> {
                // Отправляем null для сброса активной цели
                com.example.gpsmod.network.PacketHandler.sendToServer(
                    new com.example.gpsmod.network.C2SSelectBeaconPacket(null)
                );
                this.onClose();
            }
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "Выбор точки навигации", this.width / 2, 20, 0xFFFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}