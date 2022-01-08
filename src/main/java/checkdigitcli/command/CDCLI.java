package checkdigitcli.command;

import picocli.CommandLine.Command;

@Command( //
		name = "cdcli", //
		subcommands = {Exit.class, Help.class, GenerateCheckDigits.class} //
)
public class CDCLI implements Runnable {

	@Override
	public void run() {
		System.out.println("Use the command 'help' for help");
	}

}
