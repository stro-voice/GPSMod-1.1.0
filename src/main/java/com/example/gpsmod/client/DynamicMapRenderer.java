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

                // 1. Центрируем карту на игроке
                mapData.x = pX;
                mapData.z = pZ;

                // 2. Сканируем блоки только из ЗАГРУЖЕННЫХ чанков
                updateMapTerrainSafe(mc.level, mapData, pX, pZ);

                // 3. Наносим красную линию GPS
                if (stack.hasTag()) {
                    CompoundNBT tag = stack.getTag();
                    if (tag != null && tag.getBoolean("HasGPS")) {
                        int targetX = tag.getInt("TargetX");
                        int targetZ = tag.getInt("TargetZ");
                        drawRouteOnMap(mapData, pX, pZ, targetX, targetZ);
                    }
                }

                // 4. Принудительно обновляем GPU-текстуру карты
                mc.gameRenderer.getMapRenderer().update(mapData);
            }
        }
    }

    private static void updateMapTerrainSafe(World world, MapData mapData, int playerX, int playerZ) {
        for (int px = 0; px < 128; px++) {
            for (int pz = 0; pz < 128; pz++) {
                int worldX = playerX + (px - 64);
                int worldZ = playerZ + (pz - 64);

                if (world.hasChunk(worldX >> 4, worldZ >> 4)) {
                    BlockPos topPos = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(worldX, 0, worldZ));
                    if (topPos.getY() > 0) {
                        BlockState state = world.getBlockState(topPos.below());
                        MaterialColor matColor = state.getMapColor(world, topPos.below());
                        if (matColor != null) {
                            mapData.colors[px + pz * 128] = (byte) (matColor.id * 4 + 2);
                            continue;
                        }
                    }
                }
                mapData.colors[px + pz * 128] = (byte) 112; 
            }
        }
    }

    private static void drawRouteOnMap(MapData mapData, int playerX, int playerZ, int targetX, int targetZ) {
        int startX = 64;
        int startZ = 64;

        int endX = 64 + (targetX - playerX);
        int endZ = 64 + (targetZ - playerZ);

        int clampedEndX = Math.max(2, Math.min(125, endX));
        int clampedEndZ = Math.max(2, Math.min(125, endZ));

        drawLine(mapData.colors, startX, startZ, clampedEndX, clampedEndZ, (byte) 30);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int px = clampedEndX + dx;
                int pz = clampedEndZ + dz;
                if (px >= 0 && px < 128 && pz >= 0 && pz < 128) {
                    mapData.colors[px + pz * 128] = (byte) 18;
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

        while (true) {
            if (x0 >= 0 && x0 < 128 && y0 >= 0 && y0 < 128) {
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
        }
    }
}