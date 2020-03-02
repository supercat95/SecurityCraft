package net.geforcemods.securitycraft.renderers;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.SCPistonTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SCPistonTileEntityRenderer extends TileEntityRenderer<SCPistonTileEntity> {
	private BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

	@Override
	public void render(SCPistonTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		BlockPos blockpos = tileEntityIn.getPos().offset(tileEntityIn.getMotionDirection().getOpposite());
		BlockState blockstate = tileEntityIn.getPistonState();
		if (!blockstate.isAir() && !(tileEntityIn.getProgress(partialTicks) >= 1.0F)) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			if (Minecraft.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			BlockModelRenderer.enableCache();
			bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
			bufferbuilder.setTranslation(x - blockpos.getX() + tileEntityIn.getOffsetX(partialTicks), y - blockpos.getY() + tileEntityIn.getOffsetY(partialTicks), z - blockpos.getZ() + tileEntityIn.getOffsetZ(partialTicks));
			World world = this.getWorld();
			if (blockstate.getBlock() == SCContent.PISTON_HEAD.get() && tileEntityIn.getProgress(partialTicks) <= 4.0F) {
				blockstate = blockstate.with(PistonHeadBlock.SHORT, Boolean.valueOf(true));
				this.renderStateModel(blockpos, blockstate, bufferbuilder, world, false);
			} else if (tileEntityIn.shouldPistonHeadBeRendered() && !tileEntityIn.isExtending()) {
				PistonType pistontype = blockstate.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState blockstate1 = SCContent.PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.TYPE, pistontype).with(PistonHeadBlock.FACING, blockstate.get(PistonBlock.FACING));
				blockstate1 = blockstate1.with(PistonHeadBlock.SHORT, Boolean.valueOf(tileEntityIn.getProgress(partialTicks) >= 0.5F));
				this.renderStateModel(blockpos, blockstate1, bufferbuilder, world, false);
				BlockPos blockpos1 = blockpos.offset(tileEntityIn.getMotionDirection());
				bufferbuilder.setTranslation(x - blockpos1.getX(), y - blockpos1.getY(), z - blockpos1.getZ());
				blockstate = blockstate.with(PistonBlock.EXTENDED, Boolean.valueOf(true));
				this.renderStateModel(blockpos1, blockstate, bufferbuilder, world, true);
			} else {
				this.renderStateModel(blockpos, blockstate, bufferbuilder, world, false);
			}

			bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
			BlockModelRenderer.disableCache();
			RenderHelper.enableStandardItemLighting();
		}
	}

	private boolean renderStateModel(BlockPos pos, BlockState state, BufferBuilder buffer, World p_188186_4_, boolean checkSides) {
		if (blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		return this.blockRenderer.getBlockModelRenderer().renderModel(p_188186_4_, this.blockRenderer.getModelForState(state), state, pos, buffer, checkSides, new Random(), state.getPositionRandom(pos));
	}
}