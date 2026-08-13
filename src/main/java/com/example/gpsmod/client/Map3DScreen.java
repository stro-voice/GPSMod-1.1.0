package com.example.gpsmod.client;

import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class Map3DScreen extends Screen {

    private float yaw = 45.0f;
    private float pitch = 30.0f;
    private float zoom = 40.0f;

    public Map3DScreen() {
        super(new StringTextComponent("3D Map Navigator"));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 1) { // ПКМ вращение
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
        this.zoom = Math.max(10.0f, Math.min(100.0f, this.zoom));
        return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        int cx = this.width / 2;
        int cy = this.height / 2;

        fill(matrixStack, cx - 180, cy - 110, cx + 180, cy + 110, 0xEE0A0A0A);
        fill(matrixStack, cx - 178, cy - 108, cx + 178, cy + 108, 0xFF151515);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            BlockPos pPos = mc.player.blockPosition();

            int range = (int) (1000 / zoom);
            for (int x = -range; x <= range; x += 2) {
                for (int z = -range; z <= range; z += 2) {
                    BlockPos checkPos = pPos.offset(x, 0, z);
                    
                    if (GPSManager.isRoadBlock(mc.level, checkPos)) {
                        int renderX = cx + (int) (x * (zoom / 15.0f));
                        int renderY = cy + (int) (z * (zoom / 15.0f));

                        if (renderX > cx - 170 && renderX < cx + 170 && renderY > cy - 100 && renderY < cx + 100) {
                            fill(matrixStack, renderX - 1, renderY - 1, renderX + 1, renderY + 1, 0xFF555555);
                        }
                    }
                }
            }

            List<BlockPos> path = GPSManager.getInstance().getCurrentPath();
            if (path != null && !path.isEmpty()) {
                for (BlockPos pathPos : path) {
                    int rx = cx + (int) ((pathPos.getX() - pPos.getX()) * (zoom / 15.0f));
                    int ry = cy + (int) ((pathPos.getZ() - pPos.getZ()) * (zoom / 15.0f));

                    if (rx > cx - 170 && rx < cx + 170 && ry > cy - 100 && ry < cx + 100) {
                        fill(matrixStack, rx - 2, ry - 2, rx + 2, ry + 2, 0xFF00FF00);
                    }
                }
            }

            fill(matrixStack, cx - 3, cy - 3, cx + 3, cy + 3, 0xFF00AAFF);
        }

        if (this.font != null) {
            this.font.draw(matrixStack, "3D Карта трассы (включая скрытые блоки под модовым покрытием)", cx - 170, cy - 102, 0xFFFFFF);
            this.font.draw(matrixStack, "ПКМ — Вращение | Колесико — Масштаб", cx - 170, cy + 95, 0x888888);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}