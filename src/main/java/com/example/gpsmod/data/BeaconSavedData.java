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
    private final List<BlockPos> beacons = new ArrayList<>();

    public BeaconSavedData() {
        super(DATA_NAME);
    }

    public BeaconSavedData(String name) {
        super(name);
    }

    // Получение или создание данных мира
    public static BeaconSavedData get(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            return serverWorld.getDataStorage().computeIfAbsent(BeaconSavedData::new, DATA_NAME);
        }
        return new BeaconSavedData();
    }

    // Добавление флага
    public void addBeacon(BlockPos pos) {
        if (!this.beacons.contains(pos)) {
            this.beacons.add(pos);
            this.setDirty(); // Заставляет сохранять на диск
        }
    }

    // Удаление флага
    public void removeBeacon(BlockPos pos) {
        if (this.beacons.remove(pos)) {
            this.setDirty(); // Заставляет сохранять на диск
        }
    }

    // Получение списка всех флагов
    public List<BlockPos> getBeacons() {
        return this.beacons;
    }

    // Чтение списка из файла сохранения мира
    @Override
    public void load(CompoundNBT nbt) {
        this.beacons.clear();
        ListNBT list = nbt.getList("Beacons", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            this.beacons.add(NBTUtil.readBlockPos(list.getCompound(i)));
        }
    }

    // Запись списка в файл сохранения мира
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        for (BlockPos pos : this.beacons) {
            list.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put("Beacons", list);
        return compound;
    }
}