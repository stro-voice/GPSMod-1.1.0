package com.example.gpsmod.client;

import com.example.gpsmod.GPSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        // Открываем экран выбора флагов при ПКМ с Компасом
        if (event.getItemStack().getItem() == Items.COMPASS) {
            if (event.getWorld().isClientSide()) {
                Minecraft.getInstance().setScreen(new MapBeaconScreen());
            }
        }
    }
}