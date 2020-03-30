package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldRegion;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PlasmaEjectorHorizontal extends HorizontalFacingBlock implements BlockEntityProvider {
	public PlasmaEjectorHorizontal() {
		super(FabricBlockSettings.of(Material.BARRIER).build());
		setDefaultState(this.stateManager.getDefaultState()
			.with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	private static final VoxelShape NORTH = VoxelShapes.combineAndSimplify(
		Block.createCuboidShape(0, 0, 3, 16, 16, 16),
		Block.createCuboidShape(0, 12, 0, 16, 16, 3), BooleanBiFunction.OR);
	private static final VoxelShape EAST = VoxelShapes.combineAndSimplify(
		Block.createCuboidShape(0, 0, 0, 13, 16, 16),
		Block.createCuboidShape(13, 12, 0, 16, 16, 16), BooleanBiFunction.OR);
	private static final VoxelShape SOUTH = VoxelShapes.combineAndSimplify(
		Block.createCuboidShape(0, 0, 0, 16, 16, 13),
		Block.createCuboidShape(0, 12, 13, 16, 16, 16), BooleanBiFunction.OR);
	private static final VoxelShape WEST = VoxelShapes.combineAndSimplify(
		Block.createCuboidShape(3, 0, 0, 16, 16, 16),
		Block.createCuboidShape(0, 12, 0, 3, 16, 16), BooleanBiFunction.OR);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		switch (state.get(FACING)) {
			case NORTH:
				return NORTH;
			case SOUTH:
				return SOUTH;
			case EAST:
				return EAST;
			case WEST:
				return WEST;
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new PlasmaEjectorBlockEntity();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (world.isClient) {
			return;
		}
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof PlasmaEjectorBlockEntity) {
			((PlasmaEjectorBlockEntity) be).placeBlocks();
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (!world.isClient) {
			ForcefieldRegionManager manager = ForcefieldRegionManager.get(world);
			if (manager != null && neighborPos != null) {
				ForcefieldRegion reg = manager.queryRegion(neighborPos);
				if (reg != null) {
					if (world.getBlockState(neighborPos).isAir()) {
						reg.placeBlocks(world);
					}
				}
			}
		}
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}
}