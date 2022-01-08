package checkdigitcli.command;

import checkdigitcli.main.CheckDigitCLI;
import picocli.CommandLine.Command;

@Command(name = "exit", aliases = { "quit", "terminate" })
public class Exit implements Runnable {

	@Override
	public void run() {
		System.out.println("Exiting...");
		CheckDigitCLI.setTerminating(true);
	}
}
