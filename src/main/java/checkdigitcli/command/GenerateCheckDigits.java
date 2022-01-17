package checkdigitcli.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aparapi.Kernel;

import checkdigitcli.algorithm.Algorithm;
import checkdigitcli.algorithm.Algorithms;
import checkdigitcli.algorithm.Luhn.LuhnKernel;
import checkdigitcli.io.FileUtils;
import checkdigitcli.main.CheckDigitCLIException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "generateCheckDigits", aliases = {"gcd"})
public class GenerateCheckDigits implements Runnable {

    @Option(names = {"-if", "-inputFile"}, required = false)
    private File inputFile;

    @Option(names = {"-p", "-payloads"}, required = false)
    private String[] manualPayloads;

    @Option(names = {"-of", "-outputFile"}, required = false)
    private File outputFile;

    @Option(names = {"-a", "-algorithm"}, required = true)
    private String algorithmName;

    @Option(names = {"-k", "-kernel", "-gpu"}, required = false, defaultValue = "true")
    private boolean useKernel;

    @Override
    public void run() {
        try {
            Algorithm algorithm = Algorithms.getAlgorithm(algorithmName);
            Stream<String> payloads = collectPayloads(inputFile, manualPayloads);
            byte[][] digits = applyAlgorithm(algorithm, payloads);
            displayResults(digits, outputFile);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (CheckDigitCLIException e) {
            System.err.println(e.getMessage());
            return;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void displayResults(byte[][] digits, File outputFile) throws IOException, InterruptedException {

        String[] results = formatResults(digits);

        if (outputFile != null) {
            FileUtils.printOneLineResultsToOutputFile(results, outputFile);
        } else {
            FileUtils.printOneLineResultsToConsole(results);
        }

    }

    private String[] formatResults(byte[][] digits) {

        String[] out = new String[digits.length];

        for (int i = 0; i < digits.length; i++) {

            StringBuilder builder = new StringBuilder();

            for (int j = 0; j < digits[i].length; j++) {
                builder.append(digits[i][j]);
            }

            out[i] = builder.toString();
        }

        return out;
    }

    private byte[][] applyAlgorithm(Algorithm algorithm, Stream<String> sPayloads) {

        long start = System.nanoTime();

        List<String> lPayloads = sPayloads.collect(Collectors.toList());

        // Generate preview
        final int numberToPreview = 10;
        String payloadsPreview = "";
        for (int i = 0; i < (lPayloads.size() < numberToPreview ? lPayloads.size() : numberToPreview); i++) {
            payloadsPreview += (payloadsPreview.equals("") ? "" : " , ") + lPayloads.get(i);
        }
        if (lPayloads.size() > numberToPreview)
            payloadsPreview += " ... (" + (lPayloads.size() - numberToPreview) + " more)";

        // Show preview
        System.out.println("Using " + algorithm.getName() + " algorithm");
        System.out.println("Found " + lPayloads.size() + " payloads: " + payloadsPreview);

        // Apply algorithm to all
        // Create input/output arrays
        byte[][] inputs = new byte[lPayloads.size()][]; // Height=long, Width=2
        byte[][] outputs = inputs.clone();

        /*
         * [i] = [ pay load , CD ]
         * [0] = [6, 6, 3, 4, CD ]
         * [1] = [2, 3, 1, 7, CD ]
         */

        // Fill input array
        for (int i = 0; i < lPayloads.size(); i++) {
            inputs[i] = lPayloads.get(i).getBytes();
        }

        System.out.println(useKernel ? "Using Aparapi kernel" : "Not using Aparapi kernel");
        // To use the kernel, or not to use the kernel, that is the if-statement:
        if (useKernel) {

            LuhnKernel kernel = new LuhnKernel(inputs, outputs);
            kernel.execute(inputs.length);

            // kernel.get(kernel.getOutputs()); // Fetch the buffer from the GPU
            outputs = kernel.getOutputs();

        } else /* (!useKernel) */ {

            for (int i = 0; i < inputs.length; i++) {
                int digit = algorithm.generate(inputs[i]);

                outputs[i] = new byte[inputs[i].length + 1];
                outputs[i][outputs[i].length - 1] = (byte) digit;
                outputs[i][1] = (byte) 0xFF; //String.valueOf(digit).getBytes();

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
         */

        System.out.println(String.format("Took %dms to compute %d values", ((System.nanoTime() - start) / 1_000_000), lPayloads.size()));

        return outputs;
    }

    private Stream<String> collectPayloads(File inputFile, String[] manualPayloads) throws IOException, CheckDigitCLIException {

        Stream<String> filePayloads = FileUtils.getStreamOfLines(inputFile);
        Stream<String> sManualPayloads = (manualPayloads != null ? Stream.of(manualPayloads) : Stream.empty());

        return Stream.concat(filePayloads, sManualPayloads);
    }

    public static class ResultCompressorKernel extends Kernel {

        @Override
        public void run() {

        }

    }

}
