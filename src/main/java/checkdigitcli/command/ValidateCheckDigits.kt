package checkdigitcli.command

import picocli.CommandLine

@CommandLine.Command(name = "validateCheckDigits", aliases = ["vcd"])
class ValidateCheckDigits : Runnable {
    override fun run() {}
}