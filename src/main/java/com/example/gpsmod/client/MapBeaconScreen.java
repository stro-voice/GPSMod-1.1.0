package com.example.gpsmod.client;

import com.example.gpsmod.network.C2SSelectBeaconPacket;
import com.example.gpsmod.network.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class MapBeaconScreen extends Screen {
    private final List<BlockPos> beacons;

    public MapBeaconScreen(List<BlockPos> beacons) {
        super(new StringTextComponent("GPS Маяки"));
        this.beacons = beacons;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2 - 50;

        int i = 0;
        for (BlockPos pos : beacons) {
            if (i >= 5) break;
            this.addButton(new Button(cx - 90, cy + (i * 22), 180, 20,
                    new StringTextComponent("🚩 Маяк [" + pos.getX() + ", " + pos.getZ() + "]"),
                    b -> {
                        PacketHandler.CHANNEL.sendToServer(new C2SSelectBeaconPacket(pos));
                        this.onClose();
                    }));
            i++;
        }

        this.addButton(new Button(cx - 90, cy + (i * 22) + 10, 180, 20,
                new StringTextComponent("❌ Сбросить цель"),
                b -> {
                    PacketHandler.CHANNEL.sendToServer(new C2SSelectBeaconPacket(null));
                    this.onClose();
                }));
    }

    @Override
    public void render(MatrixStack ms, int mx, int my, float pt) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, "Выберите Флаг-Цель:", this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        super.render(ms, mx, my, pt);
    }
}