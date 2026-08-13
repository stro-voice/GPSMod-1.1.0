package com.example.gpsmod.client;

import com.example.gpsmod.item.ModItems;
import com.example.gpsmod.item.PhoneItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientHUDOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return;

        // Проверяем: лежит ли Телефон во ВТОРОЙ РУКЕ (Offhand)
        ItemStack offhandStack = player.getOffhandItem();
        if (offhandStack.getItem() == ModItems.PHONE.get()) {
            BlockPos target = PhoneItem.getSelectedBeacon(offhandStack);
            if (target != null) {
                MatrixStack matrixStack = event.getMatrixStack();
                
                double dx = target.getX() - player.getX();
                double dz = target.getZ() - player.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);

                // Рисуем рамку навигатора в левом верхнем углу
                AbstractGui.fill(matrixStack, 10, 10, 160, 45, 0xDD000000);
                AbstractGui.fill(matrixStack, 12, 12, 158, 43, 0xFF112233);

                mc.font.draw(matrixStack, "📍 GPS Навигатор", 18, 16, 0x55FFFF);
                mc.font.draw(matrixStack, "Цель: [" + target.getX() + ", " + target.getZ() + "]", 18, 26, 0xFFFFFF);
                mc.font.draw(matrixStack, "Дистанция: " + (int)distance + " м.", 18, 34, 0x55FF55);
            }
        }
    }
}