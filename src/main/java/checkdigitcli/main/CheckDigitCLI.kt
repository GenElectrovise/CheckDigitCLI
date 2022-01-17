package checkdigitcli.main

import checkdigitcli.command.CDCLI
import picocli.CommandLine
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object CheckDigitCLI {
    var isTerminating = false
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println("Checking for a console...")
        if (System.console() != null) {
            println("Running in a console environment!")
            runOnConsole()
        } else {
            BufferedWriter(FileWriter(File("CheckDigitCLI_ERROR.txt"))).use { writer ->
                writer.write("CheckDigitCLI must be run with access to a console.\n")
                writer.write("https://github.com/GenElectrovise/CheckDigitCLI")
            }
        }
    }

    private fun runOnConsole() {
        println("Starting interactive CLI...")
        clearConsole()
        println("== CheckDigitCLI ==")
        println("Copyright (c) GenElectrovise 2022")
        println("https://github.com/GenElectrovise/CheckDigitCLI")
        println(" ")
        println("Use command 'help' for help")
        while (!isTerminating) {
            pollCommand()
        }
        println("Bye bye :'(")
        println("Exiting normally... (0)")
        System.exit(0)
    }

    private fun pollCommand() {
        println("\n")
        var command: Array<String?> = System.console().readLine().split(" ").toTypedArray()
        if (command[0] == "cdcli") {
            val tmp = arrayOfNulls<String>(command.size - 1)
            System.arraycopy(command, 1, tmp, 0, command.size - 1)
            command = tmp
        }

        /*
         * // Debug, print args for (int i = 0; i < command.length; i++) {
         * System.out.println("    - " + command[i]); }
         */CommandLine(CDCLI()).execute(*command)
    }

    fun clearConsole() {
        try {
            ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        } catch (e: InterruptedException) {
            println("Unable to clear console!")
            e.printStackTrace()
        } catch (e: IOException) {
            println("Unable to clear console!")
            e.printStackTrace()
        }
    }
}