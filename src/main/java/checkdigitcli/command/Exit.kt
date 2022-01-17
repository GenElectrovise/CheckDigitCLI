package checkdigitcli.command

import checkdigitcli.main.CheckDigitCLI
import picocli.CommandLine

@CommandLine.Command(name = "exit", aliases = ["quit", "terminate"])
class Exit : Runnable {
    override fun run() {
        println("Exiting...")
        CheckDigitCLI.isTerminating = true
    }
}