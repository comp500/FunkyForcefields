package link.infra.funkyforcefields.blocks.transport;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.transport.FluidContainerComponent;
import link.infra.funkyforcefields.transport.FluidContainerComponentImpl;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PipeBlockEntity extends BlockEntity implements Tickable, BlockComponentProvider {
	public PipeBlockEntity() {
		super(FunkyForcefields.PIPE_BLOCK_ENTITY);
	}

	private final FluidContainerComponentImpl fluidContainerComponent = new FluidContainerComponentImpl(15, 0.2f);

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		fluidContainerComponent.fromTag(tag);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		return fluidContainerComponent.toTag(tag);
	}

	@Override
	public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		if (componentType != FluidContainerComponent.TYPE) {
			return false;
		}
		if (direction == null) {
			return true;
		}
		// TODO: check side open stuff
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T getComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		if (hasComponent(blockView, blockPos, componentType, direction)) {
			return (T) fluidContainerComponent;
		}
		return null;
	}

	@Override
	public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos blockPos, Direction direction) {
		return Collections.singleton(FluidContainerComponent.TYPE);
	}

	private static class FireFluidContainerComponent implements FluidContainerComponent {
		private final ForcefieldFluid fluid;
		private final float pressure;
		public FireFluidContainerComponent(ForcefieldFluid fluid, float pressure) {
			this.fluid = fluid;
			this.pressure = pressure;
		}

		@Override
		public float getContainerVolume() {
			return 0;
		}

		@Override
		public float getPressure() {
			return pressure;
		}

		@Override
		public float getThermalDiffusivity() {
			return 0;
		}

		@Override
		public float getTemperature() {
			return 1000;
		}

		@Override
		public ForcefieldFluid getContainedFluid() {
			return fluid;
		}

		@Override
		public void fromTag(CompoundTag tag) {
			throw new RuntimeException("FireFluidContainerComponent is fake!!!");
		}

		@Override
		public CompoundTag toTag(CompoundTag tag) {
			throw new RuntimeException("FireFluidContainerComponent is fake!!!");
		}
	}

	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			// TODO: store directions with connected components
			List<FluidContainerComponent> neighbors = new ArrayList<>();
			for (Direction dir : Direction.values()) {
				BlockPos neighborPos = pos.offset(dir, 1);
				BlockEntity be = world.getBlockEntity(neighborPos);
				if (be instanceof BlockComponentProvider && ((BlockComponentProvider) be).hasComponent(world, neighborPos, FluidContainerComponent.TYPE, dir.getOpposite())) {
					neighbors.add(((BlockComponentProvider) be).getComponent(world, neighborPos, FluidContainerComponent.TYPE, dir.getOpposite()));
				}
			}
			if (world.getBlockState(pos.offset(Direction.DOWN)).getBlock() instanceof FireBlock) {
				// TODO: make this not affect the neighbor pressure somehow?
				neighbors.add(new FireFluidContainerComponent(fluidContainerComponent.getContainedFluid(), fluidContainerComponent.getPressure()));
			}
			fluidContainerComponent.tick(neighbors.toArray(new FluidContainerComponent[0]));
		}
	}
}
