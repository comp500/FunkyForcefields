package link.infra.funkyforcefields;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class Forcefield extends Block {
	public Forcefield() {
		super(FabricBlockSettings.of(Material.BARRIER).nonOpaque().strength(-1.0F, 3600000.0F).dropsNothing().build());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return VoxelShapes.cuboid(0f, 0f, 0.495f, 1f, 1f, 0.505f);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		Entity ent = context instanceof EntityContextBypasser ? ((EntityContextBypasser) context).getUnderlyingEntity() : null;
		if (ent != null) {
			if (ent instanceof ItemEntity) {
				return VoxelShapes.empty();
			}
		}
		return super.getCollisionShape(state, view, pos, context);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
