package com.example.gpsmod.events;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.client.FlagNameScreen;
import com.example.gpsmod.data.BeaconSavedData;
import com.example.gpsmod.network.ModNetwork;
import com.example.gpsmod.network.S2CSendBeaconsPacket;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {

    // 1. Shift + ПКМ по флагу — открываем поле ввода имени на клиенте
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();

        if (player != null && player.isCrouching()) {
            BlockPos pos = event.getPos();
            Block block = event.getWorld().getBlockState(pos).getBlock();

            if (block instanceof BannerBlock || block instanceof WallBannerBlock) {
                if (event.getWorld().isClientSide()) {
                    Minecraft.getInstance().setScreen(new FlagNameScreen(pos));
                }
                event.setCanceled(true);
            }
        }
    }

    // 2. Ломание флага удаляет его из списка GPS
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        if (world.isClientSide()) return;

        BlockPos pos = event.getPos();
        Block block = event.getState().getBlock();

        if (block instanceof BannerBlock || block instanceof WallBannerBlock) {
            BeaconSavedData savedData = BeaconSavedData.get(world);
            savedData.removeBeacon(pos);
        }
    }

    // 3. ПКМ с Компасом открывает меню выбора
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        World world = event.getWorld();
        if (world.isClientSide()) return;

        PlayerEntity player = event.getPlayer();
        if (player != null && event.getItemStack().getItem() == Items.COMPASS) {
            BeaconSavedData savedData = BeaconSavedData.get(world);
            PacketHandler.sendToPlayer(
                new S2CSendBeaconsPacket(savedData.getBeacons()), 
                (ServerPlayerEntity) player
            );
        }
    }
}