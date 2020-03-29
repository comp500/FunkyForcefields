package link.infra.funkyforcefields;

import link.infra.funkyforcefields.regions.ForcefieldType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FunkyForcefields implements ModInitializer, ClientModInitializer {
	public static final String MODID = "funkyforcefields";

	public static final Block VERTICAL_FORCEFIELD = new VerticalForcefield(ForcefieldType.FUNKY_GOO);
	public static final Block PLASMA_EJECTOR = new PlasmaEjector();

	public static BlockEntityType<PlasmaEjectorBlockEntity> PLASMA_EJECTOR_BLOCK_ENTITY;

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MODID, "main"),
		() -> new ItemStack(VERTICAL_FORCEFIELD)
	);

	@Override
	public void onInitialize() {
		PLASMA_EJECTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MODID + ":plasma_ejector",
			BlockEntityType.Builder.create(PlasmaEjectorBlockEntity::new, PLASMA_EJECTOR).build(null));

		// TODO: change identifier
		Registry.register(Registry.BLOCK, new Identifier(MODID, "forcefield"), VERTICAL_FORCEFIELD);
		Registry.register(Registry.ITEM, new Identifier(MODID, "forcefield"),
			new BlockItem(VERTICAL_FORCEFIELD, new Item.Settings()));

		Registry.register(Registry.BLOCK, new Identifier(MODID, "plasma_ejector"), PLASMA_EJECTOR);
		Registry.register(Registry.ITEM, new Identifier(MODID, "plasma_ejector"),
			new BlockItem(PLASMA_EJECTOR, new Item.Settings().group(ITEM_GROUP)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(VERTICAL_FORCEFIELD, RenderLayer.getTranslucent());
	}
}
