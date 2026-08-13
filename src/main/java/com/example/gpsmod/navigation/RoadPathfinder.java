package com.example.gpsmod.navigation;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RoadPathfinder {

    public static List<BlockPos> activePath = new ArrayList<>();

    public static void calculatePath(World world, BlockPos start, BlockPos target) {
        activePath.clear();

        BlockPos current = start;
        activePath.add(current);

        for (int step = 0; step < 200; step++) {
            if (current.distSqr(target) < 4) {
                activePath.add(target);
                break;
            }

            BlockPos nextStep = null;
            double bestDist = Double.MAX_VALUE;

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;

                    BlockPos check = current.offset(x, 0, z);
                    if (world.getBlockState(check.below()).is(Blocks.IRON_BLOCK) || world.getBlockState(check).is(Blocks.IRON_BLOCK)) {
                        double dist = check.distSqr(target);
                        if (dist < bestDist && !activePath.contains(check)) {
                            bestDist = dist;
                            nextStep = check;
                        }
                    }
                }
            }

            if (nextStep != null) {
                current = nextStep;
                activePath.add(current);
            } else {
                activePath.add(target);
                break;
            }
        }
    }
}