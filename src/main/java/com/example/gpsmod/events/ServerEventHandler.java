package com.example.gpsmod.event;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.data.BeaconSavedData;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {

    // 1. Shift + ПКМ по ФЛАГУ для регистрации
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isClientSide()) return;

        PlayerEntity player = event.getPlayer();

        if (player != null && player.isCrouching()) {
            BlockPos pos = event.getPos();
            Block block = world.getBlockState(pos).getBlock();

            // Проверяем СТРОГО флаги (напольные и настенные баннеры)
            if (block instanceof BannerBlock || block instanceof WallBannerBlock) {
                BeaconSavedData savedData = BeaconSavedData.get(world);
                savedData.addBeacon(pos);

                player.sendMessage(
                    new StringTextComponent("🚩 Флаг зарегистрирован: ")
                        .append(new StringTextComponent("X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ())
                        .withStyle(TextFormatting.GREEN)),
                    player.getUUID()
                );

                event.setCanceled(true);
            }
        }
    }
}