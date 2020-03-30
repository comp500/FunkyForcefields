package link.infra.funkyforcefields.blocks;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import net.minecraft.block.Block;

public abstract class ForcefieldBlock extends Block {
	public ForcefieldBlock(Settings settings) {
		super(settings);
	}

	abstract ForcefieldFluid getFluid();
}
