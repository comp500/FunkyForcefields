package link.infra.funkyforcefields.blocks.transport;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import link.infra.funkyforcefields.transport.FluidContainerComponent;
import link.infra.funkyforcefields.transport.FluidContainerComponentImpl;
import link.infra.funkyforcefields.transport.TransportUtilities;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LiquidInputHatchBlockEntity extends BlockEntity implements BlockComponentProvider, Tickable {
	public LiquidInputHatchBlockEntity() {
		super(FunkyForcefields.LIQUID_INPUT_HATCH_BLOCK_ENTITY);
	}

	public ForcefieldFluid getNewCurrentFluid() {
		assert world != null;
		// TODO: other fluids?
		BlockState bs = world.getBlockState(pos.offset(Direction.UP));
		if (bs.getFluidState().getFluid().matchesType(Fluids.WATER)) {
			return ForcefieldFluids.WATER;
		}
		if (bs.getFluidState().getFluid().matchesType(Fluids.LAVA)) {
			return ForcefieldFluids.LAVA;
		}
		if (bs.getBlock() == Blocks.GLASS) {
			return ForcefieldFluids.GLASS;
		}
		// TODO: make event driven?
		List<ItemEntity> ents = world.getEntities(EntityType.ITEM, new Box(pos), item ->
			item.getStack() != null && (item.getStack().getItem().equals(Items.WATER_BUCKET) || item.getStack().getItem().equals(Items.LAVA_BUCKET)));
		if (ents.size() > 0) {
			if (ents.get(0).getStack().getItem().equals(Items.WATER_BUCKET)) {
				return ForcefieldFluids.WATER;
			} else {
				return ForcefieldFluids.LAVA;
			}
		}
		return null;
	}

	public ForcefieldFluid removeCurrentFluid() {
		assert world != null;
		BlockState bs = world.getBlockState(pos.offset(Direction.UP));
		if (bs.getBlock() instanceof FluidDrainable) {
			Fluid fluid = ((FluidDrainable) bs.getBlock()).tryDrainFluid(world, pos.offset(Direction.UP), bs);
			if (fluid.matchesType(Fluids.WATER)) {
				return ForcefieldFluids.WATER;
			}
			if (fluid.matchesType(Fluids.LAVA)) {
				return ForcefieldFluids.LAVA;
			}
		} else if (bs.getBlock() == Blocks.GLASS) {
			if (world.removeBlock(pos.offset(Direction.UP), false)) {
				return ForcefieldFluids.GLASS;
			} else {
				return null;
			}
		}
		List<ItemEntity> ents = world.getEntities(EntityType.ITEM, new Box(pos), item ->
			item.getStack() != null && (item.getStack().getItem().equals(Items.WATER_BUCKET) || item.getStack().getItem().equals(Items.LAVA_BUCKET)));
		if (ents.size() > 0) {
			ItemEntity ent = ents.get(0);
			if (ent.isAlive()) {
				ent.kill();
				ItemEntity emptyBucket = new ItemEntity(world, ent.getX(), ent.getY(), ent.getZ(), new ItemStack(Items.BUCKET));
				emptyBucket.setVelocity(new Vec3d(ent.getVelocity().getX(), 1, ent.getVelocity().getZ()));
				world.spawnEntity(emptyBucket);
				if (ent.getStack().getItem().equals(Items.WATER_BUCKET)) {
					return ForcefieldFluids.WATER;
				}
				return ForcefieldFluids.LAVA;
			}
		}
		return null;
	}

	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			BlockPos neighborPos = pos.offset(Direction.DOWN, 1);
			BlockEntity be = world.getBlockEntity(neighborPos);
			if (be instanceof BlockComponentProvider && ((BlockComponentProvider) be).hasComponent(world, neighborPos, FluidContainerComponent.TYPE, Direction.UP)) {
				fluidContainerComponent.tick(((BlockComponentProvider) be).getComponent(world, neighborPos, FluidContainerComponent.TYPE, Direction.UP));
			} else {
				fluidContainerComponent.tick();
			}
			if (fluidTicksRemaining > -1 && currentFluid != null) {
				fluidTicksRemaining--;
				if (fluidContainerComponent.getContainedFluid() == null) {
					fluidContainerComponent.setContainedFluid(currentFluid);
				}
				if (currentFluid != null && currentFluid.equals(fluidContainerComponent.getContainedFluid())) {
					fluidContainerComponent.setPressure(fluidContainerComponent.getPressure() + 1000);
					fluidContainerComponent.setTemperature(TransportUtilities.NOMINAL_TEMPERATURE);
				}
			} else {
				currentFluid = null;
			}
			ForcefieldFluid newCurrentFluid = getNewCurrentFluid();
			if (currentFluid == null || currentFluid.equals(newCurrentFluid)) {
				ForcefieldFluid testFluid = removeCurrentFluid();
				if (testFluid != null && (currentFluid == null || testFluid.equals(currentFluid))) {
					currentFluid = testFluid;
					fluidTicksRemaining += 5;
				}
			}
		}
	}

	private ForcefieldFluid currentFluid = null;
	private int fluidTicksRemaining = -1;
	private final FluidContainerComponentImpl fluidContainerComponent = new FluidContainerComponentImpl(10, 0.2F);

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.getInt("bufferedFluid") != -1) {
			currentFluid = ForcefieldFluid.REGISTRY.get(tag.getInt("bufferedFluid"));
		}
		fluidTicksRemaining = tag.getInt("fluidTicksRemaining");
		fluidContainerComponent.fromTag(tag);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		if (currentFluid != null) {
			tag.putInt("bufferedFluid", ForcefieldFluid.REGISTRY.getRawId(currentFluid));
		} else {
			tag.putInt("bufferedFluid", -1);
		}
		tag.putInt("fluidTicksRemaining", fluidTicksRemaining);
		return fluidContainerComponent.toTag(tag);
	}

	@Override
	public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos blockPos, ComponentType<T> componentType, Direction direction) {
		if (componentType != FluidContainerComponent.TYPE) {
			return false;
		}
		return direction == null || direction == Direction.DOWN;
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
