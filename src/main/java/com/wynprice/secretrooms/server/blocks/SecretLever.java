package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class SecretLever extends SecretBaseBlock {
    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SecretLever(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        state = state.cycle(POWERED);
        boolean isPowered = state.get(POWERED);
        if (world.isRemote) {
            return true;
        } else {
            world.setBlockState(pos, state, 3);
            float pitch = isPowered ? 0.6F : 0.5F;
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
            this.updateNeighbors(world, pos);
            return true;
        }
    }

    private void updateNeighbors(World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        for (Direction direction : Direction.values()) {
            world.notifyNeighborsOfStateChange(pos.offset(direction), this);
        }
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean canProvidePower(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            if (state.get(POWERED)) {
                this.updateNeighbors(world, pos);
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }
}