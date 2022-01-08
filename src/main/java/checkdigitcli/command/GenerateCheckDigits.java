package checkdigitcli.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import checkdigitcli.algorithm.Algorithm;
import checkdigitcli.algorithm.Algorithms;
import checkdigitcli.main.CheckDigitCLIException;
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
			Algorithm algorithm = getAlgorithmInstance(algorithmName);
			List<String> payloads = collectPayloads(inputFile, manualPayloads);
			applyAlgorithm(algorithm, payloads);

		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (CheckDigitCLIException e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	private void applyAlgorithm(Algorithm algorithm, List<String> payloads) {

		final int numberToPreview = 10;
		String payloadsPreview = "";
		for (int i = 0; i < (payloads.size() < numberToPreview ? payloads.size() : numberToPreview); i++) {
			payloadsPreview = payloadsPreview + (payloadsPreview.equals("") ? "" : " , ") + payloads.get(i);
		}
		if(payloads.size() > numberToPreview) 
			payloadsPreview += " ... (" + (payloads.size() - numberToPreview) + " more)";
			
		
		System.out.println("Using " + algorithm.getName() + " algorithm");
		System.out.println("Found " + payloads.size() + " payloads: " + payloadsPreview);

	}

	private Algorithm getAlgorithmInstance(String algorithmName) throws CheckDigitCLIException {

		// Check for algorithm internally
		if (Algorithms.ALGORITHMS.containsKey(algorithmName)) {
			Algorithm a = Algorithms.ALGORITHMS.get(algorithmName);
			System.out.println("Using algorithm " + a.getClass().getSimpleName());
			return a;
		}

		throw new CheckDigitCLIException("No algorithm with name " + algorithmName);
	}

	private List<String> collectPayloads(File inputFile, List<String> manualPayloads)
			throws IOException, CheckDigitCLIException {

		Stream<String> filePayloads = Stream.empty();
		if (inputFile != null)
			filePayloads = getFilePayloads(inputFile).stream();

		Stream<String> sManualPayloads = Stream.empty();
		if (manualPayloads != null) {
			sManualPayloads = manualPayloads.stream();
		}

		return Stream.concat(filePayloads, sManualPayloads).collect(Collectors.toList());
	}

	private List<String> getFilePayloads(File file)
			throws CheckDigitCLIException, FileNotFoundException, IOException {

		if (!inputFile.exists())
			throw new CheckDigitCLIException("Input file " + inputFile.getAbsolutePath() + " does not exist.");

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			Stream<String> fileLines = reader.lines();
			return fileLines.collect(Collectors.toList());
		}
	}

}
