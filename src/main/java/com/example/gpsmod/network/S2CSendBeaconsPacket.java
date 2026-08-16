package com.example.gpsmod.network;

import com.example.gpsmod.client.MapBeaconScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CSendBeaconsPacket {
    private final List<BlockPos> beacons;

    public S2CSendBeaconsPacket(List<BlockPos> beacons) {
        this.beacons = beacons;
    }

    public static void encode(S2CSendBeaconsPacket msg, PacketBuffer buf) {
        buf.writeInt(msg.beacons.size());
        for (BlockPos pos : msg.beacons) {
            buf.writeBlockPos(pos);
        }
    }

    public static S2CSendBeaconsPacket decode(PacketBuffer buf) {
        int size = buf.readInt();
        List<BlockPos> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(buf.readBlockPos());
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