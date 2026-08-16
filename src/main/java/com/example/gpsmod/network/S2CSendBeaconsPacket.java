package com.example.gpsmod.network;

import com.example.gpsmod.client.MapBeaconScreen;
import com.example.gpsmod.data.BeaconEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CSendBeaconsPacket {
    private final List<BeaconEntry> beacons;

    public S2CSendBeaconsPacket(List<BeaconEntry> beacons) {
        this.beacons = beacons;
    }

    public static void encode(S2CSendBeaconsPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.beacons.size());
        for (BeaconEntry entry : msg.beacons) {
            buf.writeBlockPos(entry.getPos());
            buf.writeUtf(entry.getName());
        }
    }

    public static S2CSendBeaconsPacket decode(PacketBuffer buf) {
        int size = buf.readInt();
        List<BeaconEntry> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BlockPos pos = buf.readBlockPos();
            String name = buf.readUtf(32767);
            list.add(new BeaconEntry(pos, name));
        }
        return new S2CSendBeaconsPacket(list);
    }

    public static void handle(S2CSendBeaconsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new MapBeaconScreen(msg.beacons));
        });
        context.setPacketHandled(true);
    }
}