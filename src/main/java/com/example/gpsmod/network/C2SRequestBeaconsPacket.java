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

    public static void handle(C2SRequestBeaconsPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
    NetworkEvent.Context ctx = ctxSupplier.get();
    
    ctx.enqueueWork(() -> {
        // Объявляем player ТОЛЬКО ОДИН РАЗ внутри enqueueWork
        ServerPlayerEntity player = ctx.getSender();
        if (player != null) {
            // Ваша логика отправки меток игроку...
        }
    });
    
    ctx.setPacketHandled(true);
}
}