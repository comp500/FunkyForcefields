package link.infra.funkyforcefields;

import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import link.infra.funkyforcefields.regions.ForcefieldType;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VerticalForcefield extends HorizontalFacingBlock {
	private final ForcefieldType type;

	public VerticalForcefield(ForcefieldType type) {
		super(FabricBlockSettings.of(Material.BARRIER).nonOpaque().strength(-1.0F, 3600000.0F).dropsNothing().build());
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
		this.type = type;
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
			if (ent instanceof ItemEntity) {
				return VoxelShapes.empty();
			}
		}
		return super.getCollisionShape(state, view, pos, context);
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
				// TODO: check if region matches type
				if (manager.queryRegion(pos) == null) {
					world.removeBlock(pos, false);
				}
			}
		}
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}
}
