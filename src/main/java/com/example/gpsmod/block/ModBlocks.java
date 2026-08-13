package com.example.gpsmod.block;

import com.example.gpsmod.GPSMod;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {

    public static final Block GPS_BEACON_BLOCK = new GPSBeaconBlock().setRegistryName(GPSMod.MOD_ID, "gps_beacon");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(GPS_BEACON_BLOCK);
    }

    // Регистрируем предмет блока в Креативную вкладку "Разное"
    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(
            new BlockItem(GPS_BEACON_BLOCK, new Item.Properties().tab(ItemGroup.TAB_MISC))
                .setRegistryName(GPS_BEACON_BLOCK.getRegistryName())
        );
    }
}