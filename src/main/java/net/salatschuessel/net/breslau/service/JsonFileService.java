package net.salatschuessel.net.breslau.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.salatschuessel.net.breslau.model.Register;

public class JsonFileService {

	private static ObjectMapper objectMapper;

	static {
		objectMapper = JsonMapper.builder()
				.constructorDetector(ConstructorDetector.EXPLICIT_ONLY)
				.build();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public void writeToJsonFile(final File jsonFile, final List<Register> registerList)
			throws IOException, StreamWriteException, DatabindException {
		objectMapper.writeValue(jsonFile, registerList);
	}

	public List<Register> loadFromJsonFile(final File jsonFile) {
		List<Register> registerList = new ArrayList<>();
		try {
			if (Files.exists(jsonFile.toPath())) {
				registerList = objectMapper.readerForListOf(Register.class).readValue(jsonFile);
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		return registerList;
	}
}
