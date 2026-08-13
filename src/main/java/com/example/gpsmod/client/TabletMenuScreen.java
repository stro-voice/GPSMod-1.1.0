package com.example.gpsmod.client;

import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class TabletMenuScreen extends Screen {

    public enum Page { MENU, MAP_VIEW, ROUTE_SET }
    public static Page currentPage = Page.MENU;

    public static BlockPos targetPos = null;
    public static RoadPathfinder.PathResult currentPath = null;

    public TabletMenuScreen() {
        super(new StringTextComponent("Планшет-Навигатор"));
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (currentPage == Page.MENU) {
            // Главные кнопки меню
            this.addButton(new Button(centerX - 75, centerY - 40, 150, 20, new StringTextComponent("1. Карта"), b -> {
                currentPage = Page.MAP_VIEW;
                this.init();
            }));

            this.addButton(new Button(centerX - 75, centerY - 10, 150, 20, new StringTextComponent("2. Маршрут"), b -> {
                currentPage = Page.ROUTE_SET;
                this.init();
            }));

            this.addButton(new Button(centerX - 75, centerY + 20, 150, 20, new StringTextComponent("3. Выключить"), b -> {
                this.onClose();
            }));

        } else if (currentPage == Page.ROUTE_SET) {
            // Кнопка подтверждения маршрута
            this.addButton(new Button(centerX - 60, centerY + 100, 120, 20, new StringTextComponent("ПОДТВЕРДИТЬ"), b -> {
                if (targetPos != null && this.minecraft.player != null) {
                    currentPath = RoadPathfinder.calculatePath(this.minecraft.level, this.minecraft.player.blockPosition(), targetPos);
                    this.onClose(); // Закрываем планшет, включается 3D-HUD
                }
            }));

            this.addButton(new Button(centerX - 60, centerY + 125, 120, 20, new StringTextComponent("Назад"), b -> {
                currentPage = Page.MENU;
                this.init();
            }));

        } else if (currentPage == Page.MAP_VIEW) {
            this.addButton(new Button(centerX - 50, centerY + 100, 100, 20, new StringTextComponent("Назад"), b -> {
                currentPage = Page.MENU;
                this.init();
            }));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Корпус Планшета (Рамка)
        fill(matrixStack, centerX - 120, centerY - 140, centerX + 120, centerY + 155, 0xFF1E1E1E);
        fill(matrixStack, centerX - 115, centerY - 135, centerX + 115, centerY + 145, 0xFF0D0D0D);

        if (currentPage == Page.MENU) {
            drawCenteredString(matrixStack, this.font, "📱 GPS НАВИГАТОР", centerX, centerY - 100, 0x00FF00);
            drawCenteredString(matrixStack, this.font, "Выберите раздел мышкой:", centerX, centerY - 70, 0xAAAAAA);

        } else if (currentPage == Page.MAP_VIEW || currentPage == Page.ROUTE_SET) {
            // 2D Карта местности
            ResourceLocation mapTex = WorldScanner.updateAndGetMapTexture();
            this.minecraft.getTextureManager().bind(mapTex);

            int mapSize = 160;
            int mapX = centerX - mapSize / 2;
            int mapY = centerY - 80;

            fill(matrixStack, mapX - 2, mapY - 2, mapX + mapSize + 2, mapY + mapSize + 2, 0xFF555555);
            blit(matrixStack, mapX, mapY, 0, 0, mapSize, mapSize, mapSize, mapSize);

            // Метка игрока
            fill(matrixStack, centerX - 3, mapY + mapSize / 2 - 3, centerX + 3, mapY + mapSize / 2 + 3, 0xFF00FF00);

            // Если кликнули точку — рисуем красную маркер-цель
            if (targetPos != null && this.minecraft.player != null) {
                BlockPos p = this.minecraft.player.blockPosition();
                int dx = targetPos.getX() - p.getX();
                int dz = targetPos.getZ() - p.getZ();

                int markX = centerX + (int) ((double) dx / (WorldScanner.RADIUS * 2) * mapSize);
                int markY = (mapY + mapSize / 2) + (int) ((double) dz / (WorldScanner.RADIUS * 2) * mapSize);

                fill(matrixStack, markX - 4, markY - 4, markX + 4, markY + 4, 0xFFFF0000);
            }

            if (currentPage == Page.ROUTE_SET) {
                drawCenteredString(matrixStack, this.font, "Кликните ЛКМ по карте для точки", centerX, mapY - 15, 0x55FFFF);
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentPage == Page.ROUTE_SET && button == 0 && this.minecraft.player != null) {
            int centerX = this.width / 2;
            int mapSize = 160;
            int mapX1 = centerX - mapSize / 2;
            int mapY1 = this.height / 2 - 80;

            if (mouseX >= mapX1 && mouseX <= mapX1 + mapSize && mouseY >= mapY1 && mouseY <= mapY1 + mapSize) {
                double relX = ((mouseX - mapX1) / (double) mapSize - 0.5) * (WorldScanner.RADIUS * 2);
                double relZ = ((mouseY - mapY1) / (double) mapSize - 0.5) * (WorldScanner.RADIUS * 2);

                BlockPos playerPos = this.minecraft.player.blockPosition();
                targetPos = new BlockPos(playerPos.getX() + relX, playerPos.getY(), playerPos.getZ() + relZ);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}