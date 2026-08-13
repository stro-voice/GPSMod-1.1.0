package com.example.gpsmod;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import java.util.ArrayList;
import java.util.List;

public class GPSManager {
    private static BlockPos targetPos = null;
    private static boolean active = false;

    public static void setTarget(BlockPos pos) {
        targetPos = pos;
        active = true;
    }

    public static void clearTarget() {
        targetPos = null;
        active = false;
    }

    public static boolean isActive() {
        return active && targetPos != null;
    }

    public static BlockPos getTargetPos() {
        return targetPos;
    }

    public static double getDistanceToTarget(Vector3d currentPos) {
        if (targetPos == null) return 0;
        return currentPos.distanceTo(new Vector3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5));
    }

    public static List<Vector3d> getCurrentPath(Vector3d currentPos) {
        List<Vector3d> path = new ArrayList<>();
        if (!active || targetPos == null) return path;

        path.add(currentPos);
        Vector3d targetVec = new Vector3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);

        int steps = 20;
        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            path.add(currentPos.scale(1 - t).add(targetVec.scale(t)));
        }
        return path;
    }

    public static String getTurnInstruction(float playerYaw, Vector3d playerPos) {
        if (!active || targetPos == null) return "";

        double dx = targetPos.getX() + 0.5 - playerPos.x;
        double dz = targetPos.getZ() + 0.5 - playerPos.z;

        double targetAngle = Math.toDegrees(Math.atan2(-dx, dz));
        double diff = (targetAngle - playerYaw) % 360;
        if (diff < -180) diff += 360;
        if (diff > 180) diff -= 360;

        if (Math.abs(diff) < 25) {
            return "↑ Прямо";
        } else if (diff >= 25 && diff < 115) {
            return "↱ Поверните направо";
        } else if (diff <= -25 && diff > -115) {
            return "↰ Поверните налево";
        } else {
            return "↶ Развернитесь";
        }
    }
}