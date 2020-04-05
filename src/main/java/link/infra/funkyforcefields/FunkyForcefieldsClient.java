package link.infra.funkyforcefields;

import link.infra.funkyforcefields.blocks.ForcefieldBlocks;
import link.infra.funkyforcefields.blocks.PlasmaEjectorController;
import link.infra.funkyforcefields.blocks.PlasmaEjectorScreen;
import link.infra.funkyforcefields.blocks.PlasmaProjectorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

public class FunkyForcefieldsClient implements ClientModInitializer {
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ForcefieldBlocks.initClient();
		BlockEntityRendererRegistry.INSTANCE.register(FunkyForcefields.PLASMA_PROJECTOR_BLOCK_ENTITY, PlasmaProjectorBlockEntityRenderer::new);

		ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(FunkyForcefields.MODID, "plasma_ejector"), (syncId, identifier, player, buf) -> new PlasmaEjectorScreen(
			new PlasmaEjectorController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
	}
}
