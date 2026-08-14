package com.example.gpsmod.network;

import com.example.gpsmod.data.BeaconSavedData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class C2SRequestBeaconsPacket {
    public C2SRequestBeaconsPacket() {}
    public static void encode(C2SRequestBeaconsPacket msg, PacketBuffer buf) {}
    public static C2SRequestBeaconsPacket decode(PacketBuffer buf) { return new C2SRequestBeaconsPacket(); }

    public static void handle(C2SRequestBeaconsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                BeaconSavedData data = BeaconSavedData.get(player.getCommandSenderWorld());
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2CSendBeaconsPacket(data.getBeacons()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}