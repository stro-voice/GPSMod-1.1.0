package com.example.gpsmod.events;

import com.example.gpsmod.data.BeaconSavedData;
import com.example.gpsmod.network.C2SRequestBeaconsPacket;
import com.example.gpsmod.network.PacketHandler;
import net.minecraft.block.BannerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isClientSide() && event.getItemStack().getItem() == Items.FILLED_MAP) {
            if (event.getEntityPlayer().isShiftKeyDown()) {
                if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BannerBlock) {
                    BeaconSavedData data = BeaconSavedData.get(event.getWorld());
                    data.addBeacon(event.getPos());
                    event.getEntityPlayer().sendMessage(new StringTextComponent("🚩 Флаг зарегистрирован как GPS-маяк!"), event.getEntityPlayer().getUUID());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isClientSide() && event.getItemStack().getItem() == Items.FILLED_MAP) {
            PlayerEntity player = event.getPlayer();
            if (!player.isShiftKeyDown()) {
                PacketHandler.CHANNEL.sendToServer(new C2SRequestBeaconsPacket());
            }
        }
    }
}