package com.example.gpsmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(GPSMod.MOD_ID)
public class GPSMod {
    public static final String MOD_ID = "gpsmod";

    public GPSMod() {
        MinecraftForge.EVENT_BUS.register(this);

        // Безопасный запуск клиентской логики только на клиенте
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSetup::init);
    }
}