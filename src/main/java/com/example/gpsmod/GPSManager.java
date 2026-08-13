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

    // Алгоритм поиска пути строго по железным блокам
    public void buildPath(World world, BlockPos start, BlockPos target) {
        currentPath.clear();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>();

        queue.add(start);
        parentMap.put(start, null);

        int maxSearch = 1000; // Лимит блоков, чтобы игра не зависала
        boolean found = false;

        while (!queue.isEmpty() && maxSearch-- > 0) {
            BlockPos current = queue.poll();

            if (current.withinDistance(target, 2)) {
                target = current;
                found = true;
                break;
            }

            for (BlockPos neighbor : getNeighbors(current)) {
                if (!parentMap.containsKey(neighbor)) {
                    BlockState state = world.getBlockState(neighbor);
                    BlockState stateBelow = world.getBlockState(neighbor.down());

                    // Если включен режим железа — проверяем на блоках железа
                    if (ironOnlyMode) {
                        if (state.getBlock() == Blocks.IRON_BLOCK || stateBelow.getBlock() == Blocks.IRON_BLOCK) {
                            parentMap.put(neighbor, current);
                            queue.add(neighbor);
                        }
                    } else {
                        // Обычный режим
                        if (state.getMaterial().isSolid()) {
                            parentMap.put(neighbor, current);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        // Восстанавливаем путь от финиша к старту
        if (found) {
            BlockPos curr = target;
            while (curr != null) {
                currentPath.add(0, curr);
                curr = parentMap.get(curr);
            }
        }
    }

    private List<BlockPos> getNeighbors(BlockPos pos) {
        return Arrays.asList(
            pos.north(), pos.south(), pos.east(), pos.west(),
            pos.north().up(), pos.south().up(), pos.east().up(), pos.west().up(),
            pos.north().down(), pos.south().down(), pos.east().down(), pos.west().down()
        );
    }
}