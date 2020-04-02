package link.infra.funkyforcefields.blocks.transport;

import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Set;

public class LiquidInputHatchBlock extends Block implements BlockEntityProvider, BlockComponentProvider, Tickable {
	public LiquidInputHatchBlock(Settings settings) {
		super(settings);
	}

	@Override
	public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
		return false;
	}

	@Override
	public <T extends Component> T getComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
		return null;
	}

	@Override
	public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos pos, Direction side) {
		return null;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return null;
	}

	@Override
	public void tick() {

	}
}
