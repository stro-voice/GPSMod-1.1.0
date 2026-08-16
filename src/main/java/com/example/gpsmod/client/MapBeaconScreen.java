package com.example.gpsmod.client;

import com.example.gpsmod.network.C2SSelectBeaconPacket;
import com.example.gpsmod.network.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collections;
import java.util.List;

public class MapBeaconScreen extends Screen {

    private final List<BlockPos> beacons;

    // Основной конструктор: принимает список флагов от сервера
    public MapBeaconScreen(List<BlockPos> beacons) {
        super(new StringTextComponent("GPS Меню"));
        this.beacons = beacons != null ? beacons : Collections.emptyList();
    }

    // Дополнительный конструктор по умолчанию
    public MapBeaconScreen() {
        this(Collections.emptyList());
    }

    @Override
    protected void init() {
        super.init();

        int y = 40;
        // Создаем кнопки для каждого сохраненного флага
        for (BlockPos pos : this.beacons) {
            this.addButton(new Button(
                this.width / 2 - 100, y, 200, 20,
                new StringTextComponent("🚩 X: " + pos.getX() + " | Y: " + pos.getY() + " | Z: " + pos.getZ()),
                (button) -> {
                    PacketHandler.sendToServer(new C2SSelectBeaconPacket(pos));
                    this.onClose();
                }
            ));
            y += 24;
            if (y > this.height - 60) break; // Защита от выхода за границы экрана
        }

        // Кнопка сброса маршрута
        this.addButton(new Button(
            this.width / 2 - 60, this.height - 35, 120, 20,
            new StringTextComponent("Сбросить маршрут"),
            (button) -> {
                PacketHandler.sendToServer(new C2SSelectBeaconPacket(null));
                this.onClose();
            }
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "Выберите флаг для навигации", this.width / 2, 15, 0xFFFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}