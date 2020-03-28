package link.infra.funkyforcefields;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FunkyForcefields implements ModInitializer, ClientModInitializer {
	public static final String MODID = "funkyforcefields";

	public static final Block FORCEFIELD = new Forcefield();
	public static final Block FORCEFIELD_PRESSURISER = new ForcefieldPressuriser();

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MODID, "main"),
		() -> new ItemStack(FORCEFIELD)
	);

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "forcefield"), FORCEFIELD);
		Registry.register(Registry.ITEM, new Identifier(MODID, "forcefield"),
			new BlockItem(FORCEFIELD, new Item.Settings()));

		Registry.register(Registry.BLOCK, new Identifier(MODID, "forcefield_pressuriser"), FORCEFIELD_PRESSURISER);
		Registry.register(Registry.ITEM, new Identifier(MODID, "forcefield_pressuriser"),
			new BlockItem(FORCEFIELD_PRESSURISER, new Item.Settings().group(ITEM_GROUP)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(FORCEFIELD, RenderLayer.getTranslucent());
	}
}
