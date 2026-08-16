package com.example.gpsmod.events;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.data.BeaconSavedData;
import com.example.gpsmod.network.PacketHandler;
import com.example.gpsmod.network.S2CSendBeaconsPacket;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

    // 2. ПКМ с Компасом открывает меню
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