package checkdigitcli.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
	private String[] manualPayloads;

	@Option(names = { "-of", "-outputFile" }, required = false)
	private File outputFile;

	@Option(names = { "-a", "-algorithm" }, required = true)
	private String algorithmName;

	public void run() {
		try {
			Algorithm algorithm = getAlgorithmInstance(algorithmName);
			Stream<String> payloads = collectPayloads(inputFile, manualPayloads);
			String[][] digits = applyAlgorithm(algorithm, payloads);
			displayResults(digits, outputFile);

		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (CheckDigitCLIException e) {
			System.err.println(e.getMessage());
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void displayResults(String[][] digits, File outputFile) throws IOException, InterruptedException {

		if (outputFile != null) {
			printToOutputFile(digits, outputFile);
		} else {
			printToConsole(digits, outputFile);
		}

	}

	private void printToOutputFile(String[][] digits, File outputFile) throws IOException, InterruptedException {

		System.out.println("Writing to output file " + outputFile.getAbsolutePath());
		System.out.print("Progress: 0/" + digits.length);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			
			writer.append("==START==" + System.lineSeparator());			
			for (int i = 0; i < digits.length; i++) {
				String payload = digits[i][0];
				String digit = digits[i][1];

				writer.append(payload + " " + digit + System.lineSeparator());
				System.out.print("\rProgress: " + (i + 1) +  "/" + digits.length);
			}
			writer.append("==END==");
			
			writer.flush();
			System.out.println("\nDone!");
		}
	}

	private void printToConsole(String[][] digits, File outputFile) {
		
		System.out.println("Results:");
		System.out.println("==START==");
		for (int i = 0; i < digits.length; i++) {
			String payload = digits[i][0];
			String digit = digits[i][1];

			System.out.println(payload + " " + digit);
		}
		System.out.println("==END==");
	}

	private String[][] applyAlgorithm(Algorithm algorithm, Stream<String> sPayloads) {

		List<String> lPayloads = sPayloads.collect(Collectors.toList());

		// Generate preview
		final int numberToPreview = 10;
		String payloadsPreview = "";
		for (int i = 0; i < (lPayloads.size() < numberToPreview ? lPayloads.size() : numberToPreview); i++) {
			payloadsPreview += (payloadsPreview.equals("") ? "" : " , ") + lPayloads.get(i);
		}
		if (lPayloads.size() > numberToPreview)
			payloadsPreview += " ... (" + (lPayloads.size() - numberToPreview) + " more)";

		// Show preview
		System.out.println("Using " + algorithm.getName() + " algorithm");
		System.out.println("Found " + lPayloads.size() + " payloads: " + payloadsPreview);

		// Apply algorithm to all
		String[][] digits = new String[lPayloads.size()][2];
		for (int i = 0; i < lPayloads.size(); i++) {
			String p = lPayloads.get(i);
			int digit = algorithm.generate(p);
			digits[i][0] = p;
			digits[i][1] = String.valueOf(digit);

			// 0 - [payload][check digit]
			// 1 - [payload][check digit]
			// 2 - [payload][check digit]
			// ^ iterate this
		}

		return digits;
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

	private Stream<String> collectPayloads(File inputFile, String[] manualPayloads)
			throws IOException, CheckDigitCLIException {

		Stream<String> filePayloads = Stream.empty();
		if (inputFile != null)
			filePayloads = getFilePayloads(inputFile).stream();

		Stream<String> sManualPayloads = Stream.empty();
		if (manualPayloads != null) {
			sManualPayloads = Stream.of(manualPayloads);
		}

		return Stream.concat(filePayloads, sManualPayloads);
	}

	private List<String> getFilePayloads(File file) throws CheckDigitCLIException, FileNotFoundException, IOException {

		if (!inputFile.exists())
			throw new CheckDigitCLIException("Input file " + inputFile.getAbsolutePath() + " does not exist.");

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			Stream<String> fileLines = reader.lines();
			return fileLines.collect(Collectors.toList());
		}
	}

}
