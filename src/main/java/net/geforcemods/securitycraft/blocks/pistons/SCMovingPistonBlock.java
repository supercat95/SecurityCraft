package net.geforcemods.securitycraft.blocks.pistons;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.tileentity.SCPistonTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MovingPistonBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class SCMovingPistonBlock extends MovingPistonBlock {

	public SCMovingPistonBlock(Block.Properties properties) {
		super(properties);
	}

	public static TileEntity createTilePiston(BlockState p_196343_0_, CompoundNBT tag, Direction p_196343_1_, boolean p_196343_2_, boolean p_196343_3_) {
		return new SCPistonTileEntity(p_196343_0_, tag, p_196343_1_, p_196343_2_, p_196343_3_);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SCPistonTileEntity) {
				((SCPistonTileEntity)tileentity).clearPistonTileEntity();
			}

		}
	}

	/**
	 * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
	 */
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.getBlock() instanceof ReinforcedPistonBlock && blockstate.get(PistonBlock.EXTENDED)) {
			worldIn.removeBlock(blockpos, false);
		}

	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		SCPistonTileEntity SCPistonTileEntity = this.getTileEntity(builder.getWorld(), builder.assertPresent(LootParameters.POSITION));
		return SCPistonTileEntity == null ? Collections.emptyList() : SCPistonTileEntity.getPistonState().getDrops(builder);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		SCPistonTileEntity SCPistonTileEntity = this.getTileEntity(worldIn, pos);
		return SCPistonTileEntity != null ? SCPistonTileEntity.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
	}

	@Nullable
	private SCPistonTileEntity getTileEntity(IBlockReader p_220170_1_, BlockPos p_220170_2_) {
		TileEntity tileentity = p_220170_1_.getTileEntity(p_220170_2_);
		return tileentity instanceof SCPistonTileEntity ? (SCPistonTileEntity)tileentity : null;
	}
}