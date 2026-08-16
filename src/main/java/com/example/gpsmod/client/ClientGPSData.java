package com.example.gpsmod.client;

import net.minecraft.util.math.BlockPos;

public class ClientGPSData {
    public static BlockPos targetPos = null;
    public static String targetName = "";

    public static void setTarget(BlockPos pos, String name) {
        targetPos = pos;
        targetName = name;
    }

    public static void clearTarget() {
        targetPos = null;
        targetName = "";
    }
}