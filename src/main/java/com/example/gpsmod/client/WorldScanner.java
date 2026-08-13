package com.example.gpsmod.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldScanner {

    public static final int RADIUS = 64; // Сканирует 64 блока в каждую сторону (128x128 всего)
    public static final int MAP_SIZE = RADIUS * 2;

    private static DynamicTexture mapTexture;
    private static ResourceLocation mapTextureLocation;
    private static NativeImage nativeImage;

    public static void init() {
        nativeImage = new NativeImage(MAP_SIZE, MAP_SIZE, false);
        mapTexture = new DynamicTexture(nativeImage);
        mapTextureLocation = Minecraft.getInstance().getTextureManager().register("gps_dynamic_map", mapTexture);
    }

    public static ResourceLocation updateAndGetMapTexture() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return mapTextureLocation;

        World world = mc.level;
        BlockPos playerPos = mc.player.blockPosition();

        if (nativeImage == null) init();

        int pX = playerPos.getX();
        int pY = playerPos.getY();
        int pZ = playerPos.getZ();

        for (int x = -RADIUS; x < RADIUS; x++) {
            for (int z = -RADIUS; z < RADIUS; z++) {
                int worldX = pX + x;
                int worldZ = pZ + z;

                int imgX = x + RADIUS;
                int imgY = z + RADIUS;

                int color = 0xFF222222; // Темный фон

                for (int y = 4; y >= -4; y--) {
                    BlockPos currentPos = new BlockPos(worldX, pY + y, worldZ);
                    BlockState state = world.getBlockState(currentPos);

                    if (state.is(Blocks.IRON_BLOCK)) {
                        color = 0xFFE6E6E6; // Дорога (Яркое железо)
                        break;
                    } else if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)) {
                        color = 0xFF4C7B38; // Зеленая трава
                        break;
                    } else if (state.is(Blocks.WATER)) {
                        color = 0xFF3F76E4; // Синяя вода
                        break;
                    } else if (state.is(Blocks.STONE)) {
                        color = 0xFF707070; // Серый камень
                        break;
                    }
                }

                nativeImage.setPixelRGBA(imgX, imgY, convertToABGR(color));
            }
        }

        mapTexture.upload();
        return mapTextureLocation;
    }

    private static int convertToABGR(int hexARGB) {
        int a = (hexARGB >> 24) & 0xFF;
        int r = (hexARGB >> 16) & 0xFF;
        int g = (hexARGB >> 8) & 0xFF;
        int b = hexARGB & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}