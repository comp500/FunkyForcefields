package link.infra.funkyforcefields.blocks.transport;

import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

public class LiquidInputHatchBlock extends Block implements BlockEntityProvider, BlockComponentProvider {
	public LiquidInputHatchBlock(Settings settings) {
		super(settings);
	}

	VoxelShape SHAPE = Stream.of(
		Block.createCuboidShape(0, 0, 0, 16, 4, 16),
		Block.createCuboidShape(0, 4, 0, 4, 16, 16),
		Block.createCuboidShape(4, 4, 0, 16, 16, 4),
		Block.createCuboidShape(12, 4, 4, 16, 16, 16),
		Block.createCuboidShape(4, 4, 12, 12, 16, 16)
	).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView view, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new LiquidInputHatchBlockEntity();
	}

	@Override
	public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		BlockEntity be = blockView.getBlockEntity(blockPos);
		if (be instanceof LiquidInputHatchBlockEntity) {
			return ((LiquidInputHatchBlockEntity) be).hasComponent(blockView, blockPos, componentType, direction);
		}
		return false;
	}

	@Override
	public <T extends Component> T getComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		BlockEntity be = blockView.getBlockEntity(blockPos);
		if (be instanceof LiquidInputHatchBlockEntity) {
			return ((LiquidInputHatchBlockEntity) be).getComponent(blockView, blockPos, componentType, direction);
		}
		return null;
	}

	@Override
	public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockEntity be = blockView.getBlockEntity(blockPos);
		if (be instanceof LiquidInputHatchBlockEntity) {
			return ((LiquidInputHatchBlockEntity) be).getComponentTypes(blockView, blockPos, direction);
		}
		return Collections.emptySet();
	}
}
