package com.gpsmod.assistant;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CopilotManager {

    private static long lastMessageTime = 0;

    public static void update(PlayerEntity player, boolean isOnRoad, double speedBlocksPerSec, double distanceToFinish) {
        long now = System.currentTimeMillis();
        if (now - lastMessageTime < 3500) return; // Интервал предупреждений

        if (distanceToFinish > 0 && distanceToFinish < 15) {
            notify(player, "§a[GPS-Штурман]:§f Вы приближаетесь к пункту назначения!");
            lastMessageTime = now;
            return;
        }

        if (!isOnRoad && speedBlocksPerSec > 2.0) {
            notify(player, "§c[GPS-Штурман]:§f Внимание! Потеряно железобетонное покрытие.");
            lastMessageTime = now;
            return;
        }

        if (speedBlocksPerSec > 12.0) {
            notify(player, "§e[GPS-Штурман]:§f Высокая скорость! Будьте внимательны на поворотах.");
            lastMessageTime = now;
            return;
        }

        long dayTime = player.level.getDayTime() % 24000;
        if (dayTime > 12500 && dayTime < 12600) {
            notify(player, "§9[GPS-Штурман]:§f Наступает ночь. Включите фарный свет!");
            lastMessageTime = now;
        }
    }

    private static void notify(PlayerEntity player, String message) {
        player.displayClientMessage(new StringTextComponent(message), true);
    }
}