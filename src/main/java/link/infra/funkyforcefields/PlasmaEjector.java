package link.infra.funkyforcefields;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
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

public class PlasmaEjector extends HorizontalFacingBlock implements BlockEntityProvider {
	public PlasmaEjector() {
		super(FabricBlockSettings.of(Material.BARRIER).build());
		setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		Direction dir = state.get(FACING);
		switch (dir) {
			case NORTH:
				return VoxelShapes.union(
					VoxelShapes.cuboid(0f, 0f, 0.1875f, 1f, 1f, 1f),
					VoxelShapes.cuboid(0.375f, 0f, 0f, 0.625f, 1f, 1f)
				);
			case SOUTH:
				return VoxelShapes.union(
					VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.8125f),
					VoxelShapes.cuboid(0.375f, 0f, 0f, 0.625f, 1f, 1f)
				);
			case EAST:
				return VoxelShapes.union(
					VoxelShapes.cuboid(0f, 0f, 0f, 0.8125f, 1f, 1f),
					VoxelShapes.cuboid(0f, 0f, 0.375f, 1f, 1f, 0.625f)
				);
			case WEST:
				return VoxelShapes.union(
					VoxelShapes.cuboid(0.1875f,  0f, 0f, 1f, 1f, 1f),
					VoxelShapes.cuboid(0f, 0f, 0.625f, 1f, 1f, 0.375f)
				);
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
}
