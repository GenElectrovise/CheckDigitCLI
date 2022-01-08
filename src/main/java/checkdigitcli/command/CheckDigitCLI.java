package checkdigitcli.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import picocli.CommandLine;
import picocli.CommandLine.Command;

public class CheckDigitCLI {

	public static void main(String[] args) throws IOException {

		if (System.console() != null) {
			runOnConsole();
		} else {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("CheckDigitCLI_ERROR.txt")))) {

			}
		}

		CommandLine.run(null, args);
	}

	private static void runOnConsole() {
		// TODO Auto-generated method stub

	}

}
