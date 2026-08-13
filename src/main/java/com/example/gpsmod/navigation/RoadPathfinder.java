package com.example.gpsmod.navigation;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;

public class RoadPathfinder {

    public static class PathResult {
        public List<BlockPos> pathPoints;
        public boolean isOffroad;

        public PathResult(List<BlockPos> pathPoints, boolean isOffroad) {
            this.pathPoints = pathPoints;
            this.isOffroad = isOffroad;
        }
    }

    public static PathResult calculatePath(World world, BlockPos start, BlockPos target) {
        List<BlockPos> points = new ArrayList<>();
        points.add(start);

        // Проверяем наличие дорог из железных блоков
        boolean hasRoad = false;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (world.getBlockState(start.offset(x, -1, z)).is(Blocks.IRON_BLOCK)) {
                    hasRoad = true;
                    break;
                }
            }
        }

        points.add(target);
        return new PathResult(points, !hasRoad);
    }
}