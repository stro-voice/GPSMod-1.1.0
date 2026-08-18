package com.example.gpsmod.network;

import com.example.gpsmod.GPSMod;
import com.example.gpsmod.client.ClientWaypointManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class GPSNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GPSMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int id = 0;
        CHANNEL.registerMessage(
                id++,
                PacketSyncWaypoint.class,
                PacketSyncWaypoint::encode,
                PacketSyncWaypoint::decode,
                PacketSyncWaypoint::handle
        );
    }

    public static class PacketSyncWaypoint {
        private final BlockPos pos;
        private final String name;
        private final boolean active;

        public PacketSyncWaypoint(BlockPos pos, String name, boolean active) {
            this.pos = pos;
            this.name = name;
            this.active = active;
        }

        public static void encode(PacketSyncWaypoint msg, PacketBuffer buf) {
            buf.writeBoolean(msg.active);
            if (msg.active) {
                buf.writeBlockPos(msg.pos);
                buf.writeUtf(msg.name);
            }
        }

        public static PacketSyncWaypoint decode(PacketBuffer buf) {
            boolean active = buf.readBoolean();
            if (active) {
                return new PacketSyncWaypoint(buf.readBlockPos(), buf.readUtf(32767), true);
            }
            return new PacketSyncWaypoint(null, "", false);
        }

        public static void handle(PacketSyncWaypoint msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                // Безопасный вызов клиентского менеджера на стороне клиента
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    if (msg.active) {
                        ClientWaypointManager.setTarget(msg.pos, msg.name);
                    } else {
                        ClientWaypointManager.clearTarget();
                    }
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}