package checkdigitcli.io

import checkdigitcli.main.CheckDigitCLIException
import java.io.*
import java.util.stream.Collectors
import java.util.stream.Stream

object FileUtils {
    @JvmStatic
    @Throws(FileNotFoundException::class, CheckDigitCLIException::class, IOException::class)
    fun getStreamOfLines(file: File?): Stream<String?> {
        var lines = Stream.empty<String?>()
        if (file != null) lines = readLines(file).stream()
        return lines
    }

    @Synchronized
    @Throws(CheckDigitCLIException::class, FileNotFoundException::class, IOException::class)
    private fun readLines(file: File): List<String?> {
        if (!file.exists()) throw CheckDigitCLIException("Input file " + file.absolutePath + " does not exist.")
        BufferedReader(FileReader(file)).use { reader ->
            val fileLines = reader.lines()
            return fileLines.collect(Collectors.toList())
        }
    }

    @JvmStatic
    @Synchronized
    @Throws(IOException::class)
    fun printOneLineResultsToOutputFile(contents: Array<String>, file: File) {
        println("Writing to output file " + file.absolutePath)
        print("Progress: 0/" + contents.size)
        BufferedWriter(FileWriter(file)).use { writer ->
            writer.append("==START==" + System.lineSeparator())
            for (i in contents.indices) {
                writer.append(contents[i] + System.lineSeparator())
                print("\rProgress: " + (i + 1) + "/" + contents.size)
            }
            writer.append("==END==")
            writer.flush()
            println("\nDone!")
        }
    }

    @JvmStatic
    @Synchronized
    @Throws(IOException::class)
    fun printOneLineResultsToConsole(contents: Array<String?>) {
        println("Results: ")
        println("==START==")
        for (i in contents.indices) {
            println(contents[i])
        }
        println("==END==")
        println("Done!")
    }
}