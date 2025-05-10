package net.salatschuessel.breslau;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import net.salatschuessel.breslau.model.Archive;
import net.salatschuessel.breslau.model.Register;
import net.salatschuessel.breslau.model.RegisterFile;
import net.salatschuessel.breslau.service.JsonFileService;
import net.salatschuessel.breslau.service.TableCreatorService;
import net.salatschuessel.breslau.service.UserInputService;

public class CsvParser {

	private static final String BIS = " bis ";
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final String file = "/tmp/test.csv";
	private static List<Integer> missingNumbers = List.of(28372276, 32534671, 32534676, 32534682, 32534683, 32534686,
			32534691, 32534694, 32534702, 32534703, 32534708);
	private static List<Integer> missingNumbersAncestry = List.of(7698, 7699, 7700, 7777);
	// A
//	private static final String registerToRead = "A";
//	private static int staatsarchivBreslauBaseNumber = 28373219;
//	private static int staatsarchivBreslauBaseNumberAlternative1914 = 3419962;
//	private static int staatsarchivBreslauBaseNumberAlternative1916 = 32534670;
//	private static int staatsarchivBreslauBaseNumberAlternative1918 = 34268006;
//	private static int ancestryBaseNumber45215 = 26125;
//	private static int ancestryBaseNumber42895 = 7439;
	// B
//	private static final String registerToRead = "B";
//	private static int staatsarchivBreslauBaseNumber = 28373340;
//	private static int staatsarchivBreslauBaseNumberAlternative1914 = 32534699;
//	private static int staatsarchivBreslauBaseNumberAlternative1916 = 0;
//	private static int staatsarchivBreslauBaseNumberAlternative1918 = 0;
//	private static int ancestryBaseNumber45215 = 78672;
//	private static int ancestryBaseNumber42895 = 7439;
	// C
	private static final String registerToRead = "C";
	private static int staatsarchivBreslauBaseNumber = 28373411;
	private static int staatsarchivBreslauBaseNumberAlternative1914 = 41603394;
	private static int staatsarchivBreslauBaseNumberAlternative1916 = 41976692;
	private static int staatsarchivBreslauBaseNumberAlternative1918 = 42675468;
	private static int ancestryBaseNumber45215 = 0;
	private static int ancestryBaseNumber42895 = 7882;

