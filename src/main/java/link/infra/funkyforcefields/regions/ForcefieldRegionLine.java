package link.infra.funkyforcefields.regions;

import jdk.internal.jline.internal.Nullable;
import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.VerticalForcefield;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ForcefieldRegionLine extends ForcefieldRegion {
	private final BlockPos origPos;
	private final int length;
	private final Direction dirExtension;
	private final Direction dirForcefield;

	// TODO: add forcefield type
	public ForcefieldRegionLine(BlockPos origPos, int length, Direction dirExtension, Direction dirForcefield) {
		this.origPos = origPos;
		this.length = length;
		this.dirExtension = dirExtension;
		this.dirForcefield = dirForcefield;
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
		boolean flipped = dirExtension.getDirection() == Direction.AxisDirection.NEGATIVE;
		switch (dirExtension.getAxis()) {
			case X:
				return pos.getY() == origPos.getY() && pos.getZ() == origPos.getZ() && bounded(pos.getX(), origPos.getX(), length, flipped);
			case Y:
				return pos.getX() == origPos.getX() && pos.getZ() == origPos.getZ() && bounded(pos.getY(), origPos.getY(), length, flipped);
			case Z:
				return pos.getY() == origPos.getY() && pos.getX() == origPos.getX() && bounded(pos.getZ(), origPos.getZ(), length, flipped);
		}
		return false;
	}

	private boolean placingBlocks = false;

	@Override
	public void placeBlocks(World world) {
		if (placingBlocks) return;
		placingBlocks = true;
		for (int i = 1; i < length + 1; i++) {
			BlockPos newPos = origPos.offset(dirExtension, i);
			if (!world.getBlockState(newPos).isAir()) {
				continue;
			}
			world.setBlockState(newPos, FunkyForcefields.VERTICAL_FORCEFIELD.getDefaultState().with(VerticalForcefield.FACING, dirForcefield));
		}
		placingBlocks = false;
	}

	@Override
	public boolean isValidBlock(@Nullable BlockState state) {
		return state != null && state.getBlock() == FunkyForcefields.VERTICAL_FORCEFIELD && state.get(VerticalForcefield.FACING) == dirForcefield;
	}

	@Override
	public void revalidateBlock(World world, BlockPos pos) {
		world.setBlockState(pos, FunkyForcefields.VERTICAL_FORCEFIELD.getDefaultState().with(VerticalForcefield.FACING, dirForcefield));
	}

	@Override
	public void cleanup(World world, ForcefieldRegionManager manager) {
		for (int i = 1; i < length + 1; i++) {
			BlockPos newPos = origPos.offset(dirExtension, i);
			BlockState state = world.getBlockState(newPos);
			// TODO: is forcefield function? or interface?
			if (state.getBlock() == FunkyForcefields.VERTICAL_FORCEFIELD) {
				ForcefieldRegion region = manager.queryRegion(newPos);
				if (region == null) {
					world.removeBlock(newPos, false);
				} else if (!region.isValidBlock(state)) {
					region.revalidateBlock(world, newPos);
				}
			}
		}
	}
}
