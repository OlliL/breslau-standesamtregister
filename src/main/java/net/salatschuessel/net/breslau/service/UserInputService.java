package net.salatschuessel.net.breslau.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import net.salatschuessel.net.breslau.model.Register;
import net.salatschuessel.net.breslau.model.RegisterFile;

public class UserInputService {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

	private static String readLine(final String format, final Object... args) throws IOException {
		if (System.console() != null) {
			return System.console().readLine(format, args);
		}
		System.out.print(String.format(format, args));
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		return reader.readLine();
	}

	public RegisterFile requestRegisterFile() throws IOException {
		System.out.println("Select file to work on:");
		System.out.println(" Breslau I:    1) Geburtsregister    2) Heiratsregister    3) Sterberegister ");

		RegisterFile registerFile = null;
		while (registerFile == null) {
			final String number = readLine("Enter number: ");
			registerFile = RegisterFile.getById(number);
		}
		return registerFile;
	}

	public void requestUserInput(final List<Register> registerList)
			throws IOException {
		while (true) {

			//
			// year, main/secondary register, volume - all in one
			//
			String yearToBeParsed = "";
			while (yearToBeParsed.length() < 6) {
				yearToBeParsed = readLine(
						"Enter year followed by a + for a main register or - for a secondary register and the volume (e.g. 1875+10), empty for exit: ");
				if (yearToBeParsed.isBlank()) {
					break;
				}
			}
			if (yearToBeParsed.isBlank()) {
				break;
			}
			final Register register = new Register();
			final Integer year = Integer.valueOf(yearToBeParsed.substring(0, 4));
			final boolean isMainRegister = "+".equals(yearToBeParsed.substring(4, 5));
			final Integer volume = Integer.valueOf(yearToBeParsed.substring(5));
			register.setYear(year);
			register.setVolume(volume);
			register.setMainRegister(isMainRegister);

			//
			// URL
			//
			boolean inputDone = false;
			while (!inputDone) {
				final String url = readLine("URL (empty if missing): ");
				final boolean isMissing = url.isBlank();
				register.setMissing(isMissing);
				if (!isMissing) {
					try {
						register.setUrl(new URI(url).toURL());
						inputDone = true;
					} catch (final Exception e) {
						continue;
					}
					String online = "2";
					while (!"1".equals(online) && !online.isBlank())
						online = readLine("Is it online? (empty if not, 1 if yes)");
					register.setOnline(!online.isBlank());
				} else {
					register.setOnline(false);
					inputDone = true;
				}
			}

			//
			// 1st entry
			//
			String numberFrom = "";
			inputDone = false;
			while (!inputDone) {
				numberFrom = readLine("register entry number from (empty if unknown): ");
				try {
					if (!numberFrom.isBlank()) {
						register.setNumberFrom(Integer.valueOf(numberFrom));
					}
					inputDone = true;
				} catch (final Exception e) {
				}
			}

			if (!numberFrom.isBlank()) {
				inputDone = false;
				while (!inputDone) {
					final String dateFrom = readLine("date from (DDMM) (empty if unknown): ");
					try {
						if (!dateFrom.isBlank()) {
							register.setDateFrom(LocalDate.parse(dateFrom + year, formatter));
						}
						inputDone = true;
					} catch (final Exception e) {
					}
				}

				//
				// last entry
				//
				String numberTo = "";
				inputDone = false;
				while (!inputDone) {
					numberTo = readLine("register entry number to: ");
					try {
						if (!numberTo.isBlank()) {
							register.setNumberTo(Integer.valueOf(numberTo));
						}
						inputDone = true;
					} catch (final Exception e) {
					}
				}

				if (!numberTo.isBlank()) {
					register.setNumberTo(Integer.valueOf(numberTo));
					inputDone = false;
					while (!inputDone) {
						final String dateTo = readLine("date to (DDMM) (empty if unknown): ");
						try {
							if (!dateTo.isBlank()) {
								register.setDateTo(LocalDate.parse(dateTo + year, formatter));
							}
							inputDone = true;
						} catch (final Exception e) {
						}
					}
				}
			}

			registerList.add(register);
		}
	}
}
