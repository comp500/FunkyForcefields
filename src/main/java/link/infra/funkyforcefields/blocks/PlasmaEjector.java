package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldRegion;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import link.infra.funkyforcefields.util.CursedPointingDirection;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class PlasmaEjector extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<CursedPointingDirection> POINTING = EnumProperty.of("pointing", CursedPointingDirection.class);

	public PlasmaEjector() {
		super(FabricBlockSettings.of(Material.BARRIER).build());
		setDefaultState(this.stateManager.getDefaultState()
			.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
			.with(POINTING, CursedPointingDirection.SIDEWAYS));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
		stateManager.add(POINTING);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		CursedPointingDirection pointy = CursedPointingDirection.of(ctx.getPlayerLookDirection().getOpposite());
		Direction facey = ctx.getPlayerFacing();
		if (pointy != CursedPointingDirection.SIDEWAYS) {
			facey = facey.rotateYCounterclockwise();
		}
		return this.getDefaultState()
			.with(FACING, facey)
			.with(POINTING, pointy);
	}

	// oh no

	private static final VoxelShape NORTH_SIDE = VoxelShapes.union(
		VoxelShapes.cuboid(0f, 0f, 0.1875f, 1f, 1f, 1f),
		VoxelShapes.cuboid(0.375f, 0f, 0f, 0.625f, 1f, 1f)
	);
	private static final VoxelShape SOUTH_SIDE = VoxelShapes.union(
		VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.8125f),
		VoxelShapes.cuboid(0.375f, 0f, 0f, 0.625f, 1f, 1f)
	);
	private static final VoxelShape EAST_SIDE = VoxelShapes.union(
		VoxelShapes.cuboid(0f, 0f, 0f, 0.8125f, 1f, 1f),
		VoxelShapes.cuboid(0f, 0f, 0.375f, 1f, 1f, 0.625f)
	);
	private static final VoxelShape WEST_SIDE = VoxelShapes.union(
		VoxelShapes.cuboid(0.1875f,  0f, 0f, 1f, 1f, 1f),
		VoxelShapes.cuboid(0f, 0f, 0.625f, 1f, 1f, 0.375f)
	);
	private static final VoxelShape NORTH_UP = Stream.of(
		Block.createCuboidShape(11, 13, 0, 16, 16, 16),
		Block.createCuboidShape(0, 13, 0, 5, 16, 16),
		Block.createCuboidShape(0, 0, 0, 16, 13, 16),
		Block.createCuboidShape(6, 13, 0, 10, 16, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape EAST_UP = Stream.of(
		Block.createCuboidShape(0, 13, 11, 16, 16, 16),
		Block.createCuboidShape(0, 13, 0, 16, 16, 5),
		Block.createCuboidShape(0, 0, 0, 16, 13, 16),
		Block.createCuboidShape(0, 13, 6, 16, 16, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape SOUTH_UP = Stream.of(
		Block.createCuboidShape(0, 13, 0, 5, 16, 16),
		Block.createCuboidShape(11, 13, 0, 16, 16, 16),
		Block.createCuboidShape(0, 0, 0, 16, 13, 16),
		Block.createCuboidShape(6, 13, 0, 10, 16, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape WEST_UP = Stream.of(
		Block.createCuboidShape(0, 13, 0, 16, 16, 5),
		Block.createCuboidShape(0, 13, 11, 16, 16, 16),
		Block.createCuboidShape(0, 0, 0, 16, 13, 16),
		Block.createCuboidShape(0, 13, 6, 16, 16, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
	
	private static final VoxelShape NORTH_DOWN = Stream.of(
		Block.createCuboidShape(0, 3, 0, 16, 16, 16),
		Block.createCuboidShape(6, 0, 0, 10, 3, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape EAST_DOWN = Stream.of(
		Block.createCuboidShape(0, 3, 0, 16, 16, 16),
		Block.createCuboidShape(0, 0, 6, 16, 3, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape SOUTH_DOWN = Stream.of(
		Block.createCuboidShape(0, 3, 0, 16, 16, 16),
		Block.createCuboidShape(6, 0, 0, 10, 3, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	private static final VoxelShape WEST_DOWN = Stream.of(
		Block.createCuboidShape(0, 3, 0, 16, 16, 16),
		Block.createCuboidShape(0, 0, 6, 16, 3, 10)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		Direction dir = state.get(FACING);
		switch (state.get(POINTING)) {
			case UP:
				switch (dir) {
					case NORTH:
						return NORTH_UP;
					case SOUTH:
						return SOUTH_UP;
					case EAST:
						return EAST_UP;
					case WEST:
						return WEST_UP;
					default:
						return VoxelShapes.fullCube();
				}
			case DOWN:
				switch (dir) {
					case NORTH:
						return NORTH_DOWN;
					case SOUTH:
						return SOUTH_DOWN;
					case EAST:
						return EAST_DOWN;
					case WEST:
						return WEST_DOWN;
					default:
						return VoxelShapes.fullCube();
				}
			case SIDEWAYS:
				switch (dir) {
					case NORTH:
						return NORTH_SIDE;
					case SOUTH:
						return SOUTH_SIDE;
					case EAST:
						return EAST_SIDE;
					case WEST:
						return WEST_SIDE;
					default:
						return VoxelShapes.fullCube();
				}
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
