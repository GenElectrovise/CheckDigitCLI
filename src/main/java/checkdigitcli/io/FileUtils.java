package checkdigitcli.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import checkdigitcli.main.CheckDigitCLIException;

public class FileUtils {

	public static Stream<String> getStreamOfLines(File file)
			throws FileNotFoundException, CheckDigitCLIException, IOException {
		Stream<String> lines = Stream.empty();
		if (file != null)
			lines = readLines(file).stream();
		return lines;
	}

	private static synchronized List<String> readLines(File file)
			throws CheckDigitCLIException, FileNotFoundException, IOException {

		if (!file.exists())
			throw new CheckDigitCLIException("Input file " + file.getAbsolutePath() + " does not exist.");

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			Stream<String> fileLines = reader.lines();
			return fileLines.collect(Collectors.toList());
		}
	}

	public static synchronized void printOneLineResultsToOutputFile(String[] contents, File file) throws IOException {

		System.out.println("Writing to output file " + file.getAbsolutePath());
		System.out.print("Progress: 0/" + contents.length);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

			writer.append("==START==" + System.lineSeparator());
			for (int i = 0; i < contents.length; i++) {
				writer.append(contents[i] + System.lineSeparator());
				System.out.print("\rProgress: " + (i + 1) + "/" + contents.length);
			}
			writer.append("==END==");

			writer.flush();
			System.out.println("\nDone!");
		}
	}

	public static synchronized void printOneLineResultsToConsole(String[] contents) throws IOException {

		System.out.println("Results: ");
		System.out.println("==START==");
		for (int i = 0; i < contents.length; i++) {
			System.out.println(contents[i]);
		}
		System.out.println("==END==");
		System.out.println("Done!");
	}

}
