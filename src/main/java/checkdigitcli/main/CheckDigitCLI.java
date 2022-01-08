package checkdigitcli.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import checkdigitcli.command.CDCLI;
import picocli.CommandLine;

public class CheckDigitCLI {

	private static boolean terminating = false;

	public static void main(String[] args) throws IOException {

		System.out.println("Checking for a console...");

		if (System.console() != null) {
			System.out.println("Running in a console environment!");
			runOnConsole();
		} else {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("CheckDigitCLI_ERROR.txt")))) {
				writer.write("CheckDigitCLI must be run with access to a console.\n");
				writer.write("https://github.com/GenElectrovise/CheckDigitCLI");
			}
		}
	}

	private static void runOnConsole() {
		System.out.println("Starting interactive CLI...");
		clearConsole();
		System.out.println("== CheckDigitCLI ==");
		System.out.println("Copyright (c) GenElectrovise 2022");
		System.out.println("https://github.com/GenElectrovise/CheckDigitCLI");
		System.out.println(" ");
		System.out.println("Use command 'help' for help");

		while (!terminating) {
			pollCommand();
		}

		System.out.println("Bye bye :'(");
		System.out.println("Exiting normally... (0)");
		System.exit(0);
	}

	private static void pollCommand() {
		String commandIn = System.console().readLine();
		String[] argsIn = commandIn.split(" ");
		String[] argsNoCommand = new String[argsIn.length - 1];

		System.arraycopy(argsIn, 1, argsNoCommand, 0, argsNoCommand.length);

		new CommandLine(new CDCLI()).execute();
	}

	public static void clearConsole() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (InterruptedException | IOException e) {
			System.out.println("Unable to clear console!");
			e.printStackTrace();
		}
	}

	public static boolean isTerminating() {
		return terminating;
	}

	public static void setTerminating(boolean terminating) {
		CheckDigitCLI.terminating = terminating;
	}
}
