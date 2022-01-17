package checkdigitcli.algorithm

import com.aparapi.Kernel
import java.nio.charset.StandardCharsets
import java.util.*

class Luhn : Algorithm {
    override val name: String
        get() = "luhn"

    override fun generate(inputBytes: ByteArray?): Int {
        var input = String(inputBytes!!, StandardCharsets.UTF_8)

        // Remove non numeric characters
        input = input.replace("\\D".toRegex(), "")

        // Parse string to int[]
        val chars = input.split("").toTypedArray()
        val ints = IntArray(chars.size)
        for (i in chars.indices) {
            ints[i] = Integer.valueOf(chars[i])
        }

        // For every other, from the right to the left
        // Start at zero
        // i is always 2j
        // While i < length
        // index is the maximum value of itself (length-1), minus i (to invert it)

        // When length=5, j=0 => i=0 => index=4-i=4
        // When length=5, j=i => i=2 => index=4-2=2
        // When length=5, j=2 => i=4 => index=4-i=0
        // When length=5, j=3 => i=6, i>4 so stops
        run {
            var j = 0
            var i = 0
            var index = ints.size - 1
            while (i < ints.size) {


                // Starting i=0 (2*0=0, 2*1=2)
                // Double each second number
                val iDoubled = ints[index] * 2

                /*
			 * // Sum the digits of the doubled number String sDoubled =
			 * String.valueOf(iDoubled); String[] dChars = sDoubled.split("");
			 * 
			 * // If there are two digits, put their sum if (dChars.length == 2) {
			 * ints[index] = Integer.valueOf(dChars[0]) + Integer.valueOf(dChars[1]); } //
			 * Else (just one digit), put it straight away else { ints[index] =
			 * Integer.valueOf(dChars[0]); }
			 */

                // If there are two digits, put their sum
                if (iDoubled >= 10) {
                    ints[index] = (1 + (iDoubled - 10)).toByte().toInt()
                } else {
                    ints[index] = iDoubled.toByte().toInt()
                }
                j++
                i = 2 * j
                index = ints.size - 1 - i
            }
        }

        // Sum all of the digits
        var sum = 0
        for (i in ints.indices) {
            sum += ints[i]
        }
        return 10 - sum % 10
    }

    class LuhnKernel(inputs: Array<ByteArray>, outputs: Array<ByteArray?>?) : Kernel() {
        // [0][s, t, r, i, n, g]
        // [1][s, t, r, i, n, g]
        // [2][s, t, r, i, n, g]
        // [3][s, t, r, i, n, g]
        var inputs: Array<ByteArray>
        var outputs: Array<ByteArray?>

        /**
         *
         * @param inputs  Inputs, formatted as [index][s, t, r, i, n, g]. Not checked
         * internally, so check them for rogue characters (only numeric
         * allowed) before sending them here.
         * @param outputs Outputs, formatted as [index][s, t, r, i, n, g, check_digit]
         * (pass an empty array of the same dimensions as inputs, +1 on
         * the second dimension to let the check digit be appended). The
         * contents of this field will be overwritten!
         */
        init {
            var outputs = outputs
            this.inputs = inputs

            // Turn outputs into a blank array of the same length as inputs
            // Done like this to maintain the link of the constructor.outputs to
            // this.outputs
            outputs = arrayOfNulls(inputs.size)
            this.outputs = outputs
            put(this.inputs)
            put(this.outputs)
        }

        override fun run() {

            // Obtain the ID of the core running this kernel instance
            val id = globalId
            if (id >= inputs.size) {
                println("Not using ID $id")
                return
            }

            // Get the target array (the string as a byte[])
            // This corresponds to the ID! (one core per input)
            val target = inputs[id]

            // System.out.println(id + ": target=" + Arrays.toString(target));

            // Generate digit
            val digit = Luhn().generate(target)

            // System.out.println(id + ": digit=" + digit);

            // Store payload and check digit to outputs
            val temp = ByteArray(target.size + 1)
            for (i in target.indices) {
                temp[i] = target[i]
            }

            // Convert byte containing the number 7 to the string '7'
            temp[temp.size - 1] = digit.toString()
                .toByteArray()[0] // Convert raw number (eg. digit=7) to its character (as a byte) and store back
            outputs[id] = temp
            // System.out.println(id + ": stored temp=" + Arrays.toString(temp));
            // System.out.println(id + ": temp str=" + new String(temp,
            // StandardCharsets.UTF_8));
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val input1 = "7578527827894527894789237845785478923466434565689625896845686825682346824968"
            val input2 = "6"
            val input3 = "34373957395"
            val inputs = arrayOf(input2.toByteArray()) // , input2.getBytes(), input3.getBytes() };

            // System.out.println(new Luhn().generate(input1));
            println(Luhn().generate(input2.toByteArray()))
            // System.out.println(new Luhn().generate(input3));
            val kernel = LuhnKernel(inputs, arrayOfNulls(inputs.size))
            kernel.execute(10)
            for (bs in kernel.outputs) {
                println(Arrays.toString(bs))
            }
        }
    }
}