	public static void main(final String[] args) throws IOException {
		final UserInputService userInputService = new UserInputService();
		final var registerFile = userInputService.requestRegisterFile();

		final JsonFileService jsonFileService = new JsonFileService();

		final File jsonFile = registerFile.getJsonFile();
//		final List<Register> registerList = jsonFileService.loadFromJsonFile(jsonFile);
		final List<Register> registerList = new ArrayList<>();

		final CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

		String previousYearCol = "";

		try (final CSVReader cvsReader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser)
				.build();) {

			boolean contentStarts = false;
			final List<String[]> cvsLines = cvsReader.readAll();
			for (final var cvsLine : cvsLines) {
				String yearCol = cvsLine[0].trim();
				final String archiveCol = cvsLine[1].trim();
				final String numberCol = cvsLine[2].trim();
				final String dateCol = cvsLine[3].trim();
				final String fallbackUrlNumber = cvsLine[6].trim();
				final String urlCol = cvsLine[7].trim();
				final String notesCol = cvsLine[9].trim();

				if (yearCol.equals("Band")) {
					contentStarts = true;
					continue;
				}

				if (!hasData(yearCol) && hasData(archiveCol) && hasData(dateCol))
					yearCol = previousYearCol;

				if (!contentStarts || !hasData(yearCol) || !yearCol.startsWith(registerToRead)
						|| archiveCol.equals("SZU")) {
					previousYearCol = yearCol;
					continue;
				}

				final String registerType = yearCol.substring(0, 1);

				final Register register = new Register();

				if (registerFile == RegisterFile.BRESLAU_VIII_BIRTH
						|| registerFile == RegisterFile.BRESLAU_VIII_DEATH
						|| registerFile == RegisterFile.BRESLAU_VIII_MARRIAGE) {
					register.setNote(notesCol);
				}

				final int year = Integer.parseInt(yearCol.substring(1, 5));
//				if (year < 1908) {
//					previousYearCol = yearCol;
//					continue;
//				}
				register.setYear(year);

				parseVolume(yearCol, register);
				parseRegisterNumbers(cvsLine, numberCol, register);
				parseDates(cvsLine, dateCol, year, register);

				Archive archive = null;
				if (urlCol.equals("fehlt")) {
					register.setMissing(true);
				} else {
					archive = getArchive(archiveCol, urlCol, year, registerType);
					if (archive == null)
						throwException("archive can't be determined from archiv column", cvsLine);

					switch (archive) {
					case LANDESARCHIV_BERLIN -> register.setUrl(URI.create("https://landesarchiv-berlin.de/").toURL());
					case STANDESAMT_1_BERLIN -> register.setUrl(URI.create("https://www.berlin.de/").toURL());
					case STANDESAMT_BRESLAU -> register.setUrl(URI.create("https://bip.um.wroc.pl/").toURL());
					case ANCESTRY -> register.setUrl(getAncestryUrl(year, registerType, urlCol));
					case STAATSARCHIV_BRESLAU -> {
						int number = 0;
						if (hasData(fallbackUrlNumber)) {
							number = Integer.parseInt(fallbackUrlNumber);
						} else if (isDeathRegister(registerType) && year >= 1918) {
							number = staatsarchivBreslauBaseNumberAlternative1918++;
						} else if (isDeathRegister(registerType) && year >= 1916) {
							number = staatsarchivBreslauBaseNumberAlternative1916++;
						} else if (isDeathRegister(registerType) && year >= 1914) {
							number = staatsarchivBreslauBaseNumberAlternative1914++;
//						} else if (isMarriageRegister(registerType) && year >= 1912) {
//							do {
//								number = staatsarchivBreslauBaseNumberAlternative1914++;
//							} while (missingNumbers.contains(number));
//						} else if (isBirthRegister(registerType) && year >= 1912) {
//							do {
//								number = staatsarchivBreslauBaseNumberAlternative1916++;
//							} while (missingNumbers.contains(number));
//						} else if (isBirthRegister(registerType)
//								&& (year >= 1908 || (year == 1907 && register.getVolume() > 7))) {
//							number = staatsarchivBreslauBaseNumberAlternative1914++;
						} else {
							do {
								number = staatsarchivBreslauBaseNumber++;
							} while (missingNumbers.contains(number));
						}
						register.setUrl(URI.create("https://www.szukajwarchiwach.gov.pl/de/jednostka/-/jednostka/%d"
								.formatted(number)).toURL());
					}
					}
					if (archive == Archive.ANCESTRY
							|| (archive == Archive.STAATSARCHIV_BRESLAU && !urlCol.equals("noch nicht online"))) {
						register.setOnline(true);
					}
				}

				parseMainRegister(cvsLine, yearCol, archive, register);
				registerList.add(register);
				System.out.println(register);

				previousYearCol = yearCol;
			}
		} catch (final IOException | CsvException e) {
			e.printStackTrace();
		}

		jsonFileService.writeToJsonFile(jsonFile, registerList);

