package link.infra.funkyforcefields.blocks;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class PlasmaEjectorScreen extends CottonInventoryScreen<PlasmaEjectorController> {
	public PlasmaEjectorScreen(PlasmaEjectorController container, PlayerEntity player) {
		super(container, player);
	}
}
