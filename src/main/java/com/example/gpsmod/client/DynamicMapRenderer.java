package com.example.gpsmod.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DynamicMapRenderer {

    private static int lastPlayerX = Integer.MIN_VALUE;
    private static int lastPlayerZ = Integer.MIN_VALUE;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == Items.FILLED_MAP) {
            MapData mapData = FilledMapItem.getSavedData(stack, mc.level);
            if (mapData != null) {
                int pX = (int) player.getX();
                int pZ = (int) player.getZ();

                // 1. Центрируем карту строго на игрока
                mapData.x = pX;
                mapData.z = pZ;

                // 2. Сканируем блоки мира вокруг игрока и обновляем текстуру рельефа при движении
                if (Math.abs(pX - lastPlayerX) >= 1 || Math.abs(pZ - lastPlayerZ) >= 1) {
                    lastPlayerX = pX;
                    lastPlayerZ = pZ;
                    updateMapTerrain(mc.level, mapData, pX, pZ);
                }

                // 3. Рисуем поверх рельефа GPS-маршрут к выбранному флагу
                if (stack.hasTag()) {
                    CompoundNBT tag = stack.getTag();
                    if (tag != null && tag.getBoolean("HasGPS")) {
                        int targetX = tag.getInt("TargetX");
                        int targetZ = tag.getInt("TargetZ");
                        drawRouteOnMap(mapData, pX, pZ, targetX, targetZ);
                    }
                }
            }
        }
    }

    /**
     * Сканирует блоки мира в радиусе 64 блоков вокруг игрока и записывает цвета в mapData.colors
     */
    private static void updateMapTerrain(World world, MapData mapData, int playerX, int playerZ) {
        for (int px = 0; px < 128; px++) {
            for (int pz = 0; pz < 128; pz++) {
                // Перевод пикселей карты (0..127) в координаты мира
                int worldX = playerX + (px - 64);
                int worldZ = playerZ + (pz - 64);

                // Находим верхний твердый блок
                BlockPos topPos = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(worldX, 0, worldZ));
                BlockState state = world.getBlockState(topPos.below());

                // Получаем ванильный цвет карты для блока
                MaterialColor matColor = state.getMapColor(world, topPos.below());
                if (matColor != null) {
                    // Базовый цвет блока
                    byte colorByte = (byte) (matColor.id * 4 + 2);
                    mapData.colors[px + pz * 128] = colorByte;
                }
            }
        }
    }

    /**
     * Отрисовка GPS-линии и маркера цели
     */
    private static void drawRouteOnMap(MapData mapData, int playerX, int playerZ, int targetX, int targetZ) {
        int startX = 64;
        int startZ = 64;

        int endX = 64 + (targetX - playerX);
        int endZ = 64 + (targetZ - playerZ);

        int clampedEndX = Math.max(2, Math.min(125, endX));
        int clampedEndZ = Math.max(2, Math.min(125, endZ));

        // Рисуем пунктирную линию
        drawLine(mapData.colors, startX, startZ, clampedEndX, clampedEndZ, (byte) 30); // 30 = ярко-красный цвет

        // Маркер цели 3х3
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int px = clampedEndX + dx;
                int pz = clampedEndZ + dz;
                if (px >= 0 && px < 128 && pz >= 0 && pz < 128) {
                    mapData.colors[px + pz * 128] = (byte) 18; // Ярко-желтый маркер
                }
            }
        }
    }

    private static void drawLine(byte[] colors, int x0, int y0, int x1, int y1, byte color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int step = 0;
        while (true) {
            if (step % 3 != 0 && x0 >= 0 && x0 < 128 && y0 >= 0 && y0 < 128) {
                colors[x0 + y0 * 128] = color;
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
            step++;
        }
    }
}