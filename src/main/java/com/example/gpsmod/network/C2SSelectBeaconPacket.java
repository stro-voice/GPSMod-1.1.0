package com.example.gpsmod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
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
        if (msg.target != null) buf.writeBlockPos(msg.target);
    }

    public static C2SSelectBeaconPacket decode(PacketBuffer buf) {
        boolean hasTarget = buf.readBoolean();
        return new C2SSelectBeaconPacket(hasTarget ? buf.readBlockPos() : null);
    }

    public static void handle(C2SSelectBeaconPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                ItemStack map = player.getMainHandItem();
                if (map.getItem() == Items.FILLED_MAP) {
                    CompoundNBT tag = map.getOrCreateTag();
                    if (msg.target != null) {
                        tag.putInt("TargetX", msg.target.getX());
                        tag.putInt("TargetZ", msg.target.getZ());
                        tag.putBoolean("HasGPS", true);
                    } else {
                        tag.remove("HasGPS");
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}