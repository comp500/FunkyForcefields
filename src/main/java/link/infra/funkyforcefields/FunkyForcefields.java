package link.infra.funkyforcefields;

import link.infra.funkyforcefields.blocks.*;
import link.infra.funkyforcefields.blocks.transport.LiquidInputHatchBlock;
import link.infra.funkyforcefields.blocks.transport.LiquidInputHatchBlockEntity;
import link.infra.funkyforcefields.blocks.transport.PipeBlock;
import link.infra.funkyforcefields.blocks.transport.PipeBlockEntity;
import link.infra.funkyforcefields.items.GaugeItem;
import link.infra.funkyforcefields.regions.ForcefieldFluids;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class FunkyForcefields implements ModInitializer {
	public static final String MODID = "funkyforcefields";

	private static Block FUNKY_GOO_ITEMGROUP_ICON;
	public static final Block PLASMA_EJECTOR_VERTICAL = new PlasmaEjectorVertical();
	public static final Block PLASMA_EJECTOR_HORIZONTAL = new PlasmaEjectorHorizontal();

	public static BlockEntityType<PlasmaEjectorBlockEntity> PLASMA_EJECTOR_BLOCK_ENTITY;

	public static final Block PIPE = new PipeBlock(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	public static BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY;

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier(MODID, "main"),
		() -> new ItemStack(FUNKY_GOO_ITEMGROUP_ICON)
	);

	public static final Item GAUGE = new GaugeItem(new Item.Settings().group(ITEM_GROUP));

	public static final Block LIQUID_INPUT_HATCH = new LiquidInputHatchBlock(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	public static BlockEntityType<LiquidInputHatchBlockEntity> LIQUID_INPUT_HATCH_BLOCK_ENTITY;

	public static final Block PLASMA_PROJECTOR = new PlasmaProjectorBlock(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	public static BlockEntityType<PlasmaProjectorBlockEntity> PLASMA_PROJECTOR_BLOCK_ENTITY;

	public static final Identifier PLASMA_EJECTOR_CONFIG_PACKET = new Identifier(MODID, "plasma_ejector");

	@Override
	public void onInitialize() {
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
			new PlasmaEjectorController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())));

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

		ServerPlayNetworking.registerGlobalReceiver(PLASMA_EJECTOR_CONFIG_PACKET, (server, player, handler, packetByteBuf, responseSender) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
				int lengthUpdate = packetByteBuf.readInt();
			server.execute(() -> {
				if (player.world.canSetBlock(pos)) {
					BlockEntity be = player.world.getBlockEntity(pos);
					if (be instanceof PlasmaEjectorBlockEntity) {
						((PlasmaEjectorBlockEntity) be).length = lengthUpdate;
						be.markDirty();
					}
				}
			});
		});
	}

}
