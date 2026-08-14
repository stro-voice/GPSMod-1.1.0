package com.example.gpsmod.event;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.data.BeaconSavedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GPSMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        // Работаем только на сервере
        if (world.isClientSide()) return;

        PlayerEntity player = event.getPlayer();

        // Проверяем, что игрок зажал Shift (крадется)
        if (player != null && player.isCrouching()) {
            BlockPos pos = event.getPos();
            
            // Сохраняем флаг в дата-пак мира
            BeaconSavedData savedData = BeaconSavedData.get(world);
            savedData.addBeacon(pos);

            // Отправляем игроку уведомление в чат
            player.sendMessage(
                new StringTextComponent("🚩 Флаг зарегистрирован: ")
                    .append(new StringTextComponent("X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ())
                    .withStyle(TextFormatting.GREEN)),
                player.getUUID()
            );

            // Отменяем дальнейшее взаимодействие с блоком
            event.setCanceled(true);
        }
    }
}