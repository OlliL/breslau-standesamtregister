package net.salatschuessel.breslau;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import net.salatschuessel.breslau.model.Register;
import net.salatschuessel.breslau.service.JsonFileService;
import net.salatschuessel.breslau.service.TableCreatorService;
import net.salatschuessel.breslau.service.UserInputService;

public class Main {

	public static void main(final String[] args) throws URISyntaxException, IOException {

		final UserInputService userInputService = new UserInputService();
		final JsonFileService jsonFileService = new JsonFileService();
		final TableCreatorService tableCreatorService = new TableCreatorService();

		final var registerFile = userInputService.requestRegisterFile();

		final File jsonFile = registerFile.getJsonFile();
		final List<Register> registerList = jsonFileService.loadFromJsonFile(jsonFile);
		try {
			userInputService.requestUserInput(registerList);
		} finally {
			jsonFileService.writeToJsonFile(jsonFile, registerList);
		}

		tableCreatorService.create(registerFile, registerList);
	}

}
