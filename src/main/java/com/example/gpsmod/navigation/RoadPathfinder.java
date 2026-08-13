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
        List<BlockPos> points = new ArrayList<>();
        points.add(start);

        boolean startHasRoad = hasIronRoadBelow(world, start);
        boolean targetHasRoad = hasIronRoadBelow(world, target);

        points.add(target);

        // Если под точками нет железных блоков — переключается на БЕЗДОРОЖЬЕ
        boolean isOffroad = !(startHasRoad || targetHasRoad);

        return new PathResult(points, isOffroad);
    }

    private static boolean hasIronRoadBelow(World world, BlockPos pos) {
        for (int dy = 0; dy <= 5; dy++) {
            if (world.getBlockState(pos.below(dy)).is(Blocks.IRON_BLOCK)) {
                return true;
            }
        }
        return false;
    }
}