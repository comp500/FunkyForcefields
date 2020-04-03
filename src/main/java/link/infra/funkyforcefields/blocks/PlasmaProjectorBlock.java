package link.infra.funkyforcefields.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class PlasmaProjectorBlock extends Block implements BlockEntityProvider {
	public PlasmaProjectorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new PlasmaProjectorBlockEntity();
	}
}
