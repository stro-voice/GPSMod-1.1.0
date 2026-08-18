package com.example.gpsmod.client;

import com.example.gpsmod.network.ModNetwork;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MapBeaconScreen extends Screen {
    
    public MapBeaconScreen() {
        super(new StringTextComponent("Карта меток"));
    }

    @Override
    protected void init() {
        super.init();
        
        // Строка 42: Проверьте, что кнопка использует this.font и this.width/this.height
        this.addButton(new Button(
            this.width / 2 - 100, 
            this.height / 2 + 50, 
            200, 20, 
            new StringTextComponent("Закрыть"), 
            button -> this.onClose()
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        // Строка 56: Отрисовка текста должна использовать this.font
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}