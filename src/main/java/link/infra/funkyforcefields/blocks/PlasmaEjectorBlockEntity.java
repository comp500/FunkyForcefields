package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import link.infra.funkyforcefields.regions.ForcefieldRegionHolder;
import link.infra.funkyforcefields.regions.ForcefieldRegionLine;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import link.infra.funkyforcefields.transport.FluidContainerComponent;
import link.infra.funkyforcefields.transport.FluidContainerComponentImpl;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Collections;
import java.util.Set;

public class PlasmaEjectorBlockEntity extends BlockEntity implements ForcefieldRegionHolder, Tickable, BlockComponentProvider {
	private ForcefieldRegionLine region;
	private boolean queueBlockPlace = false;

	public PlasmaEjectorBlockEntity() {
		super(FunkyForcefields.PLASMA_EJECTOR_BLOCK_ENTITY);
	}

	protected void placeBlocks() {
		queueBlockPlace = true;
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		if (getWorld() != null && !getWorld().isClient()) {
			ForcefieldRegionManager manager = ForcefieldRegionManager.get(getWorld());
			if (manager != null) {
				manager.removeRegion(this);
				if (region != null) {
					region.cleanup(getWorld(), manager);
				}
			}
		}
	}

	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			if (region == null) {
				BlockState state = world.getBlockState(pos);
				if (state.getBlock() instanceof PlasmaEjectorVertical) {
					// TODO: fluid system, length
					switch (state.get(PlasmaEjectorVertical.POINTING)) {
						case UP:
							region = new ForcefieldRegionLine(pos, 10, Direction.UP, state.get(PlasmaEjectorVertical.FACING), ForcefieldFluids.GLASS);
							break;
						case DOWN:
							region = new ForcefieldRegionLine(pos, 10, Direction.DOWN, state.get(PlasmaEjectorVertical.FACING), ForcefieldFluids.GLASS);
							break;
						case SIDEWAYS:
							region = new ForcefieldRegionLine(pos, 10, state.get(PlasmaEjectorVertical.FACING), state.get(PlasmaEjectorVertical.FACING), ForcefieldFluids.GLASS);
					}
				} else {
					region = new ForcefieldRegionLine(pos, 10, state.get(PlasmaEjectorHorizontal.FACING), Direction.UP, ForcefieldFluids.GLASS);
				}
				registerRegion(region, world);
			}

			if (queueBlockPlace) {
				region.placeBlocks(world);
				queueBlockPlace = false;
			}
		}
	}

	private final FluidContainerComponent fluidContainerComponent = new FluidContainerComponentImpl(0, 0.3f);

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
		BlockState state = blockView.getBlockState(blockPos);
		if (state.getBlock() instanceof PlasmaEjectorVertical) {
			switch (state.get(PlasmaEjectorVertical.POINTING)) {
				case UP:
					return Direction.DOWN == direction;
				case DOWN:
					return Direction.UP == direction;
				case SIDEWAYS:
					return state.get(PlasmaEjectorVertical.FACING).getOpposite() == direction;
			}
		} else {
			return state.get(PlasmaEjectorHorizontal.FACING).getOpposite() == direction;
		}
		return false;
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
}
