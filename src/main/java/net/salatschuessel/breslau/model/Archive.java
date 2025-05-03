package net.salatschuessel.breslau.model;

public enum Archive {
	STAATSARCHIV_BRESLAU("Staatsarchiv Breslau"),
	LANDESARCHIV_BERLIN("Landesarchiv Berlin"),
	STANDESAMT_1_BERLIN("Standesamt I Berlin"),
	STANDESAMT_BRESLAU("Standesamt Breslau"),
	ANCESTRY("Ancestry");

	private final String label;

	private Archive(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
}
