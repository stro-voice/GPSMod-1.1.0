package com.example.gpsmod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DynamicMapRenderer {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == Items.FILLED_MAP) {
            MapData mapData = FilledMapItem.getSavedData(stack, mc.level);
            if (mapData != null) {
                mapData.x = (int) player.getX();
                mapData.z = (int) player.getZ();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == Items.FILLED_MAP && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.getBoolean("HasGPS")) {
                int targetX = tag.getInt("TargetX");
                int targetZ = tag.getInt("TargetZ");

                Minecraft mc = Minecraft.getInstance();
                PlayerEntity player = mc.player;
                if (player == null) return;

                MatrixStack ms = event.getMatrixStack();
                double dx = targetX - player.getX();
                double dz = targetZ - player.getZ();
                int dist = (int) Math.sqrt(dx * dx + dz * dz);

                mc.font.draw(ms, "🎯 До флага: " + dist + "м.", 10, 10, 0x00FF00);
            }
        }
    }
}