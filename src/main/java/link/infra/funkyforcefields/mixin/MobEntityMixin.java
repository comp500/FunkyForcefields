package link.infra.funkyforcefields.mixin;

import link.infra.funkyforcefields.FunkyForcefields;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
	protected MobEntityMixin(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;", cancellable = true)
	private static void wearInputHatch(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
		if (stack.getItem().equals(FunkyForcefields.LIQUID_INPUT_HATCH.asItem())) {
			cir.setReturnValue(EquipmentSlot.HEAD);
		}
	}
}
