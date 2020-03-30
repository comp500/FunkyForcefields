package link.infra.funkyforcefields.regions;

import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ForcefieldRegion {
	public abstract boolean containsCoordinate(BlockPos pos);
	public abstract void placeBlocks(World world);
	public abstract boolean isValidBlock(@Nullable BlockState state);
	public abstract void revalidateBlock(World world, BlockPos pos);
	public abstract void cleanup(World world, ForcefieldRegionManager manager);
}
