package com.example.gpsmod.tracker;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class IronBeaconTracker {
    public static final List<BlockPos> IRON_BEACONS = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!event.getWorld().isClientSide() && event.getPlacedBlock().getBlock() == Blocks.IRON_BLOCK) {
            BlockPos pos = event.getPos().immutable();
            if (!IRON_BEACONS.contains(pos)) {
                IRON_BEACONS.add(pos);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isClientSide() && event.getState().getBlock() == Blocks.IRON_BLOCK) {
            IRON_BEACONS.remove(event.getPos());
        }
    }
}