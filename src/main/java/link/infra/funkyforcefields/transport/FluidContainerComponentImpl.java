package link.infra.funkyforcefields.transport;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class FluidContainerComponentImpl implements FluidContainerComponent {
	final float containerVolume;
	float pressure = TransportUtilities.NOMINAL_PRESSURE;
	final float thermalDiffusivity;
	float temperature = TransportUtilities.NOMINAL_TEMPERATURE;
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
	public void fromTag(NbtCompound compoundTag) {
		pressure = compoundTag.getFloat("pressure");
		temperature = compoundTag.getFloat("temperature");
		if (compoundTag.getInt("containedFluid") != -1) {
			containedFluid = ForcefieldFluid.REGISTRY.get(compoundTag.getInt("containedFluid"));
		}
	}

	@Override
	public NbtCompound toTag(NbtCompound compoundTag) {
		compoundTag.putFloat("pressure", pressure);
		compoundTag.putFloat("temperature", temperature);
		if (containedFluid != null) {
			compoundTag.putInt("containedFluid", ForcefieldFluid.REGISTRY.getRawId(containedFluid));
		} else {
			compoundTag.putInt("containedFluid", -1);
		}
		return compoundTag;
	}

	public void tick(FluidContainerComponent... neighbors) {
		if (containedFluid == null) {
			FluidContainerComponent biggestNeighbor = null;
			for (FluidContainerComponent neighbor : neighbors) {
				if (neighbor.getContainedFluid() != null) {
					if (biggestNeighbor == null) {
						biggestNeighbor = neighbor;
					} else if (neighbor.getPressure() > biggestNeighbor.getPressure()) {
						biggestNeighbor = neighbor;
					}
				}
			}
			if (biggestNeighbor != null && biggestNeighbor.getPressure() > pressure) {
				containedFluid = biggestNeighbor.getContainedFluid();
			}
		}
		float[] neighborValues = new float[neighbors.length];
		for (int i = 0; i < neighbors.length; i++) {
			neighborValues[i] = neighbors[i].getPressure();
		}
		pressure = TransportUtilities.tickPressure(containerVolume, pressure, neighborValues);
		for (int i = 0; i < neighbors.length; i++) {
			neighborValues[i] = neighbors[i].getTemperature();
		}
		temperature = TransportUtilities.tickTemperature(thermalDiffusivity, temperature, neighborValues);
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
