package com.example.gpsmod.client;

import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class MapScreen extends Screen {

    public enum SubMenu {
        MAIN,
        VIEW_2D_MAP,
        SET_ROUTE
    }

    public static SubMenu currentSubMenu = SubMenu.MAIN;
    public static int selectedIndex = 0; // 0: Карта, 1: Маршрут, 2: Назад
    
    public static BlockPos targetPos = null;
    public static boolean isOffroad = false;

    public MapScreen() {
        super(new StringTextComponent("GPS Navigator Menu"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_0) {
            if (currentSubMenu != SubMenu.MAIN) {
                currentSubMenu = SubMenu.MAIN;
            } else {
                this.onClose();
            }
            return true;
        }

        if (currentSubMenu == SubMenu.MAIN) {
            if (keyCode == GLFW.GLFW_KEY_MINUS) { // Навигация клавишей [-]
                selectedIndex = (selectedIndex - 1 + 3) % 3;
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_EQUAL) { // Подтверждение клавишей [=]
                executeMenuAction();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void executeMenuAction() {
        if (selectedIndex == 0) {
            currentSubMenu = SubMenu.VIEW_2D_MAP;
        } else if (selectedIndex == 1) {
            currentSubMenu = SubMenu.SET_ROUTE;
        } else if (selectedIndex == 2) {
            this.onClose();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (currentSubMenu == SubMenu.MAIN) {
            drawCenteredString(matrixStack, this.font, "=== GPS НАВИГАТОР ===", centerX, centerY - 60, 0x00FF00);
            drawCenteredString(matrixStack, this.font, "[-] Выбор | [=] Подтвердить | [0] Назад", centerX, centerY - 45, 0x888888);

            String item1 = (selectedIndex == 0 ? "> 1. КАРТА <" : "1. Карта");
            String item2 = (selectedIndex == 1 ? "> 2. МАРШРУТ <" : "2. Маршрут");
            String item3 = (selectedIndex == 2 ? "> 3. НАЗАД <" : "3. Назад");

            drawCenteredString(matrixStack, this.font, item1, centerX, centerY - 10, selectedIndex == 0 ? 0xFFFF00 : 0xFFFFFF);
            drawCenteredString(matrixStack, this.font, item2, centerX, centerY + 10, selectedIndex == 1 ? 0xFFFF00 : 0xFFFFFF);
            drawCenteredString(matrixStack, this.font, item3, centerX, centerY + 30, selectedIndex == 2 ? 0xFFFF00 : 0xFFFFFF);

        } else if (currentSubMenu == SubMenu.VIEW_2D_MAP || currentSubMenu == SubMenu.SET_ROUTE) {
            ResourceLocation mapTex = WorldScanner.updateAndGetMapTexture();
            this.minecraft.getTextureManager().bind(mapTex);

            int mapSize = 180;
            int mapX = centerX - mapSize / 2;
            int mapY = centerY - mapSize / 2;

            fill(matrixStack, mapX - 2, mapY - 2, mapX + mapSize + 2, mapY + mapSize + 2, 0xFFFFFFFF);
            blit(matrixStack, mapX, mapY, 0, 0, mapSize, mapSize, mapSize, mapSize);

            fill(matrixStack, centerX - 3, centerY - 3, centerX + 3, centerY + 3, 0xFF00FF00);

            if (currentSubMenu == SubMenu.SET_ROUTE) {
                drawCenteredString(matrixStack, this.font, "Кликните ЛКМ по карте для выбора маршрута", centerX, mapY - 15, 0x55FFFF);
                if (targetPos != null) {
                    String modeText = isOffroad ? "Статус: БЕЗДОРОЖЬЕ (нет iron_block)" : "Статус: Маршрут по ДОРОГЕ";
                    drawCenteredString(matrixStack, this.font, modeText, centerX, mapY + mapSize + 10, isOffroad ? 0xFF5555 : 0x55FF55);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentSubMenu == SubMenu.SET_ROUTE && button == 0 && this.minecraft != null && this.minecraft.player != null) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int mapSize = 180;

            int mapX1 = centerX - mapSize / 2;
            int mapY1 = centerY - mapSize / 2;
            int mapX2 = mapX1 + mapSize;
            int mapY2 = mapY1 + mapSize;

            if (mouseX >= mapX1 && mouseX <= mapX2 && mouseY >= mapY1 && mouseY <= mapY2) {
                double relX = ((mouseX - mapX1) / (double) mapSize - 0.5) * (WorldScanner.RADIUS * 2);
                double relZ = ((mouseY - mapY1) / (double) mapSize - 0.5) * (WorldScanner.RADIUS * 2);

                BlockPos playerPos = this.minecraft.player.blockPosition();
                targetPos = new BlockPos(playerPos.getX() + relX, playerPos.getY(), playerPos.getZ() + relZ);

                RoadPathfinder.PathResult result = RoadPathfinder.calculatePath(this.minecraft.level, playerPos, targetPos);
                isOffroad = result.isOffroad;

                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}