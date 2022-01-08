package checkdigitcli.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "generateCheckDigits")
public class GenerateCheckDigits implements Runnable {

	@Option(names = { "-if", "-inputFile" }, required = false)
	private File inputFile;

	@Option(names = { "-p", "-payloads" }, required = false)
	private List<String> manualPayloads;

	@Option(names = { "-of", "-outputFile" }, required = false)
	private File outputFile;

	@Option(names = { "-a", "-algorithm" }, required = true)
	private String algorithmName;

	public void run() {
		try {
			List<String> payloads = collectPayloads(inputFile, manualPayloads);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<String> collectPayloads(File inputFile, List<String> manualPayloads) throws IOException {

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {

			Stream<String> fileLines = reader.lines();
			Stream<String> payloadLines = manualPayloads.stream();

			return Stream.concat(fileLines, payloadLines).collect(Collectors.toList());
		}

	}

}
