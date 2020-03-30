package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.regions.ForcefieldRegion;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import link.infra.funkyforcefields.util.EntityContextBypasser;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class VerticalForcefieldBlock extends ForcefieldBlock {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	private final ForcefieldFluid fluid;

	public VerticalForcefieldBlock(ForcefieldFluid fluid) {
		super(FabricBlockSettings.of(Material.BARRIER).nonOpaque().strength(-1.0F, 3600000.0F).dropsNothing().build());
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
		this.fluid = fluid;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

	private static final VoxelShape NS = VoxelShapes.cuboid(0.495f, 0f, 0f, 0.505f, 1f, 1f);
	private static final VoxelShape EW = VoxelShapes.cuboid(0f, 0f, 0.495f, 1f, 1f, 0.505f);

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		Direction dir = state.get(FACING);
		switch (dir) {
			case NORTH:
			case SOUTH:
				return NS;
			case EAST:
			case WEST:
				return EW;
			default:
				return VoxelShapes.fullCube();
		}
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
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
		fluid.displayTick(world, pos, random, getOutlineShape(state, world, pos, EntityContext.absent()));
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
