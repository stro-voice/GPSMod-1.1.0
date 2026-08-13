package com.example.gpsmod.navigation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;

public class MapPathRenderer {

    public static void drawPathOnMap(PlayerEntity player, BlockPos targetPos) {
        ItemStack mapStack = ItemStack.EMPTY;
        
        // Находим ванильную заполненную карту в инвентаре игрока
        for (ItemStack stack : player.inventory.items) {
            if (stack.getItem() == Items.FILLED_MAP) {
                mapStack = stack;
                break;
            }
        }

        if (mapStack.isEmpty()) return;

        // Получаем данные карты
        MapData mapData = FilledMapItem.getSavedData(mapStack, player.level);
        if (mapData == null) return;

        // В Forge 1.16.5 координаты центра карты хранятся в полях x и z
        int scale = 1 << mapData.scale;
        int startX = (int) ((player.getX() - mapData.x) / scale) + 64;
        int startZ = (int) ((player.getZ() - mapData.z) / scale) + 64;

        int endX = (int) ((targetPos.getX() - mapData.x) / scale) + 64;
        int endZ = (int) ((targetPos.getZ() - mapData.z) / scale) + 64;

        // Рисуем линию маршрута по пиксельной сетке карты
        drawLine(mapData.colors, startX, startZ, endX, endZ, (byte) 18); // 18 - красный цвет
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