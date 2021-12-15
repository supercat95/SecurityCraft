package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseFullMineBlock extends ExplosiveBlock implements IOverlayDisplay, IBlockMine {

	private final Block blockDisguisedAs;

	public BaseFullMineBlock(Block.Properties properties, Block disguisedBlock) {
		super(properties);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext collisionContext)
	{
		if(collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity().isPresent())
		{
			Entity entity = ctx.getEntity().get();

			if(entity instanceof ItemEntity)
				return Shapes.block();
			else if(entity instanceof Player player)
			{
				BlockEntity te = world.getBlockEntity(pos);

				if(te instanceof OwnableBlockEntity ownableTe)
				{
					if(ownableTe.getOwner().isOwner(player))
						return Shapes.block();
				}
			}

			return Shapes.empty();
		}

		return Shapes.block();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity){
		if(!EntityUtils.doesEntityOwn(entity, level, pos))
			explode(level, pos);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion explosion){
		if (!world.isClientSide)
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid){
		if(!world.isClientSide)
			if(player != null && player.isCreative() && !ConfigHandler.SERVER.mineExplodesWhenInCreative.get())
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
				explode(world, pos);
				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
			}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public boolean activateMine(Level world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean defuseMine(Level world, BlockPos pos)
	{
		return false;
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		if (!world.isClientSide) {
			world.destroyBlock(pos, false);
			world.explode((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 2.5F : 5.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean dropFromExplosion(Explosion explosion){
		return false;
	}

	@Override
	public boolean isActive(Level world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(SCContent.beTypeAbstract, pos, state);
	}

	@Override
	public ItemStack getDisplayStack(Level world, BlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(Level world, BlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		if (player.isCreative() || (world.getBlockEntity(pos) instanceof OwnableBlockEntity te && te.getOwner().isOwner(player)))
			return super.getPickBlock(state, target, world, pos, player);

		return new ItemStack(blockDisguisedAs);
	}

	public Block getBlockDisguisedAs()
	{
		return blockDisguisedAs;
	}
}