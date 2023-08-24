package link.infra.funkyforcefields.mixin;

import link.infra.funkyforcefields.util.EntityContextBypasser;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityShapeContext.class)
public abstract class EntityShapeContextMixin implements EntityContextBypasser {
	private Entity underlyingEntity = null;

	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/entity/Entity;)V")
	public void onConstruction(Entity ent, CallbackInfo ci) {
		underlyingEntity = ent;
	}

	@Override
	public Entity getUnderlyingEntity() {
		return underlyingEntity;
	}
}
