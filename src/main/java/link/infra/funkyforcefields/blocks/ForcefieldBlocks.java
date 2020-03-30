package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ForcefieldBlocks {
	private static final List<ForcefieldFluid> registeredFluids = new ArrayList<>();
	private static final Map<Class<? extends ForcefieldBlock>, Function<ForcefieldFluid, ? extends ForcefieldBlock>> blockGenerators = new HashMap<>();
	private static final Map<ForcefieldFluid, Map<Class<? extends ForcefieldBlock>, ForcefieldBlock>> registeredBlocks = new HashMap<>();

	public static <T extends ForcefieldBlock> void registerBlockType(Function<ForcefieldFluid, T> gen, Class<T> type) {
		blockGenerators.put(type, gen);
		for (ForcefieldFluid fluid : registeredFluids) {
			T block = gen.apply(fluid);
			registeredBlocks.getOrDefault(fluid, new HashMap<>()).put(type, block);
		}
	}

	public static void registerFluid(ForcefieldFluid fluid) {
		registeredFluids.add(fluid);
		Map<Class<? extends ForcefieldBlock>, ForcefieldBlock> typeToBlockMap = registeredBlocks.put(fluid, new HashMap<>());
		assert typeToBlockMap != null;
		for (Map.Entry<Class<? extends ForcefieldBlock>, Function<ForcefieldFluid, ? extends ForcefieldBlock>> gen : blockGenerators.entrySet()) {
			ForcefieldBlock block = gen.getValue().apply(fluid);
			typeToBlockMap.put(gen.getKey(), block);
		}
	}

	private static Identifier appendIdentifier(Identifier prevIdentifier, String suffix) {
		return new Identifier(prevIdentifier.getNamespace(), prevIdentifier.getPath() + suffix);
	}

	public static void registerStandardBlockTypes() {
		registerBlockType(fluid -> {
			ForcefieldBlockVertical vfb = new ForcefieldBlockVertical(fluid);
			Registry.register(Registry.BLOCK, appendIdentifier(fluid.getBaseIdentifier(), "_vertical"), vfb);
			Registry.register(Registry.ITEM, appendIdentifier(fluid.getBaseIdentifier(), "_vertical"),
				new BlockItem(vfb, new Item.Settings()));
			return vfb;
		}, ForcefieldBlockVertical.class);

		registerBlockType(fluid -> {
			ForcefieldBlockHorizontal vfb = new ForcefieldBlockHorizontal(fluid);
			Registry.register(Registry.BLOCK, appendIdentifier(fluid.getBaseIdentifier(), "_horizontal"), vfb);
			Registry.register(Registry.ITEM, appendIdentifier(fluid.getBaseIdentifier(), "_horizontal"),
				new BlockItem(vfb, new Item.Settings()));
			return vfb;
		}, ForcefieldBlockHorizontal.class);
	}

	public static ForcefieldBlock getBlock(ForcefieldFluid fluid, Class<? extends ForcefieldBlock> clazz) {
		return registeredBlocks.get(fluid).get(clazz);
	}
}
