package link.infra.funkyforcefields.regions;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ForcefieldRegionHolder {
	BlockPos getPos();

	default void registerRegion(ForcefieldRegion region, World world) {
		ForcefieldRegionManager.get(world).addRegion(this, region);
	}
}
