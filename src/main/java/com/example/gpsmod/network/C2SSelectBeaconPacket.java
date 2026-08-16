package com.example.gpsmod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SSelectBeaconPacket {
    private final BlockPos targetPos;

    public C2SSelectBeaconPacket(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public static void encode(C2SSelectBeaconPacket msg, PacketBuffer buf) {
        boolean hasPos = msg.targetPos != null;
        buf.writeBoolean(hasPos);
        if (hasPos) {
            buf.writeBlockPos(msg.targetPos);
        }
    }

    public static C2SSelectBeaconPacket decode(PacketBuffer buf) {
        boolean hasPos = buf.readBoolean();
        BlockPos pos = hasPos ? buf.readBlockPos() : null;
        return new C2SSelectBeaconPacket(pos);
    }

    public static void handle(C2SSelectBeaconPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player != null) {
                if (msg.targetPos != null) {
                    player.sendMessage(
                        new StringTextComponent("🎯 Маршрут установлен на: ")
                            .append(new StringTextComponent("X: " + msg.targetPos.getX() + " Z: " + msg.targetPos.getZ())
                            .withStyle(TextFormatting.GREEN)),
                        player.getUUID()
                    );
                } else {
                    player.sendMessage(
                        new StringTextComponent("❌ Маршрут сброшен").withStyle(TextFormatting.RED),
                        player.getUUID()
                    );
                }
            }
        });
        context.setPacketHandled(true);
    }
}