package checkdigitcli.command

import checkdigitcli.algorithm.Algorithm
import checkdigitcli.algorithm.Algorithms
import checkdigitcli.algorithm.Luhn.LuhnKernel
import checkdigitcli.io.FileUtils.getStreamOfLines
import checkdigitcli.io.FileUtils.printOneLineResultsToConsole
import checkdigitcli.io.FileUtils.printOneLineResultsToOutputFile
import checkdigitcli.main.CheckDigitCLIException
import com.aparapi.Kernel
import picocli.CommandLine
import java.io.File
import java.io.IOException
import java.util.stream.Collectors
import java.util.stream.Stream

@CommandLine.Command(name = "generateCheckDigits", aliases = ["gcd"])
class GenerateCheckDigits : Runnable {
    @CommandLine.Option(names = ["-if", "-inputFile"], required = false)
    private val inputFile: File? = null

    @CommandLine.Option(names = ["-p", "-payloads"], required = false)
    private val manualPayloads: Array<String>

    @CommandLine.Option(names = ["-of", "-outputFile"], required = false)
    private val outputFile: File? = null

    @CommandLine.Option(names = ["-a", "-algorithm"], required = true)
    private val algorithmName: String? = null

    @CommandLine.Option(names = ["-k", "-kernel", "-gpu"], required = false, defaultValue = "true")
    private val useKernel = false
    override fun run() {
        try {
            val algorithm = Algorithms.getAlgorithm(algorithmName)
            val payloads = collectPayloads(inputFile, manualPayloads)
            val digits = applyAlgorithm(algorithm, payloads)
            displayResults(digits, outputFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        } catch (e: CheckDigitCLIException) {
            System.err.println(e.message)
            return
        } catch (e: InterruptedException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun displayResults(digits: Array<ByteArray?>, outputFile: File?) {
        val results = formatResults(digits)
        if (outputFile != null) {
            printOneLineResultsToOutputFile(results, outputFile)
        } else {
            printOneLineResultsToConsole(results)
        }
    }

    private fun formatResults(digits: Array<ByteArray?>): Array<String?> {
        val out = arrayOfNulls<String>(digits.size)
        for (i in digits.indices) {
            val builder = StringBuilder()
            for (j in digits[i]!!.indices) {
                builder.append(digits[i]!![j].toInt())
            }
            out[i] = builder.toString()
        }
        return out
    }

    private fun applyAlgorithm(algorithm: Algorithm, sPayloads: Stream<String?>): Array<ByteArray?> {
        val start = System.nanoTime()
        val lPayloads = sPayloads.collect(Collectors.toList())

        // Generate preview
        val numberToPreview = 10
        var payloadsPreview = ""
        for (i in 0 until if (lPayloads.size < numberToPreview) lPayloads.size else numberToPreview) {
            payloadsPreview += (if (payloadsPreview == "") "" else " , ") + lPayloads[i]
        }
        if (lPayloads.size > numberToPreview) payloadsPreview += " ... (" + (lPayloads.size - numberToPreview) + " more)"

        // Show preview
        println("Using " + algorithm.name + " algorithm")
        println("Found " + lPayloads.size + " payloads: " + payloadsPreview)

        // Apply algorithm to all
        // Create input/output arrays
        val inputs = arrayOfNulls<ByteArray>(lPayloads.size) // Height=long, Width=2
        var outputs = inputs.clone()

        /*
         * [i] = [ pay load , CD ]
         * [0] = [6, 6, 3, 4, CD ]
         * [1] = [2, 3, 1, 7, CD ]
         */

        // Fill input array
        for (i in lPayloads.indices) {
            inputs[i] = lPayloads[i]!!.toByteArray()
        }
        println(if (useKernel) "Using Aparapi kernel" else "Not using Aparapi kernel")
        // To use the kernel, or not to use the kernel, that is the if-statement:
        if (useKernel) {
            val kernel = LuhnKernel(inputs, outputs)
            kernel.execute(inputs.size)

            // kernel.get(kernel.getOutputs()); // Fetch the buffer from the GPU
            outputs = kernel.outputs
        } else  /* (!useKernel) */ {
            for (i in inputs.indices) {
                val digit = algorithm.generate(inputs[i])
                outputs[i] = ByteArray(inputs[i]!!.size + 1)
                outputs[i]!![outputs[i]!!.size - 1] = digit.toByte()
                outputs[i]!![1] = 0xFF.toByte() //String.valueOf(digit).getBytes();

                /*
                 * [i] = [ pay load , CD ]
                 * [0] = [6, 6, 3, 4, CD ]
                 * [1] = [2, 3, 1, 7, CD ]
                 */
            }
        }

        /*
         * for (int i = 0; i < lPayloads.size(); i++) { String p = lPayloads.get(i); int
         * digit = algorithm.generate(p.getBytes()); outputs[i][0] = p; outputs[i][1] =
         * String.valueOf(digit); }
         */println(
            String.format(
                "Took %dms to compute %d values",
                (System.nanoTime() - start) / 1000000,
                lPayloads.size
            )
        )
        return outputs
    }

    @Throws(IOException::class, CheckDigitCLIException::class)
    private fun collectPayloads(inputFile: File?, manualPayloads: Array<String>?): Stream<String?> {
        val filePayloads = getStreamOfLines(inputFile)
        val sManualPayloads = if (manualPayloads != null) Stream.of(*manualPayloads) else Stream.empty()
        return Stream.concat(filePayloads, sManualPayloads)
    }

    class ResultCompressorKernel : Kernel() {
        override fun run() {}
    }
}