package com.example.gpsmod.network;

import com.example.gpsmod.data.BeaconSavedData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRegisterBeaconPacket {
    private final BlockPos pos;
    private final String name;

    public C2SRegisterBeaconPacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public static void encode(C2SRegisterBeaconPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.name);
    }

    public static C2SRegisterBeaconPacket decode(PacketBuffer buf) {
        return new C2SRegisterBeaconPacket(buf.readBlockPos(), buf.readUtf(32767));
    }

    public static void handle(C2SRegisterBeaconPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player != null) {
                ServerWorld world = player.getLevel();
                BeaconSavedData savedData = BeaconSavedData.get(world);
                savedData.addBeacon(msg.pos, msg.name);

                player.sendMessage(
                    new StringTextComponent("🚩 Флаг '")
                        .append(new StringTextComponent(msg.name).withStyle(TextFormatting.YELLOW))
                        .append("' сохранен!"),
                    player.getUUID()
                );
            }
        });
        context.setPacketHandled(true);
    }
}