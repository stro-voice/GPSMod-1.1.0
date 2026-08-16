package com.example.gpsmod.network;

import com.example.gpsmod.GPSMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(GPSMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.messageBuilder(S2CSendBeaconsPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(S2CSendBeaconsPacket::encode)
            .decoder(S2CSendBeaconsPacket::decode)
            .consumer(S2CSendBeaconsPacket::handle)
            .add();

        INSTANCE.messageBuilder(C2SSelectBeaconPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(C2SSelectBeaconPacket::encode)
            .decoder(C2SSelectBeaconPacket::decode)
            .consumer(C2SSelectBeaconPacket::handle)
            .add();
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToPlayer(Object message, ServerPlayerEntity player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}