package com.example.gpsmod.network;

import com.example.gpsmod.client.ClientGpsState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SSelectBeaconPacket {
    private final BlockPos target;

    public C2SSelectBeaconPacket(BlockPos target) {
        this.target = target;
    }

    public static void encode(C2SSelectBeaconPacket msg, PacketBuffer buf) {
        buf.writeBoolean(msg.target != null);
        if (msg.target != null) {
            buf.writeBlockPos(msg.target);
        }
    }

    public static C2SSelectBeaconPacket decode(PacketBuffer buf) {
        boolean hasTarget = buf.readBoolean();
        return new C2SSelectBeaconPacket(hasTarget ? buf.readBlockPos() : null);
    }

    public static void handle(C2SSelectBeaconPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            // Устанавливает выбранную цель или null при сбросе
            ClientGpsState.activeTarget = msg.target;
        });
        context.setPacketHandled(true);
    }
}