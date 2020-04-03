package link.infra.funkyforcefields.regions;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ForcefieldRegion {
	protected final ForcefieldFluid forcefieldFluid;
	public ForcefieldRegion(ForcefieldFluid fluid) {
		this.forcefieldFluid = fluid;
	}

	public ForcefieldFluid getForcefieldFluid() {
		return forcefieldFluid;
	}

	public abstract boolean containsCoordinate(BlockPos pos);
	public abstract void placeBlocks(World world);
	public abstract boolean isValidBlock(@Nullable BlockState state);
	public abstract void revalidateBlock(World world, BlockPos pos);
	public abstract void cleanup(World world, ForcefieldRegionManager manager);
}
