package com.example.gpsmod.navigation;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RoadPathfinder {

    public static class PathResult {
        public List<BlockPos> points;
        public boolean isOffroad;

        public PathResult(List<BlockPos> points, boolean isOffroad) {
            this.points = points;
            this.isOffroad = isOffroad;
        }
    }

    public static PathResult calculatePath(World world, BlockPos start, BlockPos target) {
        List<BlockPos> path = new ArrayList<>();
        path.add(start);

        boolean hasRoad = isIronRoadBelow(world, start) || isIronRoadBelow(world, target);

        if (hasRoad) {
            // Маршрут по железной дороге
            path.add(target);
            return new PathResult(path, false);
        } else {
            // Режим БЕЗДОРОЖЬЕ
            path.add(target);
            return new PathResult(path, true);
        }
    }

    private static boolean isIronRoadBelow(World world, BlockPos pos) {
        for (int dy = 0; dy <= 4; dy++) {
            if (world.getBlockState(pos.below(dy)).getBlock() == Blocks.IRON_BLOCK) {
                return true;
            }
        }
        return false;
    }
}