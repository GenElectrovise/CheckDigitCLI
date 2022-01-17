package checkdigitcli.command

import picocli.CommandLine

@CommandLine.Command(name = "cdcli", subcommands = [Exit::class, Help::class, GenerateCheckDigits::class])
class CDCLI : Runnable {
    override fun run() {
        println("Use the command 'help' for help")
    }
}