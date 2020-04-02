package link.infra.funkyforcefields.transport;

import net.minecraft.util.StringIdentifiable;

public enum PipeConnection implements StringIdentifiable {
	DISCONNECTED("disconnected"),
	CONNECTED("connected"),
	BLOCK_CONNECTED("block_connected");

	private final String name;
	PipeConnection(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return name;
	}
}
