package com.example.gpsmod.data;

import com.example.gpsmod.GPSMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class BeaconSavedData extends WorldSavedData {
    private static final String DATA_NAME = GPSMod.MOD_ID + "_beacons";
    private final List<BeaconEntry> beacons = new ArrayList<>();

    public BeaconSavedData() {
        super(DATA_NAME);
    }

    public BeaconSavedData(String name) {
        super(name);
    }

    public static BeaconSavedData get(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            return serverWorld.getDataStorage().computeIfAbsent(BeaconSavedData::new, DATA_NAME);
        }
        return new BeaconSavedData();
    }

    public void addBeacon(BlockPos pos, String name) {
        this.beacons.removeIf(entry -> entry.getPos().equals(pos));
        this.beacons.add(new BeaconEntry(pos, name));
        this.setDirty();
    }

    public void removeBeacon(BlockPos pos) {
        if (this.beacons.removeIf(entry -> entry.getPos().equals(pos))) {
            this.setDirty();
        }
    }

    public List<BeaconEntry> getBeacons() {
        return this.beacons;
    }

    @Override
    public void load(CompoundNBT nbt) {
        this.beacons.clear();
        ListNBT list = nbt.getList("Beacons", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            BlockPos pos = NBTUtil.readBlockPos(tag.getCompound("Pos"));
            String name = tag.getString("Name");
            this.beacons.add(new BeaconEntry(pos, name));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (BeaconEntry entry : this.beacons) {
            CompoundNBT tag = new CompoundNBT();
            tag.put("Pos", NBTUtil.writeBlockPos(entry.getPos()));
            tag.putString("Name", entry.getName());
            list.add(tag);
        }
        compound.put("Beacons", list);
        return compound;
    }
}