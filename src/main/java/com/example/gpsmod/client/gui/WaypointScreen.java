package com.example.gpsmod.client.gui;

import com.example.gpsmod.network.ModNetwork;
import com.example.gpsmod.network.SaveWaypointPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaypointScreen extends Screen {
    private final BlockPos targetPos;
    private TextFieldWidget nameInput;

    public WaypointScreen(BlockPos pos) {
        super(new StringTextComponent("Создание метки"));
        this.targetPos = pos;
    }

    @Override
    protected void init() {
        super.init();
        
        // Поле ввода текста
        this.nameInput = new TextFieldWidget(
                this.font, 
                this.width / 2 - 100, 
                this.height / 2 - 20, 
                200, 20, 
                new StringTextComponent("Имя метки")
        );
        this.nameInput.setMaxLength(32);
        this.children.add(this.nameInput);

        // Кнопка сохранения
        this.addButton(new Button(
                this.width / 2 - 100, 
                this.height / 2 + 10, 
                200, 20, 
                new StringTextComponent("Сохранить"), 
                button -> saveAndClose()
        ));
    }

    private void saveAndClose() {
        String text = this.nameInput.getValue().trim();
        if (!text.isEmpty()) {
            // Безопасная отправка пакета на сервер
            ModNetwork.CHANNEL.sendToServer(new SaveWaypointPacket(targetPos, text));
            this.onClose(); // Закрываем GUI безопасно
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.nameInput.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}