package com.example.gpsmod.network;

import com.example.gpsmod.GPSMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GPSMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, C2SRequestBeaconsPacket.class, 
                C2SRequestBeaconsPacket::encode, 
                C2SRequestBeaconsPacket::decode, 
                C2SRequestBeaconsPacket::handle);

        CHANNEL.registerMessage(id++, S2CSendBeaconsPacket.class, 
                S2CSendBeaconsPacket::encode, 
                S2CSendBeaconsPacket::decode, 
                S2CSendBeaconsPacket::handle);

        CHANNEL.registerMessage(id++, C2SSelectBeaconPacket.class, 
                C2SSelectBeaconPacket::encode, 
                C2SSelectBeaconPacket::decode, 
                C2SSelectBeaconPacket::handle);
    }
}