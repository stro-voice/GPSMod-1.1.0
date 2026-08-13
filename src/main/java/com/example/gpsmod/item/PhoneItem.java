package com.example.gpsmod.item;

import com.example.gpsmod.client.PhoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class PhoneItem extends Item {

    public PhoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClientSide) {
            // При ПКМ открываем экран выбора
            Minecraft.getInstance().setScreen(new PhoneScreen());
        }
        return ActionResult.success(player.getItemInHand(hand));
    }
}