package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldRegion;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Set;

public class PlasmaEjectorHorizontal extends HorizontalFacingBlock implements BlockEntityProvider, BlockComponentProvider {
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

	@Override
	public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		BlockEntity be = blockView.getBlockEntity(blockPos);
		if (be instanceof PlasmaEjectorBlockEntity) {
			return ((PlasmaEjectorBlockEntity) be).hasComponent(blockView, blockPos, componentType, direction);
		}
		return false;
	}

	@Override
	public <T extends Component> T getComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		BlockEntity be = blockView.getBlockEntity(blockPos);
		if (be instanceof PlasmaEjectorBlockEntity) {
			return ((PlasmaEjectorBlockEntity) be).getComponent(blockView, blockPos, componentType, direction);
		}
		return null;
	}

	@Override
	public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockEntity be = blockView.getBlockEntity(blockPos);
		if (be instanceof PlasmaEjectorBlockEntity) {
			return ((PlasmaEjectorBlockEntity) be).getComponentTypes(blockView, blockPos, direction);
		}
		return Collections.emptySet();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.PASS;

		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof PlasmaEjectorBlockEntity) {
			ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(FunkyForcefields.MODID, "plasma_ejector"),
				player, (packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
		}

		return ActionResult.SUCCESS;
	}
}
