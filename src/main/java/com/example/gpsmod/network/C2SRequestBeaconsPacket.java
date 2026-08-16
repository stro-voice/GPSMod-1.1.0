package com.example.gpsmod.network;

import com.example.gpsmod.data.BeaconSavedData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRequestBeaconsPacket {
    public C2SRequestBeaconsPacket() {}

    public static void encode(C2SRequestBeaconsPacket msg, PacketBuffer buf) {}

    public static C2SRequestBeaconsPacket decode(PacketBuffer buf) {
        return new C2SRequestBeaconsPacket();
    }

    public static void handle(C2SRequestBeaconsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player != null) {
                ServerWorld world = player.getLevel();
                BeaconSavedData savedData = BeaconSavedData.get(world);
                PacketHandler.sendToPlayer(new S2CSendBeaconsPacket(savedData.getBeacons()), player);
            }
        });
        context.setPacketHandled(true);
    }
}