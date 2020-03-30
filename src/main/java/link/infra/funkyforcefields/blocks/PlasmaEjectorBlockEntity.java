package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import link.infra.funkyforcefields.regions.ForcefieldRegionHolder;
import link.infra.funkyforcefields.regions.ForcefieldRegionLine;
import link.infra.funkyforcefields.regions.ForcefieldRegionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public class PlasmaEjectorBlockEntity extends BlockEntity implements ForcefieldRegionHolder, Tickable {
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
							region = new ForcefieldRegionLine(pos, 10, Direction.UP, state.get(PlasmaEjectorVertical.FACING), ForcefieldFluids.WATER);
							break;
						case DOWN:
							region = new ForcefieldRegionLine(pos, 10, Direction.DOWN, state.get(PlasmaEjectorVertical.FACING), ForcefieldFluids.WATER);
							break;
						case SIDEWAYS:
							region = new ForcefieldRegionLine(pos, 10, state.get(PlasmaEjectorVertical.FACING), state.get(PlasmaEjectorVertical.FACING), ForcefieldFluids.WATER);
					}
				} else {
					region = new ForcefieldRegionLine(pos, 10, state.get(PlasmaEjectorHorizontal.FACING), Direction.UP, ForcefieldFluids.WATER);
				}
				registerRegion(region, world);
			}

			if (queueBlockPlace) {
				region.placeBlocks(world);
				queueBlockPlace = false;
			}
		}
	}
}
