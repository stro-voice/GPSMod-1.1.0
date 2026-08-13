package com.example.gpsmod.item;

import com.example.gpsmod.client.TabletListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TabletItem extends Item {

    public TabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClientSide) {
            // Открывает экран планшета со списком Блоков-Маяков
            Minecraft.getInstance().setScreen(new TabletListScreen());
        }
        return ActionResult.success(player.getItemInHand(hand));
    }
}