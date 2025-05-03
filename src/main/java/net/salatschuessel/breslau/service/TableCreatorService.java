package net.salatschuessel.breslau.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.salatschuessel.breslau.model.Archive;
import net.salatschuessel.breslau.model.Register;
import net.salatschuessel.breslau.model.RegisterFile;
import net.salatschuessel.breslau.model.RegisterGroup;

public class TableCreatorService {
	private static DateTimeFormatter formatterWiki = DateTimeFormatter.ofPattern("dd.MM.");

	public void create(final RegisterFile registerFile, final List<Register> registerList) throws IOException {
		final var yearRegisterGroupsMap = this.getRegisterMap(registerList);

		final FileWriter fileWriter = new FileWriter(registerFile.getWikiFile());
		final PrintWriter printWriter = new PrintWriter(fileWriter);

		this.writeHeader(printWriter, registerFile);
		for (final var yearRegisterGroupsMapEntry : yearRegisterGroupsMap.entrySet()) {
			final int year = yearRegisterGroupsMapEntry.getKey();

			// get max volume number
			final int numberOfVolumes = yearRegisterGroupsMapEntry.getValue().keySet().stream().filter(i -> i < 99)
					.mapToInt(i -> i).max().orElse(0);
			// get number of timeframes
			final long numberOfTimeframes = yearRegisterGroupsMapEntry.getValue().keySet().stream().filter(i -> i > 99)
					.count();

			this.writeNewYear(printWriter, year, numberOfVolumes + numberOfTimeframes);
			int previousVolume = 0;
			for (final var registerGroupMap : yearRegisterGroupsMapEntry.getValue().entrySet()) {
				if (registerGroupMap.getKey() < 99)
					while (++previousVolume < registerGroupMap.getKey()) {
						this.writeCompletelyMissingVolumeLine(printWriter, previousVolume, numberOfTimeframes);
					}
				this.writeVolumeLine(printWriter, registerGroupMap.getKey(), registerGroupMap.getValue(),
						numberOfTimeframes);
			}
			this.writeEndOfYear(printWriter);
		}
		this.writeFooter(printWriter);

		printWriter.close();

	}

	private void writeCompletelyMissingVolumeLine(final PrintWriter printWriter, final int volume,
			final long numberOfTimeframes) {
		this.writeVolumeNumber(printWriter, volume);
		this.writeMissingVolume(printWriter, volume < 99 && numberOfTimeframes == 0);
		this.writeMissingVolume(printWriter, volume < 99);
		printWriter.print("|-\n");
	}

	private void writeEndOfYear(final PrintWriter printWriter) {
		printWriter.print("""
				! colspan="8"|
				|-
				""");

	}

	private void writeVolumeLine(final PrintWriter printWriter, final Integer volume,
			final RegisterGroup registerGroup, final long numberOfTimeframes) {
		if (volume < 99)
			this.writeVolumeNumber(printWriter, volume);
		else
			printWriter.print("| \n");

		if (registerGroup.getMainRegister() == null || registerGroup.getMainRegister().isMissing()) {
			this.writeMissingVolume(printWriter, volume < 99 && numberOfTimeframes == 0);
		} else {
			this.writeVolume(printWriter, registerGroup.getMainRegister());
		}
		if (registerGroup.getSecondaryRegister() == null || registerGroup.getSecondaryRegister().isMissing()) {
			this.writeMissingVolume(printWriter, volume < 99);
		} else {
			this.writeVolume(printWriter, registerGroup.getSecondaryRegister());
		}
		printWriter.print("|-\n");
	}

	private void writeVolumeNumber(final PrintWriter printWriter, final Integer volume) {
		printWriter.print("| %d\n".formatted(volume));
	}

	private void writeVolume(final PrintWriter printWriter, final Register register) {
		String note = register.getNote();
		if (note == null && !register.isOnline() && register.getArchiv() == Archive.STAATSARCHIV_BRESLAU)
			note = "<i>noch nicht online</i>";

		printWriter
				.print("| %s-%s\n".formatted(
						register.getNumberFrom() == null ? "?" : register.getNumberFrom(),
						register.getNumberTo() == null ? "?" : register.getNumberTo()));
		printWriter.print("| %s-%s\n".formatted(
				register.getDateFrom() == null ? "?" : register.getDateFrom().format(formatterWiki),
				register.getDateTo() == null ? "?" : register.getDateTo().format(formatterWiki)));

		if (register.getArchiv() == Archive.ANCESTRY || register.getArchiv() == Archive.STAATSARCHIV_BRESLAU)
			if (register.getUrl() != null)
				printWriter.print("| [%s %s]%s\n".formatted(register.getUrl().toString(), this.getLabel(register),
						note == null ? "" : "<br>" + note));
			else
				printWriter.print("| \n");
		else
			printWriter.print("| %s\n".formatted(this.getLabel(register)));

	}

	private String getLabel(final Register register) {
		return register.getArchiv().getLabel();
	}

	private void writeMissingVolume(final PrintWriter printWriter, final boolean isMissing) {
		if (isMissing)
			printWriter.print("""
					| colspan="3" style="background-color:#DCDCDC;"|fehlt
					""");
		else
			printWriter.print("""
					| colspan="3" |
					""");

	}

	private void writeNewYear(final PrintWriter printWriter, final int year, final long numberOfVolumes) {
		printWriter.print("! rowspan=\"%d\"|%d\n".formatted(numberOfVolumes, year));
	}

	private void writeHeader(final PrintWriter printWriter, final RegisterFile registerFile) {
		printWriter.print("""
				{| class="wikitable" style="text-align:center;"
				|+%s
				|-
				! scope="col" rowspan="2"  style="padding-left:20px;padding-right:20px"| Jahr
				! scope="col" rowspan="2"| Bd.
				! scope="col" colspan="3"| Hauptregister
				! scope="col" colspan="3"| Nebenregister
				|-
				! scope="col"| Reg.-Nr.
				! scope="col"| Datum
				! scope="col"| URL
				! scope="col"| Reg.-Nr.
				! scope="col"| Datum
				! scope="col"| URL
				|-
				""".formatted(registerFile.getLabel()));
	}

	private void writeFooter(final PrintWriter printWriter) {
		printWriter.print("""
				|}
				""");
	}

	private int volumeMapKey(final int i) {
		return i == 0 ? 100 : i;
	}

	private Map<Integer, Map<Integer, RegisterGroup>> getRegisterMap(final List<Register> registerList) {
		final Map<Integer, Map<Integer, RegisterGroup>> yearRegisterGroupsMap = new TreeMap<>();
		for (final var register : registerList) {
			final var registerGroupMap = yearRegisterGroupsMap.computeIfAbsent(register.getYear(),
					i -> new TreeMap<Integer, RegisterGroup>());
			int volume = this.volumeMapKey(register.getVolume());
			if (volume < 99) {
				final var registerGroup = registerGroupMap.computeIfAbsent(volume,
						i -> new RegisterGroup());
				if (register.isMainRegister()) {
					registerGroup.setMainRegister(register);
				} else {
					registerGroup.setSecondaryRegister(register);
				}
			} else {
				while (true) {
					final var registerGroup = registerGroupMap.computeIfAbsent(volume,
							i -> new RegisterGroup());
					volume++;
					if (register.isMainRegister()) {
						if (registerGroup.getMainRegister() != null)
							continue;
						registerGroup.setMainRegister(register);
					} else {
						if (registerGroup.getSecondaryRegister() != null)
							continue;
						registerGroup.setSecondaryRegister(register);
					}
					break;
				}
			}
		}
		return yearRegisterGroupsMap;
	}

}
