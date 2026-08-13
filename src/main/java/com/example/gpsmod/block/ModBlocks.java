package com.example.gpsmod.block;

import com.example.gpsmod.GPSMod;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GPSMod.MOD_ID);

    public static final RegistryObject<Block> GPS_BEACON = BLOCKS.register("gps_beacon", GPSBeaconBlock::new);
}