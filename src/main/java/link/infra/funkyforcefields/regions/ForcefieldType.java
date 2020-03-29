package link.infra.funkyforcefields.regions;

public enum ForcefieldType {
	FUNKY_GOO(0),
	WATER(1),
	LAVA(2);

	public final int id;
	ForcefieldType(int id) {
		this.id = id;
	}
}
