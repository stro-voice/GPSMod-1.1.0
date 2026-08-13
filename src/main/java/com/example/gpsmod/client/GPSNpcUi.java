package com.example.gpsmod.client;

import com.example.gpsmod.GPSManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class GPSNpcUi extends Screen {

    private final int xSize = 200;
    private final int ySize = 120;
    private int guiLeft;
    private int guiTop;

    public GPSNpcUi() {
        super(new StringTextComponent("Route Advisor"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        // Кнопка переключения режима маршрута (Только по железу / Везде)
        this.addButton(new Button(this.guiLeft + 15, this.guiTop + 45, 170, 20, 
            new StringTextComponent("Режим: Железные блоки"), (button) -> {
                boolean current = GPSManager.getInstance().isIronOnlyMode();
                GPSManager.getInstance().setIronOnlyMode(!current);
                button.setMessage(new StringTextComponent(
                    !current ? "Режим: Только Железо" : "Режим: Все блоки"
                ));
            }));

        // Кнопка сброса маршрута
        this.addButton(new Button(this.guiLeft + 15, this.guiTop + 75, 170, 20, 
            new StringTextComponent("Сбросить маршрут"), (button) -> {
                GPSManager.getInstance().clearPath();
                this.closeScreen();
            }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        // Рисуем полу-3D плашку в стиле Route Advisor из ETS 2 (без сторонних PNG)
        // Тёмный фон
        fill(matrixStack, guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xEE1E1E1E);
        // Границы с эффектом объёма (3D)
        fill(matrixStack, guiLeft, guiTop, guiLeft + xSize, guiTop + 2, 0xFF555555);
        fill(matrixStack, guiLeft, guiTop, guiLeft + 2, guiTop + ySize, 0xFF555555);
        fill(matrixStack, guiLeft, guiTop + ySize - 2, guiLeft + xSize, guiTop + ySize, 0xFF111111);
        fill(matrixStack, guiLeft + xSize - 2, guiTop, guiLeft + xSize, guiTop + ySize, 0xFF111111);

        // Шапка окна (ETS 2 Style)
        fill(matrixStack, guiLeft + 2, guiTop + 2, guiLeft + xSize - 2, guiTop + 25, 0xFF2A2A2A);
        this.font.drawString(matrixStack, "Mobile Route Advisor", guiLeft + 10, guiTop + 9, 0xFFFFA500);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}