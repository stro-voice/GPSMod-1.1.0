package com.example.gpsmod;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class GPSManager {
    private static final GPSManager INSTANCE = new GPSManager();
    private boolean ironOnlyMode = true;
    private List<BlockPos> currentPath = new ArrayList<>();

    public static GPSManager getInstance() {
        return INSTANCE;
    }

    public boolean isIronOnlyMode() {
        return ironOnlyMode;
    }

    public void setIronOnlyMode(boolean ironOnlyMode) {
        this.ironOnlyMode = ironOnlyMode;
    }

    public List<BlockPos> getCurrentPath() {
        return currentPath;
    }

    public void clearPath() {
        this.currentPath.clear();
    }

    public void buildPath(World world, BlockPos start, BlockPos target) {
        currentPath.clear();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>();

        queue.add(start);
        parentMap.put(start, null);

        int maxSearch = 1000;
        boolean found = false;

        while (!queue.isEmpty() && maxSearch-- > 0) {
            BlockPos current = queue.poll();

            if (current.closerThan(target, 2)) {
                target = current;
                found = true;
                break;
            }

            for (BlockPos neighbor : getNeighbors(current)) {
                if (!parentMap.containsKey(neighbor)) {
                    BlockState state = world.getBlockState(neighbor);
                    BlockState stateBelow = world.getBlockState(neighbor.below());

                    if (ironOnlyMode) {
                        if (state.getBlock() == Blocks.IRON_BLOCK || stateBelow.getBlock() == Blocks.IRON_BLOCK) {
                            parentMap.put(neighbor, current);
                            queue.add(neighbor);
                        }
                    } else {
                        if (!state.isAir()) {
                            parentMap.put(neighbor, current);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        if (found) {
            BlockPos curr = target;
            while (curr != null) {
                currentPath.add(0, curr);
                curr = parentMap.get(curr);
            }
        }
    }

    private List<BlockPos> getNeighbors(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        return Arrays.asList(
            new BlockPos(x + 1, y, z),
            new BlockPos(x - 1, y, z),
            new BlockPos(x, y, z + 1),
            new BlockPos(x, y, z - 1),
            new BlockPos(x + 1, y + 1, z),
            new BlockPos(x - 1, y + 1, z),
            new BlockPos(x, y + 1, z + 1),
            new BlockPos(x, y + 1, z - 1),
            new BlockPos(x + 1, y - 1, z),
            new BlockPos(x - 1, y - 1, z),
            new BlockPos(x, y - 1, z + 1),
            new BlockPos(x, y - 1, z - 1)
        );
    }
}