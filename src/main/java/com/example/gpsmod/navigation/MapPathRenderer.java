package com.example.gpsmod.navigation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;

import java.util.Arrays;

public class MapPathRenderer {

    public static void drawPathOnMap(PlayerEntity player, BlockPos targetPos) {
        ItemStack mapStack = ItemStack.EMPTY;
        
        // Находим карту в инвентаре
        for (ItemStack stack : player.inventory.items) {
            if (stack.getItem() == Items.FILLED_MAP) {
                mapStack = stack;
                break;
            }
        }

        if (mapStack.isEmpty()) return;

        MapData mapData = FilledMapItem.getSavedData(mapStack, player.level);
        if (mapData == null) return;

        // 1. Очищаем холст карты (делаем всё бежевым фоном карт)
        Arrays.fill(mapData.colors, (byte) 28); // 28 - базовый цвет бумаги

        // 2. Считаем реальные координаты
        double playerX = player.getX();
        double playerZ = player.getZ();
        double targetX = targetPos.getX();
        double targetZ = targetPos.getZ();

        double deltaX = targetX - playerX;
        double deltaZ = targetZ - playerZ;

        // 3. Вычисляем динамический масштаб под ЛЮБОЕ расстояние
        double maxDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
        if (maxDistance < 1) maxDistance = 1; // Защита от деления на 0

        // Карта 128x128. Оставляем по 14 пикселей отступов от краев (рабочая зона = 100 пикселей)
        double scale = maxDistance / 100.0;

        // 4. Переводим координаты в пиксели холста (Центр карты = 64, 64)
        int pX = 64 - (int)(deltaX / scale / 2.0);
        int pZ = 64 - (int)(deltaZ / scale / 2.0);

        int tX = 64 + (int)(deltaX / scale / 2.0);
        int tZ = 64 + (int)(deltaZ / scale / 2.0);

        // 5. Рисуем линию маршрута
        drawLine(mapData.colors, pX, pZ, tX, tZ, (byte) 18); // Красная линия

        // 6. Рисуем маркера (Точки А и Б)
        drawPoint(mapData.colors, pX, pZ, (byte) 30); // Синяя точка (Игрок)
        drawPoint(mapData.colors, tX, tZ, (byte) 34); // Зеленая точка (Маяк)
    }

    private static void drawPoint(byte[] mapColors, int x, int y, byte color) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < 128 && py >= 0 && py < 128) {
                    mapColors[px + py * 128] = color;
                }
            }
        }
    }

    private static void drawLine(byte[] mapColors, int x0, int y0, int x1, int y1, byte color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x0 >= 0 && x0 < 128 && y0 >= 0 && y0 < 128) {
                mapColors[x0 + y0 * 128] = color;
            }

            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}