package link.infra.funkyforcefields.transport;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class FluidContainerComponentImpl implements FluidContainerComponent {
	final float containerVolume;
	float pressure;
	final float thermalDiffusivity;
	float temperature;
	ForcefieldFluid containedFluid;

	public FluidContainerComponentImpl(float containerVolume, float thermalDiffusivity) {
		this.containerVolume = containerVolume;
		this.thermalDiffusivity = thermalDiffusivity;
	}

	@Override
	public float getContainerVolume() {
		return containerVolume;
	}

	@Override
	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

	@Override
	public float getThermalDiffusivity() {
		return thermalDiffusivity;
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public ForcefieldFluid getContainedFluid() {
		return containedFluid;
	}

	public void setContainedFluid(ForcefieldFluid containedFluid) {
		this.containedFluid = containedFluid;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		pressure = compoundTag.getFloat("pressure");
		temperature = compoundTag.getFloat("temperature");
		containedFluid = ForcefieldFluid.REGISTRY.get(compoundTag.getInt("containedFluid"));
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putFloat("pressure", pressure);
		compoundTag.putFloat("temperature", temperature);
		compoundTag.putInt("containedFluid", ForcefieldFluid.REGISTRY.getRawId(containedFluid));
		return compoundTag;
	}

	public void tick(FluidContainerComponent... neighbours) {
		float[] neighbourValues = new float[neighbours.length];
		for (int i = 0; i < neighbours.length; i++) {
			neighbourValues[i] = neighbours[i].getPressure();
		}
		pressure = TransportUtilities.tickPressure(containerVolume, pressure, neighbourValues);
		for (int i = 0; i < neighbours.length; i++) {
			neighbourValues[i] = neighbours[i].getTemperature();
		}
		temperature = TransportUtilities.tickTemperature(thermalDiffusivity, temperature, neighbourValues);
		if (Math.abs(pressure - TransportUtilities.NOMINAL_PRESSURE) <= TransportUtilities.NEGLIGIBILITY) {
			containedFluid = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FluidContainerComponentImpl that = (FluidContainerComponentImpl) o;
		return Float.compare(that.getContainerVolume(), getContainerVolume()) == 0 &&
			Float.compare(that.getPressure(), getPressure()) == 0 &&
			Float.compare(that.getThermalDiffusivity(), getThermalDiffusivity()) == 0 &&
			Float.compare(that.getTemperature(), getTemperature()) == 0 &&
			Objects.equals(getContainedFluid(), that.getContainedFluid());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getContainerVolume(), getPressure(), getThermalDiffusivity(), getTemperature(), getContainedFluid());
	}
}
