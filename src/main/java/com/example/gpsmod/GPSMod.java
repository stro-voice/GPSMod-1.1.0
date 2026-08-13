package com.example.gpsmod;

import com.example.gpsmod.client.WorldScanner;
import com.example.gpsmod.item.TabletItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("gpsmod")
public class GPSMod {

    public static TabletItem TABLET_ITEM = new TabletItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC));

    public GPSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        WorldScanner.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            event.getRegistry().register(TABLET_ITEM.setRegistryName("gps_tablet"));
        }
    }
}