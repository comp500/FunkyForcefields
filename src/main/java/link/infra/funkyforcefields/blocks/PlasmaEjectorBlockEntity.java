package link.infra.funkyforcefields.blocks;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldRegionHolder;
import link.infra.funkyforcefields.regions.ForcefieldRegionLine;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import link.infra.funkyforcefields.transport.FluidContainerComponent;
import link.infra.funkyforcefields.transport.FluidContainerComponentImpl;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class PlasmaEjectorBlockEntity extends BlockEntity implements ForcefieldRegionHolder, Tickable, BlockComponentProvider, PropertyDelegateHolder, NameableContainerFactory, BlockEntityClientSerializable {
	private ForcefieldRegionLine region;
	private boolean queuedBlockUpdate = false;

	public PlasmaEjectorBlockEntity() {
		super(FunkyForcefields.PLASMA_EJECTOR_BLOCK_ENTITY);
	}

	protected void placeBlocks() {
		queuedBlockUpdate = true;
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
				region = null;
			}
		}
	}

	private static final float REQUIRED_PRESSURE = 2000;
	private static final float PRESSURE_PER_TICK_PER_BLOCK = 10;
	private static final float REQUIRED_TEMP = 700;

	private void updateRegion(boolean newState, boolean doBlockUpdates) {
		ForcefieldRegionManager manager = ForcefieldRegionManager.get(getWorld());
		if (manager != null) {
			if (region != null) {
				manager.removeRegion(this);
				region.cleanup(getWorld(), manager);
				region = null;
				if (newState) {
					doBlockUpdates = true;
				}
			}
		}

		if (newState && fluidContainerComponent.getContainedFluid() != null) {
			if (getCachedState().getBlock() instanceof PlasmaEjectorVertical) {
				switch (getCachedState().get(PlasmaEjectorVertical.POINTING)) {
					case UP:
						region = new ForcefieldRegionLine(pos, length, Direction.UP, getCachedState().get(PlasmaEjectorVertical.FACING), fluidContainerComponent.getContainedFluid());
						break;
					case DOWN:
						region = new ForcefieldRegionLine(pos, length, Direction.DOWN, getCachedState().get(PlasmaEjectorVertical.FACING), fluidContainerComponent.getContainedFluid());
						break;
					case SIDEWAYS:
						region = new ForcefieldRegionLine(pos, length, getCachedState().get(PlasmaEjectorVertical.FACING), getCachedState().get(PlasmaEjectorVertical.FACING), fluidContainerComponent.getContainedFluid());
				}
			} else {
				region = new ForcefieldRegionLine(pos, length, getCachedState().get(PlasmaEjectorHorizontal.FACING), Direction.UP, fluidContainerComponent.getContainedFluid());
			}
			registerRegion(region, world);
		}

		if (doBlockUpdates && region != null) {
			region.placeBlocks(world);
		}
	}

	private boolean isFluidContainerValid() {
		if (fluidContainerComponent.getContainedFluid() != null) {
			if (fluidContainerComponent.getTemperature() >= REQUIRED_TEMP) {
				return fluidContainerComponent.getPressure() >= REQUIRED_PRESSURE;
			}
		}
		return false;
	}

	private void tickFluidMagic() {
		if (isFluidContainerValid()) {
			fluidContainerComponent.setPressure(fluidContainerComponent.getPressure() - (PRESSURE_PER_TICK_PER_BLOCK * length));
			if (region == null) {
				updateRegion(true, true);
			} else if (region.getForcefieldFluid() != fluidContainerComponent.getContainedFluid()) {
				updateRegion(true, true);
			} else if (queuedBlockUpdate) {
				region.placeBlocks(world);
			}
			queuedBlockUpdate = false;
			return;
		} else {
			if (region != null) {
				updateRegion(false, true);
			}
		}

		if (region != null) {
			updateRegion(false, true);
		}
	}

	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			// TODO: cache component connection?
			Direction dir = null;
			if (getCachedState().getBlock() instanceof PlasmaEjectorVertical) {
				switch (getCachedState().get(PlasmaEjectorVertical.POINTING)) {
					case UP:
						dir = Direction.DOWN;
						break;
					case DOWN:
						dir = Direction.UP;
						break;
					case SIDEWAYS:
						dir = getCachedState().get(PlasmaEjectorVertical.FACING).getOpposite();
				}
			} else {
				dir = getCachedState().get(PlasmaEjectorHorizontal.FACING).getOpposite();
			}
			BlockPos neighborPos = pos.offset(dir, 1);
			BlockEntity be = world.getBlockEntity(neighborPos);
			if (be instanceof BlockComponentProvider && ((BlockComponentProvider) be).hasComponent(world, neighborPos, FluidContainerComponent.TYPE, dir.getOpposite())) {
				fluidContainerComponent.tick(((BlockComponentProvider) be).getComponent(world, neighborPos, FluidContainerComponent.TYPE, dir.getOpposite()));
			} else {
				fluidContainerComponent.tick();
			}

			tickFluidMagic();
		}
	}

	private final FluidContainerComponentImpl fluidContainerComponent = new FluidContainerComponentImpl(0, 0.3f);

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		fluidContainerComponent.fromTag(tag);
		length = tag.getInt("length");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		tag = fluidContainerComponent.toTag(tag);
		tag.putInt("length", length);
		return tag;
	}

	@Override
	public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		if (componentType != FluidContainerComponent.TYPE) {
			return false;
		}
		if (direction == null) {
			return true;
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

	public int length = 3;

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return new PropertyDelegate() {
			@Override
			public int get(int index) {
				return length;
			}

			@Override
			public void set(int index, int value) {
				length = value;
				markDirty();
			}

			@Override
			public int size() {
				return 1;
			}
		};
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("block.funkyforcefields.plasma_ejector");
	}

	@Nullable
	@Override
	public Container createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new PlasmaEjectorController(syncId, inv, BlockContext.create(world, pos));
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag) {
		length = compoundTag.getInt("length");
	}

	@Override
	public CompoundTag toClientTag(CompoundTag compoundTag) {
		compoundTag.putInt("length", length);
		return compoundTag;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (world != null && !world.isClient()) {
			updateRegion(isFluidContainerValid(), true);
			sync();
		}
	}
}
