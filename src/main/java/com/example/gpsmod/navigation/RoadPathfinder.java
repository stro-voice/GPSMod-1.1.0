package com.example.gpsmod.navigation;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RoadPathfinder {

    // Проверяет, есть ли под указанными координатами железный блок (в пределах 4 блоков вниз)
    public static boolean isRoadBlock(World world, BlockPos pos) {
        for (int dy = 0; dy <= 4; dy++) {
            if (world.getBlockState(pos.below(dy)).getBlock() == Blocks.IRON_BLOCK) {
                return true;
            }
        }
        return false;
    }

    // Простейший поиск прямых участков дорог
    public static List<BlockPos> findPath(World world, BlockPos start, BlockPos target) {
        List<BlockPos> path = new ArrayList<>();
        path.add(start);

        // Добавляем промежуточную и конечную точки
        path.add(target);
        return path;
    }
}