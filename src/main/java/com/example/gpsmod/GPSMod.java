package com.example.gpsmod;

import com.example.gpsmod.block.ModBlocks;
import com.example.gpsmod.item.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GPSMod.MOD_ID)
public class GPSMod {
    public static final String MOD_ID = "gpsmod";

    public GPSMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Регистрируем блоки и предметы в Forge
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
    }
}