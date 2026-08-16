package com.example.gpsmod.client;

import com.example.gpsmod.network.C2SRegisterBeaconPacket;
import com.example.gpsmod.network.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class FlagNameScreen extends Screen {
    private final BlockPos pos;
    private TextFieldWidget nameField;

    public FlagNameScreen(BlockPos pos) {
        super(new StringTextComponent("Название флага"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.nameField = new TextFieldWidget(
            this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20,
            new StringTextComponent("Имя флага")
        );
        this.nameField.setMaxStringLength(24);
        this.nameField.setValue("Точка " + pos.getX() + ", " + pos.getZ());
        this.children.add(this.nameField);
        this.setFocusedDefault(this.nameField);

        this.addButton(new Button(
            this.width / 2 - 50, this.height / 2 + 20, 100, 20,
            new StringTextComponent("Сохранить"),
            (button) -> {
                String name = this.nameField.getText().trim();
                if (name.isEmpty()) name = "Флаг " + pos.getX() + ", " + pos.getZ();
                PacketHandler.sendToServer(new C2SRegisterBeaconPacket(pos, name));
                this.onClose();
            }
        ));
    }

    @Override
    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        super.onClose();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, "Введите название флага:", this.width / 2, this.height / 2 - 35, 0xFFFFFFFF);
        this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}