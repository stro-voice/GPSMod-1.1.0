package com.example.gpsmod;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.*;

public class GPSManager {
    private static final GPSManager INSTANCE = new GPSManager();
    private boolean minimapVisible = true;
    private List<BlockPos> currentPath = new ArrayList<>();
    private BlockPos targetPos = null;
    private String lastTurnMessage = "";

    public static GPSManager getInstance() { return INSTANCE; }

    public boolean isMinimapVisible() { return minimapVisible; }
    public void toggleMinimap() { this.minimapVisible = !this.minimapVisible; }

    public List<BlockPos> getCurrentPath() { return currentPath; }

    public void clearPath() { 
        this.currentPath.clear(); 
        this.targetPos = null;
        this.lastTurnMessage = "";
    }

    // Сканирование: видит железный блок под ЛЮБЫМИ блоками дорожного покрытия из модов (до 4 блоков вниз)
    public static boolean isRoadBlock(World world, BlockPos pos) {
        for (int dy = 0; dy >= -4; dy--) {
            BlockPos checkPos = pos.offset(0, dy, 0);
            BlockState state = world.getBlockState(checkPos);
            if (state.getBlock() == Blocks.IRON_BLOCK) {
                return true;
            }
        }
        return false;
    }

    // Построение пути по железным блокам к выбранной точке
    public void buildSmartPathToTarget(World world, BlockPos startPos, BlockPos target) {
        clearPath();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>();

        queue.add(startPos);
        parentMap.put(startPos, null);

        int maxSearch = 8000;
        boolean found = false;

        while (!queue.isEmpty() && maxSearch-- > 0) {
            BlockPos current = queue.poll();

            if (current.closerThan(target, 4.0)) {
                this.targetPos = current;
                found = true;
                break;
            }

            for (BlockPos neighbor : getRoadNeighbors(current)) {
                if (!parentMap.containsKey(neighbor)) {
                    if (isRoadBlock(world, neighbor)) {
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        if (found && targetPos != null) {
            BlockPos curr = targetPos;
            while (curr != null) {
                currentPath.add(0, curr);
                curr = parentMap.get(curr);
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    new StringTextComponent("§b[GPS] Маршрут успешно построен! Длина: §e" + currentPath.size() + " §bблоков."), 
                    false
                );
            }
        } else {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    new StringTextComponent("§c[GPS] Не удалось найти трассу из железных блоков к этой точке!"), 
                    false
                );
            }
        }
    }

    public void tick(BlockPos playerPos) {
        if (targetPos == null || currentPath.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Авто-сброс при прибытии в пункт назначения
        if (playerPos.closerThan(targetPos, 4.0)) {
            mc.player.displayClientMessage(
                new StringTextComponent("§a[GPS] Вы прибыли в пункт назначения!"), 
                true
            );
            clearPath();
            return;
        }

        checkTurnAnnouncements(playerPos, mc);
    }

    private void checkTurnAnnouncements(BlockPos playerPos, Minecraft mc) {
        if (currentPath.size() < 12) return;

        int closestIndex = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < currentPath.size(); i++) {
            double dist = currentPath.get(i).distSqr(playerPos);
            if (dist < minDist) {
                minDist = dist;
                closestIndex = i;
            }
        }

        if (closestIndex + 12 < currentPath.size()) {
            BlockPos p1 = currentPath.get(closestIndex);
            BlockPos p2 = currentPath.get(closestIndex + 6);
            BlockPos p3 = currentPath.get(closestIndex + 12);

            int dx1 = p2.getX() - p1.getX();
            int dz1 = p2.getZ() - p1.getZ();
            int dx2 = p3.getX() - p2.getX();
            int dz2 = p3.getZ() - p2.getZ();

            int crossProduct = dx1 * dz2 - dz1 * dx2;

            String currentTurn = "";
            if (crossProduct > 2) {
                currentTurn = "§eЧерез 10-15 блоков поворот направо! ↱";
            } else if (crossProduct < -2) {
                currentTurn = "§eЧерез 10-15 блоков поворот налево! ↰";
            }

            if (!currentTurn.isEmpty() && !currentTurn.equals(lastTurnMessage)) {
                mc.player.displayClientMessage(new StringTextComponent(currentTurn), true);
                lastTurnMessage = currentTurn;
            }
        }
    }

    // Соседи с поддержкой диагоналей для широких дорог
    private List<BlockPos> getRoadNeighbors(BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return Arrays.asList(
            new BlockPos(x + 1, y, z), new BlockPos(x - 1, y, z),
            new BlockPos(x, y, z + 1), new BlockPos(x, y, z - 1),
            new BlockPos(x + 1, y, z + 1), new BlockPos(x - 1, y, z - 1),
            new BlockPos(x + 1, y, z - 1), new BlockPos(x - 1, y, z + 1)
        );
    }
}