		final TableCreatorService tableCreatorService = new TableCreatorService();
		tableCreatorService.create(registerFile, registerList);
	}

	private static URL getAncestryUrl(final int year, final String registerType, final String urlCol)
			throws MalformedURLException {
		if (!urlCol.isEmpty() && !urlCol.contains("backurl")) {
			return URI.create(urlCol).toURL();
		}

		final String collection = getAncestryCollection(year, registerType);
		final String counter = getAndIncrementCounter(collection);

		return URI.create(
				"https://www.ancestry.de/imageviewer/collections/60749/images/%s_srep100%%5E%s-00001"
						.formatted(collection, counter))
				.toURL();
	}

	private static String getAndIncrementCounter(final String collection) {
		int number = 0;
		do {
			number = collection.equals("45215") ? ancestryBaseNumber45215++ : ancestryBaseNumber42895++;
		} while (missingNumbersAncestry.contains(number));
		return String.format("%6s", Integer.valueOf(number).toString()).replace(' ', '0');
	}

	private static String getAncestryCollection(final int year, final String registerType) {
		if ((isBirthRegister(registerType) && year > 1899) || (isMarriageRegister(registerType) && year > 1929))
			return "45215";
		return "42895";
	}

	private static void parseVolume(final String yearCol, final Register register) {
		final int volume = yearCol.length() >= 8 ? Integer.parseInt(yearCol.substring(6, 8)) : 0;
		register.setVolume(volume);
	}

	private static void parseMainRegister(final String[] cvsLine, final String yearCol, final Archive archive,
			final Register register) {
		Boolean isMainRegister = null;
		if (yearCol.length() == 5 || yearCol.length() == 8) {
			if (archive == Archive.LANDESARCHIV_BERLIN || archive == Archive.STANDESAMT_1_BERLIN || archive == null)
				isMainRegister = false;
			else
				isMainRegister = true;
		} else if (yearCol.length() == 9 && yearCol.endsWith("H")) {
			isMainRegister = true;
		} else if (yearCol.length() == 10 && yearCol.endsWith("NB")) {
			isMainRegister = false;
		}

		if (isMainRegister == null)
			throwException("main/secondary register not determinable", cvsLine);

		register.setMainRegister(isMainRegister);
	}

	private static void parseRegisterNumbers(final String[] cvsLine, final String numberCol, final Register register) {
		if (hasData(numberCol)) {
			try {
				final int bisIndex = numberCol.indexOf(BIS);
				if (bisIndex < 0)
					throwException("string ' bis ' not found in register numbers column", cvsLine);
				final String numberFromStr = numberCol.substring(0, bisIndex);
				final String numberToStr = numberCol.substring(bisIndex + BIS.length(), numberCol.length());

				final Integer numberFrom = numberFromStr.contains("?") ? null : Integer.parseInt(numberFromStr);
				final Integer numberTo = numberToStr.contains("?") ? null : Integer.parseInt(numberToStr);

				register.setNumberFrom(numberFrom);
				register.setNumberTo(numberTo);
			} catch (final Exception e) {
				throwException("register numbers can't be parsed", cvsLine, e);
			}
		}
	}

	private static void parseDates(final String[] cvsLine, final String dateCol, final int year,
			final Register register) {
		if (hasData(dateCol)) {
			try {
				final int bisIndex = dateCol.indexOf(BIS);
				if (bisIndex < 0)
					throwException("string ' bis ' not found in date range column", cvsLine);
				final String dateFromStr = dateCol.substring(0, bisIndex);
				final String dateToStr = dateCol.substring(bisIndex + BIS.length(), dateCol.length());

				final LocalDate dateFrom = dateFromStr.contains("?") ? null
						: LocalDate.parse(dateFromStr + year, dateFormatter);
				final LocalDate dateTo = dateToStr.contains("?") ? null
						: LocalDate.parse(dateToStr + year, dateFormatter);

				register.setDateFrom(dateFrom);
				register.setDateTo(dateTo);
			} catch (final Exception e) {
				throwException("date ranges can't be parsed", cvsLine, e);
			}
		}
	}

	private static boolean hasData(final String numberCol) {
		return !numberCol.isBlank();
	}

	private static Archive getArchive(final String archiveCol, final String urlCol, final int year,
			final String registerType) {
		Archive archive = switch (archiveCol) {
		case "LAB" -> Archive.LANDESARCHIV_BERLIN;
		case "USC" -> Archive.STANDESAMT_BRESLAU;
		case "APW" -> Archive.STAATSARCHIV_BRESLAU;
		default -> null;
		};

		if (archive == Archive.LANDESARCHIV_BERLIN) {
			if ((isBirthRegister(registerType) && year > 1910)
					|| (isMarriageRegister(registerType) && year > 1940)
					|| (isDeathRegister(registerType) && year > 1945)) {
				archive = Archive.STANDESAMT_1_BERLIN;
			} else if (hasData(urlCol) && !"noch nicht online".equals(urlCol)) {
				archive = Archive.ANCESTRY;
			}
		}

		return archive;
	}

	private static boolean isDeathRegister(final String registerType) {
		return registerType.equals("C");
	}

	private static boolean isMarriageRegister(final String registerType) {
		return registerType.equals("B");
	}

	private static boolean isBirthRegister(final String registerType) {
		return registerType.equals("A");
	}

	private static void throwException(final String reason, final String[] cvsLine) {
		throwException(reason, cvsLine, null);
	}

	private static void throwException(final String reason, final String[] cvsLine, final Throwable e) {
		throw new RuntimeException("Line not processable (%s): %s".formatted(reason, Arrays.toString(cvsLine)), e);
	}

}
