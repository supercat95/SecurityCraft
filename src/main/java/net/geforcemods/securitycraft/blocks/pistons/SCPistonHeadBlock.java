package net.geforcemods.securitycraft.blocks.pistons;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class SCPistonHeadBlock extends PistonHeadBlock {

	public SCPistonHeadBlock(Block.Properties properties) {
		super(properties);
	}

	/**
	 * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
	 * this block
	 */
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote && player.abilities.isCreativeMode) {
			BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
			Block block = worldIn.getBlockState(blockpos).getBlock();
			if (block == SCContent.REINFORCED_PISTON.get() || block == SCContent.REINFORCED_STICKY_PISTON.get()) {
				worldIn.removeBlock(blockpos, false);
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			super.onReplaced(state, worldIn, pos, newState, isMoving);
			Direction direction = state.get(FACING).getOpposite();
			pos = pos.offset(direction);
			BlockState blockstate = worldIn.getBlockState(pos);
			if ((blockstate.getBlock() == SCContent.REINFORCED_PISTON.get() || blockstate.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get()) && blockstate.get(PistonBlock.EXTENDED)) {
				spawnDrops(blockstate, worldIn, pos);
				worldIn.removeBlock(pos, false);
			}

		}
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite())).getBlock();
		return block == SCContent.REINFORCED_PISTON.get() || block == SCContent.REINFORCED_STICKY_PISTON.get() || block == SCContent.MOVING_PISTON.get();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return new ItemStack(state.get(TYPE) == PistonType.STICKY ? SCContent.REINFORCED_STICKY_PISTON.get() : SCContent.REINFORCED_PISTON.get());
	}

}