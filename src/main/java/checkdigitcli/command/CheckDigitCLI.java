package checkdigitcli.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CheckDigitCLI {

	public static void main(String[] args) throws IOException {
		
		System.out.println();

		if (System.console() != null) {
			runOnConsole();
		} else {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("CheckDigitCLI_ERROR.txt")))) {
				writer.write("CheckDigitCLI must be run with access to a console.\n");
				writer.write("https://github.com/GenElectrovise/CheckDigitCLI");
			}
		}
	}

	private static void runOnConsole() {
		// TODO Auto-generated method stub

	}
	
	public static void clearConsole() throws InterruptedException, IOException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}

}
