package com.example.gpsmod.item;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.block.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GPSMod.MOD_ID);

    // 1. Предмет Телефон -> попадает в вкладку "Разное"
    public static final RegistryObject<Item> PHONE = ITEMS.register("phone", 
            () -> new PhoneItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));

    // 2. Предмет для размещения Блока-Маяка -> попадает в вкладку "Разное"
    public static final RegistryObject<Item> GPS_BEACON_ITEM = ITEMS.register("gps_beacon", 
            () -> new BlockItem(ModBlocks.GPS_BEACON.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));
}