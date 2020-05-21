package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorTileEntityRenderer extends TileEntityRenderer<ProjectorTileEntity> {

	public ProjectorTileEntityRenderer(TileEntityRendererDispatcher terd) 
	{
		super(terd);
	}

	@Override
	public void render(ProjectorTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, int arg5) 
	{
		for(int i = 0; i < te.getProjectionWidth(); i++) {
			for(int j = 0; j < te.getProjectionWidth(); j++) {
				stack.push();

				BlockPos pos = translateProjection(te, stack, te.getBlockState().get(ProjectorBlock.FACING), i, j, te.getProjectionRange(), te.getProjectionOffset());

				RenderSystem.disableCull();
				// Values of around 220 - 240 make the block appear, but lower values make the model progressively darker?
				Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.STONE.getDefaultState(), stack, buffer, LightTexture.packLight(te.getWorld().getLightFor(LightType.BLOCK, pos), te.getWorld().getLightFor(LightType.SKY, pos)), OverlayTexture.NO_OVERLAY);
				RenderSystem.enableCull();

				stack.pop();
			}
		}
	}

	/**
	 * Shifts the projection depending on the offset and range set in the projector
	 * 
	 * @return The BlockPos of the fake block to be drawn
	 */
	private BlockPos translateProjection(ProjectorTileEntity te, MatrixStack stack, Direction direction, int x, int y, double distance, double offset) 
	{
		BlockPos pos;

		if(direction == Direction.NORTH) {
			pos = new BlockPos(0.0D + x + offset, 0.0D + y, distance);
			stack.translate(0.0D + x + offset, 0.0D + y, distance);
		}
		else if(direction == Direction.SOUTH) {
			pos = new BlockPos(0.0D + x + offset, 0.0D + y, -distance);
			stack.translate(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if(direction == Direction.WEST) {
			pos = new BlockPos(distance, 0.0D + y, 0.0D + x + offset);
			stack.translate(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if(direction == Direction.EAST) {
			pos = new BlockPos(-distance, 0.0D + y, 0.0D + x + offset);
			stack.translate(-distance, 0.0D + y, 0.0D + x + offset);
		}
		else {
			stack.translate(0.0D, 0.0D, 0.0D);
			return te.getPos();
		}
		
		return pos;
	}
}
