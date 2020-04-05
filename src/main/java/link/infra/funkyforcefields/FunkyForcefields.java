package link.infra.funkyforcefields;

import link.infra.funkyforcefields.blocks.*;
import link.infra.funkyforcefields.blocks.transport.LiquidInputHatchBlock;
import link.infra.funkyforcefields.blocks.transport.LiquidInputHatchBlockEntity;
import link.infra.funkyforcefields.blocks.transport.PipeBlock;
import link.infra.funkyforcefields.blocks.transport.PipeBlockEntity;
import link.infra.funkyforcefields.items.GaugeItem;
import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.BlockContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class FunkyForcefields implements ModInitializer, ClientModInitializer {
	public static final String MODID = "funkyforcefields";

	private static Block FUNKY_GOO_ITEMGROUP_ICON;
	public static final Block PLASMA_EJECTOR_VERTICAL = new PlasmaEjectorVertical();
	public static final Block PLASMA_EJECTOR_HORIZONTAL = new PlasmaEjectorHorizontal();

	public static BlockEntityType<PlasmaEjectorBlockEntity> PLASMA_EJECTOR_BLOCK_ENTITY;

	// TODO: customise block settings? and for plasma ejector?
	public static final Block PIPE = new PipeBlock(FabricBlockSettings.of(Material.METAL).build());
	public static BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY;

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MODID, "main"),
		() -> new ItemStack(FUNKY_GOO_ITEMGROUP_ICON)
	);

	public static final Item GAUGE = new GaugeItem(new Item.Settings().group(ITEM_GROUP));

	public static final Block LIQUID_INPUT_HATCH = new LiquidInputHatchBlock(FabricBlockSettings.of(Material.METAL).build());
	public static BlockEntityType<LiquidInputHatchBlockEntity> LIQUID_INPUT_HATCH_BLOCK_ENTITY;

	public static final Block PLASMA_PROJECTOR = new PlasmaProjectorBlock(FabricBlockSettings.of(Material.METAL).build());
	public static BlockEntityType<PlasmaProjectorBlockEntity> PLASMA_PROJECTOR_BLOCK_ENTITY;

	public static final Identifier PLASMA_EJECTOR_CONFIG_PACKET = new Identifier(MODID, "plasma_ejector");

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

		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "plasma_ejector"), (syncId, id, player, buf) ->
			new PlasmaEjectorController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));

		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			BlockState state = world.getBlockState(blockPos);
			if (state.getBlock() instanceof ForcefieldBlock) {
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		});

		Registry.register(Registry.BLOCK, new Identifier(MODID, "pipe"), PIPE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "pipe"),
			new BlockItem(PIPE, new Item.Settings().group(ITEM_GROUP)));

		PIPE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "pipe"),
			BlockEntityType.Builder.create(PipeBlockEntity::new, PIPE).build(null));

		Registry.register(Registry.ITEM, new Identifier(MODID, "gauge"), GAUGE);

		Registry.register(Registry.BLOCK, new Identifier(MODID, "liquid_input_hatch"), LIQUID_INPUT_HATCH);
		Registry.register(Registry.ITEM, new Identifier(MODID, "liquid_input_hatch"),
			new BlockItem(LIQUID_INPUT_HATCH, new Item.Settings().group(ITEM_GROUP)));
		LIQUID_INPUT_HATCH_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "liquid_input_hatch"),
			BlockEntityType.Builder.create(LiquidInputHatchBlockEntity::new, LIQUID_INPUT_HATCH).build(null));

		Registry.register(Registry.BLOCK, new Identifier(MODID, "plasma_projector"), PLASMA_PROJECTOR);
		Registry.register(Registry.ITEM, new Identifier(MODID, "plasma_projector"),
			new BlockItem(PLASMA_PROJECTOR, new Item.Settings().group(ITEM_GROUP)));
		PLASMA_PROJECTOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "plasma_projector"),
			BlockEntityType.Builder.create(PlasmaProjectorBlockEntity::new, PLASMA_PROJECTOR).build(null));

		ServerSidePacketRegistry.INSTANCE.register(PLASMA_EJECTOR_CONFIG_PACKET, (packetContext, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
				int lengthUpdate = packetByteBuf.readInt();
			packetContext.getTaskQueue().execute(() -> {
				if (packetContext.getPlayer().world.canSetBlock(pos)) {
					BlockEntity be = packetContext.getPlayer().world.getBlockEntity(pos);
					if (be instanceof PlasmaEjectorBlockEntity) {
						((PlasmaEjectorBlockEntity) be).length = lengthUpdate;
						be.markDirty();
					}
				}
			});
		});
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
		ForcefieldBlocks.initClient();
		BlockEntityRendererRegistry.INSTANCE.register(PLASMA_PROJECTOR_BLOCK_ENTITY, PlasmaProjectorBlockEntityRenderer::new);

		ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "plasma_ejector"), (syncId, identifier, player, buf) -> new PlasmaEjectorScreen(
			new PlasmaEjectorController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
	}
}
