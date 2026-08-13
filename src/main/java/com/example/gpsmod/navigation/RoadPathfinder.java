package com.gpsmod.navigation;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class RoadPathfinder {

    // Сканируем 4 блока вниз по оси Y под асфальтом/травой
    private static final int SCAN_DEPTH = 4;

    public static BlockPos findRoadBlockUnder(World world, BlockPos surfacePos) {
        if (world == null || surfacePos == null) return null;
        for (int dy = 0; dy <= SCAN_DEPTH; dy++) {
            BlockPos checkPos = surfacePos.down(dy);
            if (world.getBlockState(checkPos).getBlock() == Blocks.IRON_BLOCK) {
                return checkPos;
            }
        }
        return null;
    }

    public static List<BlockPos> findPath(World world, BlockPos start, BlockPos target) {
        BlockPos startRoad = findRoadBlockUnder(world, start);
        BlockPos targetRoad = findRoadBlockUnder(world, target);

        if (startRoad == null || targetRoad == null) return Collections.emptyList();

        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();

        queue.add(startRoad);
        cameFrom.put(startRoad, null);

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            if (current.equals(targetRoad)) {
                return reconstructPath(cameFrom, current);
            }

            for (int[] dir : directions) {
                // Проверяем перепад высот дороги в пределах +-1 блока
                for (int dy = -1; dy <= 1; dy++) {
                    BlockPos neighborSurface = current.add(dir[0], dy, dir[1]);
                    BlockPos neighborRoad = findRoadBlockUnder(world, neighborSurface);

                    if (neighborRoad != null && !cameFrom.containsKey(neighborRoad)) {
                        queue.add(neighborRoad);
                        cameFrom.put(neighborRoad, current);
                        break;
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        List<BlockPos> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}