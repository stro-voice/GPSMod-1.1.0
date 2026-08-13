package com.example.gpsmod.item;

import com.example.gpsmod.GPSMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GPSMod.MOD_ID);

    public static final RegistryObject<Item> PHONE = ITEMS.register("phone", 
            () -> new PhoneItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));
}