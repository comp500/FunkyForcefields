package link.infra.funkyforcefields;

import link.infra.funkyforcefields.blocks.*;
import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FunkyForcefields implements ModInitializer, ClientModInitializer {
	public static final String MODID = "funkyforcefields";

	private static Block FUNKY_GOO_ITEMGROUP_ICON;
	public static final Block PLASMA_EJECTOR_VERTICAL = new PlasmaEjectorVertical();
	public static final Block PLASMA_EJECTOR_HORIZONTAL = new PlasmaEjectorHorizontal();

	public static BlockEntityType<PlasmaEjectorBlockEntity> PLASMA_EJECTOR_BLOCK_ENTITY;

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MODID, "main"),
		() -> new ItemStack(FUNKY_GOO_ITEMGROUP_ICON)
	);

	@Override
	public void onInitialize() {
		Registry.register(Registry.REGISTRIES, new Identifier(MODID, "forcefield_type"), ForcefieldFluid.REGISTRY);
		ForcefieldFluids.register();
		ForcefieldBlocks.registerStandardBlockTypes();

		// TODO: funky goo?!!
		FUNKY_GOO_ITEMGROUP_ICON = ForcefieldBlocks.getBlock(ForcefieldFluids.LAVA, ForcefieldBlockVertical.class);

		PLASMA_EJECTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "plasma_ejector"),
			BlockEntityType.Builder.create(PlasmaEjectorBlockEntity::new, PLASMA_EJECTOR_VERTICAL, PLASMA_EJECTOR_HORIZONTAL).build(null));

		Registry.register(Registry.BLOCK, new Identifier(MODID, "plasma_ejector_vertical"), PLASMA_EJECTOR_VERTICAL);
		Registry.register(Registry.ITEM, new Identifier(MODID, "plasma_ejector_vertical"),
			new BlockItem(PLASMA_EJECTOR_VERTICAL, new Item.Settings().group(ITEM_GROUP)));
		Registry.register(Registry.BLOCK, new Identifier(MODID, "plasma_ejector_horizontal"), PLASMA_EJECTOR_HORIZONTAL);
		Registry.register(Registry.ITEM, new Identifier(MODID, "plasma_ejector_horizontal"),
			new BlockItem(PLASMA_EJECTOR_HORIZONTAL, new Item.Settings().group(ITEM_GROUP)));

		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			BlockState state = world.getBlockState(blockPos);
			if (state.getBlock() instanceof ForcefieldBlock) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
		ForcefieldBlocks.initClient();
	}
}
