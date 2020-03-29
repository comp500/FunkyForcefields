package link.infra.funkyforcefields.regions;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ForcefieldRegion {
	public abstract boolean containsCoordinate(BlockPos pos);
	// TODO: what should this do
	public abstract void createBlocks(World world);
}
