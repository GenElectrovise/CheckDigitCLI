package checkdigitcli.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aparapi.Kernel;

import checkdigitcli.algorithm.Algorithm;
import checkdigitcli.algorithm.Algorithms;
import checkdigitcli.io.FileUtils;
import checkdigitcli.main.CheckDigitCLIException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "generateCheckDigits", aliases = { "gcd" })
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
			Algorithm algorithm = Algorithms.getAlgorithm(algorithmName);
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

		String[] results = formatResults(digits);
		
		if (outputFile != null) {
			FileUtils.printOneLineResultsToOutputFile(results, outputFile);
		} else {
			FileUtils.printOneLineResultsToConsole(results);
		}

	}

	private String[] formatResults(String[][] digits) {
		
		String[] out = new String[digits.length];
		
		for (int i = 0; i < digits.length; i++) {
			
			StringBuilder builder = new StringBuilder();
			
			for (int j = 0; j < digits[i].length; j++) {
				builder.append(digits[i][j]);
			}
			
			out[i] = builder.toString();
		}
		
		return out;
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

	private Stream<String> collectPayloads(File inputFile, String[] manualPayloads)
			throws IOException, CheckDigitCLIException {

		Stream<String> filePayloads = FileUtils.getStreamOfLines(inputFile);
		Stream<String> sManualPayloads = (manualPayloads != null ? Stream.of(manualPayloads) : Stream.empty());

		return Stream.concat(filePayloads, sManualPayloads);
	}
	
	public static class ResultCompressorKernel extends Kernel {

		@Override
		public void run() {
			
		}
		
	}

}
