package com.example.gpsmod.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class GPSBeaconBlock extends Block {

    // Список всех поставленных Маяков в мире
    public static final List<BlockPos> BEACONS = new ArrayList<>();

    public GPSBeaconBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .strength(3.0F)
                .sound(SoundType.METAL)
                .noOcclusion()); // noOcclusion нужен для корректного рендера 3D модели
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, net.minecraft.block.BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide) {
            if (!BEACONS.contains(pos)) {
                BEACONS.add(pos.immutable());
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public void onRemove(net.minecraft.block.BlockState state, World world, BlockPos pos, net.minecraft.block.BlockState newState, boolean isMoving) {
        if (!world.isClientSide && !state.is(newState.getBlock())) {
            BEACONS.remove(pos);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}