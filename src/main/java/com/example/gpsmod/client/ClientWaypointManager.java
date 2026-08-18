package com.example.gpsmod.client;

import net.minecraft.util.math.BlockPos;

public class ClientWaypointManager {
    private static BlockPos targetPos = null;
    private static String targetName = "";
    private static boolean hasTarget = false;

    public static void setTarget(BlockPos pos, String name) {
        targetPos = pos;
        targetName = name;
        hasTarget = true;
    }

    public static void clearTarget() {
        targetPos = null;
        targetName = "";
        hasTarget = false;
    }

    public static BlockPos getTargetPos() { return targetPos; }
    public static String getTargetName() { return targetName; }
    public static boolean hasTarget() { return hasTarget; }
}