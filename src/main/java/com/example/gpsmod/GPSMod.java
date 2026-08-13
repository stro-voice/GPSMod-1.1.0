package com.example.gpsmod;

import com.example.gpsmod.item.TabletItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(GPSMod.MOD_ID)
public class GPSMod {
    public static final String MOD_ID = "gpsmod";

    // Добавляем Планшет в Креативную вкладку "Разное"
    public static final TabletItem GPS_TABLET = (TabletItem) new TabletItem(
            new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)
    ).setRegistryName(MOD_ID, "gps_tablet");

    public GPSMod() {}

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(GPS_TABLET);
        }
    }
}