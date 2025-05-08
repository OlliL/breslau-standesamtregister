package net.salatschuessel.breslau.model;

import java.io.File;

public enum RegisterFile {
	BRESLAU_I_BIRTH("1", "breslau_1_birth", "Geburtsregister Breslau I"),
	BRESLAU_I_MARRIAGE("2", "breslau_1_marriage", "Heiratsregister Breslau I"),
	BRESLAU_I_DEATH("3", "breslau_1_death", "Sterberegister Breslau I"),
	BRESLAU_II_BIRTH("4", "breslau_2_birth", "Geburtsregister Breslau II"),
	BRESLAU_II_MARRIAGE("5", "breslau_2_marriage", "Heiratsregister Breslau II"),
	BRESLAU_II_DEATH("6", "breslau_2_death", "Sterberegister Breslau II"),
	BRESLAU_III_BIRTH("7", "breslau_3_birth", "Geburtsregister Breslau III"),
	BRESLAU_III_MARRIAGE("8", "breslau_3_marriage", "Heiratsregister Breslau III"),
	BRESLAU_III_DEATH("9", "breslau_3_death", "Sterberegister Breslau III"),
	BRESLAU_IV_BIRTH("10", "breslau_4_birth", "Geburtsregister Breslau IV"),
	BRESLAU_IV_MARRIAGE("11", "breslau_4_marriage", "Heiratsregister Breslau IV"),
	BRESLAU_IV_DEATH("12", "breslau_4_death", "Sterberegister Breslau IV"),
	BRESLAU_V_BIRTH("13", "breslau_5_birth", "Geburtsregister Breslau V"),
	BRESLAU_V_MARRIAGE("14", "breslau_5_marriage", "Heiratsregister Breslau V"),
	BRESLAU_V_DEATH("15", "breslau_5_death", "Sterberegister Breslau V"),
	BRESLAU_VI_BIRTH("16", "breslau_6_birth", "Geburtsregister Breslau VI (Hundsfeld)"),
	BRESLAU_VI_MARRIAGE("17", "breslau_6_marriage", "Heiratsregister Breslau VI (Hundsfeld)"),
	BRESLAU_VI_DEATH("18", "breslau_6_death", "Sterberegister Breslau VI (Hundsfeld)"),
	BRESLAU_VII_BIRTH("19", "breslau_7_birth", "Geburtsregister Breslau VII (Deutsch Lissa)"),
	BRESLAU_VII_MARRIAGE("20", "breslau_7_marriage", "Heiratsregister Breslau VII (Deutsch Lissa)"),
	BRESLAU_VII_DEATH("21", "breslau_7_death", "Sterberegister Breslau VII (Deutsch Lissa)"),
	BRESLAU_VIII_BIRTH("22", "breslau_8_birth", "Geburtsregister Breslau VIII (Pilsnitz)"),
	BRESLAU_VIII_MARRIAGE("23", "breslau_8_marriage", "Heiratsregister Breslau VIII (Pilsnitz)"),
	BRESLAU_VIII_DEATH("24", "breslau_8_death", "Sterberegister Breslau VIII (Pilsnitz)"),
	BRESLAU_LAND_BIRTH("25", "breslau_land_birth", "Geburtsregister Breslau Land"),
	BRESLAU_LAND_MARRIAGE("26", "breslau_land_marriage", "Heiratsregister Breslau Land"),
	BRESLAU_LAND_DEATH("27", "breslau_land_death", "Sterberegister Breslau Land"),
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
