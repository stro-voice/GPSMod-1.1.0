package com.example.gpsmod.data;

import net.minecraft.util.math.BlockPos;

public class BeaconEntry {
    private final BlockPos pos;
    private final String name;

    public BeaconEntry(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public BlockPos getPos() { return pos; }
    public String getName() { return name; }
}