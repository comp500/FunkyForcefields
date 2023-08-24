package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.regions.ForcefieldRegion;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import link.infra.funkyforcefields.util.EntityContextBypasser;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class ForcefieldBlockHorizontal extends ForcefieldBlock {
	private final ForcefieldFluid fluid;

	public ForcefieldBlockHorizontal(ForcefieldFluid fluid) {
		super(FabricBlockSettings.of(Material.BARRIER).nonOpaque().strength(-1.0F, 3600000.0F).dropsNothing());
		this.fluid = fluid;
	}

	private static final VoxelShape SHAPE = VoxelShapes.cuboid(0f, 0.990f, 0f, 1f, 1f, 1f);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		Entity ent = context instanceof EntityContextBypasser ? ((EntityContextBypasser) context).getUnderlyingEntity() : null;
		if (ent != null) {
			if (fluid.allowsEntity(ent)) {
				return VoxelShapes.empty();
			}
		}
		return super.getCollisionShape(state, view, pos, context);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		fluid.applyCollisionEffect(world, pos, entity);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		fluid.displayTick(world, pos, random, getOutlineShape(state, world, pos, ShapeContext.absent()));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return fluid.hasModel() ? super.getRenderType(state) : BlockRenderType.INVISIBLE;
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (!world.isClient) {
			ForcefieldRegionManager manager = ForcefieldRegionManager.get(world);
			if (manager != null) {
				ForcefieldRegion reg = manager.queryRegion(pos);
				if (reg == null) {
					world.removeBlock(pos, false);
				} else {
					if (!reg.isValidBlock(state)) {
						reg.revalidateBlock(world, pos);
					}
					if (neighborPos != null && reg.containsCoordinate(neighborPos)) {
						BlockState bs = world.getBlockState(neighborPos);
						if (bs.isAir()) {
							reg.placeBlocks(world);
						}
					}
				}
			}
		}
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}

	@Override
	public ForcefieldFluid getFluid() {
		return fluid;
	}
}
