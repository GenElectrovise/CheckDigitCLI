package checkdigitcli.command

import picocli.CommandLine

@CommandLine.Command(name = "help")
class Help : Runnable {
    override fun run() {
        println("== CheckDigitCLI ==")
        println("Copyright (c) GenElectrovise 2022")
        println("https://github.com/GenElectrovise/CheckDigitCLI")
    }
}