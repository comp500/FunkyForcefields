package link.infra.funkyforcefields.transport;

import jdk.internal.jline.internal.Nullable;
import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.regions.ForcefieldFluid;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.util.Identifier;

public interface FluidContainerComponent extends Component {
	ComponentType<FluidContainerComponent> TYPE = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(FunkyForcefields.MODID, "fluid_container"), FluidContainerComponent.class);

	float getContainerVolume();
	float getPressure();
	float getThermalDiffusivity();
	float getTemperature();
	@Nullable ForcefieldFluid getContainedFluid();
}
