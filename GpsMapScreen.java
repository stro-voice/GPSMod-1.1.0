package com.example.gpsmod.client;

import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class GpsMapScreen extends Screen {

    private double mapOffsetX = 0;
    private double mapOffsetZ = 0;
    private float zoom = 2.0f;

    private boolean isDragging = false;
    private BlockPos selectedPos = null;

    public GpsMapScreen() {
        super(new StringTextComponent("GPS Интерактивная карта"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int bottomY = this.height - 30;

        this.addButton(new Button(centerX - 105, bottomY, 100, 20, new StringTextComponent("Поехать"), button -> {
            if (selectedPos != null) {
                GPSManager.setTarget(selectedPos);
                this.onClose();
            }
        }));

        this.addButton(new Button(centerX + 5, bottomY, 100, 20, new StringTextComponent("Сбросить"), button -> {
            GPSManager.clearTarget();
            this.selectedPos = null;
            this.onClose();
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        World world = mc.level;
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        Vector3d playerPos = mc.player.position();

        int step = (int) Math.max(1, 4 / zoom);
        for (int screenX = 0; screenX < this.width; screenX += step * zoom) {
            for (int screenY = 0; screenY < this.height; screenY += step * zoom) {
                double worldX = playerPos.x + (screenX - centerX) / zoom - mapOffsetX;
                double worldZ = playerPos.z + (screenY - centerY) / zoom - mapOffsetZ;

                BlockPos topPos = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(worldX, 0, worldZ));
                BlockState state = world.getBlockState(topPos.below());

                int color = getBlockTerrainColor(state);
                fill(matrixStack, screenX, screenY, (int) (screenX + step * zoom), (int) (screenY + step * zoom), color);
            }
        }

        if (selectedPos != null) {
            int pinX = (int) (centerX + (selectedPos.getX() - playerPos.x + mapOffsetX) * zoom);
            int pinZ = (int) (centerY + (selectedPos.getZ() - playerPos.z + mapOffsetZ) * zoom);

            fill(matrixStack, pinX - 6, pinZ - 1, pinX + 6, pinZ + 1, 0xFF00FF00);
            fill(matrixStack, pinX - 1, pinZ - 6, pinX + 1, pinZ + 6, 0xFF00FF00);

            String text = String.format("Цель: X: %d, Z: %d", selectedPos.getX(), selectedPos.getZ());
            drawCenteredString(matrixStack, this.font, text, pinX, pinZ - 15, 0x55FF55);
        }

        int playerScreenX = (int) (centerX + mapOffsetX * zoom);
        int playerScreenZ = (int) (centerY + mapOffsetZ * zoom);
        fill(matrixStack, playerScreenX - 4, playerScreenZ - 4, playerScreenX + 4, playerScreenZ + 4, 0xFFFF0000);
        drawCenteredString(matrixStack, this.font, "Вы", playerScreenX, playerScreenZ - 12, 0xFFFFFF);

        drawCenteredString(matrixStack, this.font, "Кликните ЛКМ по карте для выбора точки направления", centerX, 15, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private int getBlockTerrainColor(BlockState state) {
        Material mat = state.getMaterial();
        if (mat == Material.WATER) return 0xDD2B5D9B;
        if (mat == Material.GRASS) return 0xDD5D9B42;
        if (mat == Material.SAND) return 0xDDD2C289;
        if (mat == Material.STONE) return 0xDD737373;
        if (mat == Material.DIRT) return 0xDD866043;
        if (mat == Material.LEAVES) return 0xDD386821;
        if (mat == Material.ICE) return 0xDDA0C8F0;
        return 0xDD444444;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY < this.height - 40 && button == 0) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                int centerX = this.width / 2;
                int centerY = this.height / 2;

                double worldX = mc.player.getX() + (mouseX - centerX) / zoom - mapOffsetX;
                double worldZ = mc.player.getZ() + (mouseY - centerY) / zoom - mapOffsetZ;
                int targetY = (int) mc.player.getY();

                this.selectedPos = new BlockPos((int) Math.floor(worldX), targetY, (int) Math.floor(worldZ));
                return true;
            }
        }

        if (button == 0) {
            this.isDragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging) {
            this.mapOffsetX += dragX / zoom;
            this.mapOffsetZ += dragY / zoom;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0 && zoom < 8.0f) {
            zoom *= 1.2f;
        } else if (delta < 0 && zoom > 0.5f) {
            zoom /= 1.2f;
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}