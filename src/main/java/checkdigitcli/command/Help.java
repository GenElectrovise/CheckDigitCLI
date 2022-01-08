package checkdigitcli.command;

import picocli.CommandLine.Command;

@Command(name = "help")
public class Help implements Runnable {

	@Override
	public void run() {
		System.out.println("== CheckDigitCLI ==");
		System.out.println("Copyright (c) GenElectrovise 2022");
		System.out.println("https://github.com/GenElectrovise/CheckDigitCLI");
	}
}
