package link.infra.funkyforcefields.regions;

import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.WeakHashMap;

public class ForcefieldRegionManager {
	private static final WeakHashMap<World, ForcefieldRegionManager> regionManagerMap = new WeakHashMap<>();
	protected WeakHashMap<ForcefieldRegionHolder, ForcefieldRegion> regionMap = new WeakHashMap<>();

	@Nullable
	public static ForcefieldRegionManager get(@Nullable World world) {
		if (world == null) {
			return null;
		}
		if (world.isClient()) {
			throw new RuntimeException("Can't get a ForcefieldRegionManager for client world views");
		}
		return regionManagerMap.computeIfAbsent(world, ignored -> new ForcefieldRegionManager());
	}

	public void addRegion(ForcefieldRegionHolder holder, ForcefieldRegion region) {
		regionMap.put(holder, region);
	}

	public void removeRegion(ForcefieldRegionHolder holder) {
		regionMap.remove(holder);
	}

	public ForcefieldRegion queryRegion(BlockPos pos) {
		for (ForcefieldRegion region : regionMap.values()) {
			if (region.containsCoordinate(pos)) {
				return region;
			}
		}
		return null;
	}
}
