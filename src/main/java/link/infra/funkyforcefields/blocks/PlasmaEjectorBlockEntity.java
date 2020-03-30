package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.FunkyForcefields;
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
				region.cleanup(getWorld(), manager);
			}
		}
	}

	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			if (region == null) {
				BlockState state = world.getBlockState(pos);
				switch (state.get(PlasmaEjector.POINTING)) {
					case UP:
						region = new ForcefieldRegionLine(pos, 10, Direction.UP, state.get(PlasmaEjector.FACING));
						break;
					case DOWN:
						region = new ForcefieldRegionLine(pos, 10, Direction.DOWN, state.get(PlasmaEjector.FACING));
						break;
					case SIDEWAYS:
						region = new ForcefieldRegionLine(pos, 10, state.get(PlasmaEjector.FACING), state.get(PlasmaEjector.FACING));
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
