package checkdigitcli.command;

import picocli.CommandLine.Command;

@Command( //
		name = "cdcli", //
		aliases = {"help"},
		subcommands = {} //
)
public class CDCLI implements Runnable {

	@Override
	public void run() {
		System.out.println("= HELP =");
		System.out.println("== CheckDigitCLI ==");
		System.out.println("Copyright (c) GenElectrovise 2022");
		System.out.println("https://github.com/GenElectrovise/CheckDigitCLI");
	}

}
