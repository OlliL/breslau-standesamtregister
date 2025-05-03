package net.salatschuessel.net.breslau;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import net.salatschuessel.net.breslau.model.Register;
import net.salatschuessel.net.breslau.service.JsonFileService;
import net.salatschuessel.net.breslau.service.TableCreatorService;
import net.salatschuessel.net.breslau.service.UserInputService;

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
