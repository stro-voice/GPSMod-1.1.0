package com.example.gpsmod.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class BeaconSavedData extends WorldSavedData {
    private static final String DATA_NAME = "gpsmod_beacons";
    private final List<BlockPos> beacons = new ArrayList<>();

    public BeaconSavedData() {
        super(DATA_NAME);
    }

    public static BeaconSavedData get(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            return serverWorld.getDataStorage().computeIfAbsent(BeaconSavedData::new, DATA_NAME);
        }
        return new BeaconSavedData();
    }

    public void addBeacon(BlockPos pos) {
        if (!beacons.contains(pos)) {
            beacons.add(pos);
            setDirty();
        }
    }

    public List<BlockPos> getBeacons() {
        return this.beacons;
    }

    @Override
    public void load(CompoundNBT nbt) {
        beacons.clear();
        ListNBT list = nbt.getList("Beacons", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            beacons.add(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (BlockPos pos : beacons) {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            list.add(tag);
        }
        compound.put("Beacons", list);
        return compound;
    }
}