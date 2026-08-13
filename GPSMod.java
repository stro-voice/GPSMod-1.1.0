package com.example.gpsmod;

import com.example.gpsmod.client.ClientInputHandler;
import com.example.gpsmod.client.GpsMinimapHud;
import com.example.gpsmod.client.KeyBindings;
import com.example.gpsmod.client.WorldRouteRenderer;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("gpsmod")
public class GPSMod {
    public static final String MOD_ID = "gpsmod";

    public GPSMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(GpsMinimapHud.class);
        MinecraftForge.EVENT_BUS.register(WorldRouteRenderer.class);
        MinecraftForge.EVENT_BUS.register(ClientInputHandler.class);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        KeyBindings.register();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("gps")
                .then(Commands.argument("target", BlockPosArgument.blockPos())
                    .executes(context -> {
                        BlockPos target = BlockPosArgument.getLoadedBlockPos(context, "target");
                        GPSManager.setTarget(target);
                        context.getSource().sendSuccess(new StringTextComponent("§a[GPS] Маршрут проложен к: " + target.getX() + ", " + target.getY() + ", " + target.getZ()), false);
                        return 1;
                    }))
                .then(Commands.literal("clear")
                    .executes(context -> {
                        GPSManager.clearTarget();
                        context.getSource().sendSuccess(new StringTextComponent("§c[GPS] Маршрут сброшен"), false);
                        return 1;
                    }))
        );
    }
}