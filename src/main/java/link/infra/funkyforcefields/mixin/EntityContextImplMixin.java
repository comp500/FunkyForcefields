package link.infra.funkyforcefields.mixin;

import link.infra.funkyforcefields.EntityContextBypasser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContextImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityContextImpl.class)
public abstract class EntityContextImplMixin implements EntityContextBypasser {
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
