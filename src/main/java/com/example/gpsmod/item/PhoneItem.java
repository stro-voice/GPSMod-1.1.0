package com.example.gpsmod.item;

import com.example.gpsmod.client.PhoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhoneItem extends Item {

    public PhoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClientSide && hand == Hand.MAIN_HAND) {
            Minecraft.getInstance().setScreen(new PhoneScreen(player.getItemInHand(hand)));
        }
        return ActionResult.success(player.getItemInHand(hand));
    }

    public static void setSelectedBeacon(ItemStack phoneStack, BlockPos pos) {
        CompoundNBT tag = phoneStack.getOrCreateTag();
        tag.putInt("TargetX", pos.getX());
        tag.putInt("TargetY", pos.getY());
        tag.putInt("TargetZ", pos.getZ());
        tag.putBoolean("HasTarget", true);
    }

    public static BlockPos getSelectedBeacon(ItemStack phoneStack) {
        CompoundNBT tag = phoneStack.getTag();
        if (tag != null && tag.getBoolean("HasTarget")) {
            return new BlockPos(tag.getInt("TargetX"), tag.getInt("TargetY"), tag.getInt("TargetZ"));
        }
        return null;
    }
}