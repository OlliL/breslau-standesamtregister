package net.salatschuessel.breslau.model;

import java.io.File;

public enum RegisterFile {
	BRESLAU_I_BIRTH("1", "breslau_1_birth", "Geburtsregister Breslau I"),
	BRESLAU_I_MARRIAGE("2", "breslau_1_marriage", "Heiratsregister Breslau I"),
	BRESLAU_I_DEATH("3", "breslau_1_death", "Sterberegister Breslau I"),
	BRESLAU_II_BIRTH("4", "breslau_2_birth", "Geburtsregister Breslau II"),
	BRESLAU_II_MARRIAGE("5", "breslau_2_marriage", "Heiratsregister Breslau II"),
	BRESLAU_II_DEATH("6", "breslau_2_death", "Sterberegister Breslau II"),
	;

	private final String id;
	private final String fileprefix;
	private final String label;

	private RegisterFile(final String id, final String fileprefix, final String label) {
		this.id = id;
		this.fileprefix = fileprefix;
		this.label = label;
	}

	public static RegisterFile getById(final String id) {
		for (final var registerFile : RegisterFile.values()) {
			if (registerFile.id.equals(id)) {
				return registerFile;
			}
		}
		return null;
	}

	public File getJsonFile() {
		return new File("data", this.fileprefix + ".json");
	}

	public File getWikiFile() {
		return new File("data", this.fileprefix + ".txt");
	}

	public String getLabel() {
		return this.label;
	}
}
