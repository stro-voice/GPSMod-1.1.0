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

    // 1. Создание маршрута + Сообщение "Маршрут построен"
    public void buildSmartPath(World world, BlockPos startPos) {
        clearPath();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>();

        queue.add(startPos);
        parentMap.put(startPos, null);

        int maxSearch = 3000;
        BlockPos bestEndPos = startPos;

        while (!queue.isEmpty() && maxSearch-- > 0) {
            BlockPos current = queue.poll();
            bestEndPos = current;

            for (BlockPos neighbor : getRoadNeighbors(current)) {
                if (!parentMap.containsKey(neighbor)) {
                    BlockState state = world.getBlockState(neighbor);
                    BlockState stateBelow = world.getBlockState(neighbor.below());

                    if (state.getBlock() == Blocks.IRON_BLOCK || stateBelow.getBlock() == Blocks.IRON_BLOCK) {
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        this.targetPos = bestEndPos;

        BlockPos curr = bestEndPos;
        while (curr != null) {
            currentPath.add(0, curr);
            curr = parentMap.get(curr);
        }

        // Сообщение 1: Маршрут построен
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && !currentPath.isEmpty()) {
            mc.player.displayClientMessage(
                new StringTextComponent("§b[GPS] Маршрут успешно построен! Длина: §e" + currentPath.size() + " §bблоков."), 
                false
            );
        }
    }

    // Проверка тика: сброс при прибытии и оповещения о поворотах
    public void tick(BlockPos playerPos) {
        if (targetPos == null || currentPath.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Сообщение 2: Вы прибыли в пункт назначения
        if (playerPos.closerThan(targetPos, 3.0)) {
            mc.player.displayClientMessage(
                new StringTextComponent("§a[GPS] Вы прибыли в пункт назначения!"), 
                true
            );
            clearPath();
            return;
        }

        // Сообщение 3: Оповещение о поворотах
        checkTurnAnnouncements(playerPos, mc);
    }

    private void checkTurnAnnouncements(BlockPos playerPos, Minecraft mc) {
        if (currentPath.size() < 10) return;

        int closestIndex = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < currentPath.size(); i++) {
            double dist = currentPath.get(i).distSqr(playerPos);
            if (dist < minDist) {
                minDist = dist;
                closestIndex = i;
            }
        }

        if (closestIndex + 10 < currentPath.size()) {
            BlockPos p1 = currentPath.get(closestIndex);
            BlockPos p2 = currentPath.get(closestIndex + 5);
            BlockPos p3 = currentPath.get(closestIndex + 10);

            int dx1 = p2.getX() - p1.getX();
            int dz1 = p2.getZ() - p1.getZ();
            int dx2 = p3.getX() - p2.getX();
            int dz2 = p3.getZ() - p2.getZ();

            int crossProduct = dx1 * dz2 - dz1 * dx2;

            String currentTurn = "";
            if (crossProduct > 0) {
                currentTurn = "§eЧерез 10 блоков поворот направо! ↱";
            } else if (crossProduct < 0) {
                currentTurn = "§eЧерез 10 блоков поворот налево! ↰";
            }

            if (!currentTurn.isEmpty() && !currentTurn.equals(lastTurnMessage)) {
                mc.player.displayClientMessage(new StringTextComponent(currentTurn), true);
                lastTurnMessage = currentTurn;
            }
        }
    }

    private List<BlockPos> getRoadNeighbors(BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return Arrays.asList(
            new BlockPos(x + 1, y, z), new BlockPos(x - 1, y, z),
            new BlockPos(x, y, z + 1), new BlockPos(x, y, z - 1),
            new BlockPos(x + 1, y + 1, z), new BlockPos(x - 1, y + 1, z),
            new BlockPos(x, y + 1, z + 1), new BlockPos(x, y - 1, z - 1)
        );
    }
}