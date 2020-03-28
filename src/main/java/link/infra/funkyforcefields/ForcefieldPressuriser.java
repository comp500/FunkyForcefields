package link.infra.funkyforcefields;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class ForcefieldPressuriser extends Block {
	public ForcefieldPressuriser() {
		super(FabricBlockSettings.of(Material.BARRIER).build());
	}
}
