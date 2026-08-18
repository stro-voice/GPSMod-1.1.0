package com.example.gpsmod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SaveWaypointPacket {
    private final BlockPos pos;
    private final String name;

    public SaveWaypointPacket(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public SaveWaypointPacket(PacketBuffer buf) {
        this.pos = buf.readBlockPos();
        this.name = buf.readUtf(32767);
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(this.pos);
        buf.writeUtf(this.name);
    }

    public static void handle(SaveWaypointPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        
        // ФИКС КРАША: Перенос сохранения в главный поток сервера
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            if (player != null && msg.name != null && !msg.name.trim().isEmpty()) {
                // Здесь логика сохранения точки на сервере (например, в NBT игрока)
                System.out.println("Точка [" + msg.name + "] сохранена для " + player.getName().getString() + " на " + msg.pos);
            }
        });
        
        ctx.setPacketHandled(true);
    }
}