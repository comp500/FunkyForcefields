package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;

public abstract class ForcefieldBlock extends Block {
	public ForcefieldBlock(Settings settings) {
		super(settings);
	}

	abstract ForcefieldFluid getFluid();

	@Environment(EnvType.CLIENT)
	void initRenderLayer() {
		if (getFluid().getRenderLayer() != null) {
			BlockRenderLayerMap.INSTANCE.putBlock(this, getFluid().getRenderLayer());
		}
	}
}
