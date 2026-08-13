package com.example.gpsmod.client;

import com.example.gpsmod.navigation.RoadPathfinder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class MapScreen extends Screen {

    public enum MenuState {
        MAIN_MENU,
        VIEW_MAP,
        SET_ROUTE
    }

    public static MenuState currentState = MenuState.MAIN_MENU;
    public static BlockPos targetPos = null;
    public static boolean isOffroadMode = false;

    public MapScreen() {
        super(new StringTextComponent("GPS Menu"));
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        int centerX = this.width / 2 - 100;
        int centerY = this.height / 2 - 40;

        if (currentState == MenuState.MAIN_MENU) {
            // 3 Раздела Главного меню
            this.addButton(new Button(centerX, centerY, 200, 20, new StringTextComponent("1. Карта"), b -> currentState = MenuState.VIEW_MAP));
            this.addButton(new Button(centerX, centerY + 25, 200, 20, new StringTextComponent("2. Маршрут"), b -> currentState = MenuState.SET_ROUTE));
            this.addButton(new Button(centerX, centerY + 50, 200, 20, new StringTextComponent("3. Назад"), b -> this.onClose()));
        } else {
            // Кнопка возврата в меню
            this.addButton(new Button(10, 10, 80, 20, new StringTextComponent("< Меню"), b -> {
                currentState = MenuState.MAIN_MENU;
                this.init();
            }));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (currentState == MenuState.MAIN_MENU) {
            drawCenteredString(matrixStack, this.font, "--- НАВИГАТОР ---", this.width / 2, this.height / 2 - 70, 0x00FF00);
        } else if (currentState == MenuState.VIEW_MAP) {
            drawCenteredString(matrixStack, this.font, "Просмотр 2D Карты местности", this.width / 2, 20, 0xFFFFFF);
        } else if (currentState == MenuState.SET_ROUTE) {
            drawCenteredString(matrixStack, this.font, "Кликните на карту для прокладки маршрута", this.width / 2, 20, 0x55FF55);
            if (targetPos != null) {
                String status = isOffroadMode ? "Статус: БЕЗДОРОЖЬЕ!" : "Статус: По дороге";
                drawCenteredString(matrixStack, this.font, status, this.width / 2, 40, isOffroadMode ? 0xFF5555 : 0x55FFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentState == MenuState.SET_ROUTE && button == 0 && this.minecraft != null && this.minecraft.player != null) {
            BlockPos playerPos = this.minecraft.player.blockPosition();
            targetPos = playerPos.north(60); // Задание точки вперед

            RoadPathfinder.PathResult res = RoadPathfinder.calculatePath(this.minecraft.level, playerPos, targetPos);
            isOffroadMode = res.isOffroad;

            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}