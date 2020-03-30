package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public abstract class ForcefieldBlock extends Block {
	public ForcefieldBlock(Settings settings) {
		super(settings);
	}

	abstract ForcefieldFluid getFluid();

	@Environment(EnvType.CLIENT)
	void initRenderLayer() {
		if (getFluid().isTranslucent()) {
			BlockRenderLayerMap.INSTANCE.putBlock(this, RenderLayer.getTranslucent());
		}
	}
}
