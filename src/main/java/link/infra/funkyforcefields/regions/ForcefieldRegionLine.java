package link.infra.funkyforcefields.regions;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.VerticalForcefield;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ForcefieldRegionLine extends ForcefieldRegion {
	private final BlockPos origPos;
	private final int length;
	private final Direction dir;

	public ForcefieldRegionLine(BlockPos origPos, int length, Direction dir) {
		this.origPos = origPos;
		this.length = length;
		this.dir = dir;
	}

	private static boolean bounded(int value, int orig, int length, boolean flipped) {
		int blockIndexAlongLine = value - orig;
		if (flipped) {
			blockIndexAlongLine = -blockIndexAlongLine;
		}
		return blockIndexAlongLine >= 1 && blockIndexAlongLine < (length + 1);
	}

	@Override
	public boolean containsCoordinate(BlockPos pos) {
		boolean flipped = dir.getDirection() == Direction.AxisDirection.NEGATIVE;
		switch (dir.getAxis()) {
			case X:
				return pos.getY() == origPos.getY() && pos.getZ() == origPos.getZ() && bounded(pos.getX(), origPos.getX(), length, flipped);
			case Y:
				return pos.getX() == origPos.getX() && pos.getZ() == origPos.getZ() && bounded(pos.getY(), origPos.getY(), length, flipped);
			case Z:
				return pos.getY() == origPos.getY() && pos.getX() == origPos.getX() && bounded(pos.getZ(), origPos.getZ(), length, flipped);
		}
		return false;
	}

	@Override
	public void createBlocks(World world) {
		// TODO: don't just nuke all the things!!
		for (int i = 1; i < length + 1; i++) {
			world.setBlockState(origPos.offset(dir, i), FunkyForcefields.VERTICAL_FORCEFIELD.getDefaultState().with(VerticalForcefield.FACING, dir));
		}
	}
}
