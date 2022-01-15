package checkdigitcli.algorithm;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import com.aparapi.Kernel;

public class Luhn implements Algorithm {

	public static void main(String[] args) {

		String input1 = "7578527827894527894789237845785478923466434565689625896845686825682346824968";
		String input2 = "6";
		String input3 = "34373957395";
		byte[][] inputs = { input2.getBytes() }; // , input2.getBytes(), input3.getBytes() };

		// System.out.println(new Luhn().generate(input1));
		System.out.println(new Luhn().generate(input2));
		// System.out.println(new Luhn().generate(input3));

		LuhnKernel kernel = new LuhnKernel(inputs, new byte[inputs.length][]);
		kernel.execute(10);

		for (byte[] bs : kernel.getOutputs()) {
			System.out.println(Arrays.toString(bs));
		}

	}

	@Override
	public String getName() {
		return "luhn";
	}

	@Override
	public int generate(String input) {

		// Remove non numeric characters
		input = input.replaceAll("\\D", "");

		// Parse string to int[]
		String[] chars = input.split("");
		int[] ints = new int[chars.length];
		for (int i = 0; i < chars.length; i++) {
			ints[i] = Integer.valueOf(chars[i]);
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

		for (int j = 0, i = 0, index = (ints.length - 1); i < ints.length; j++, i = 2 * j, index = (ints.length - 1) - i) {

			// Starting i=0 (2*0=0, 2*1=2)
			// Double each second number
			int iDoubled = ints[index] * 2;

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
				ints[index] = (byte) (1 + (iDoubled - 10));
			}
			// Else (just one digit), put it straight away
			else {
				ints[index] = (byte) iDoubled;
			}
		}

		// Sum all of the digits
		int sum = 0;
		for (int i = 0; i < ints.length; i++) {
			sum += ints[i];
		}

		return 10 - (sum % 10);
	}

	public static class LuhnKernel extends Kernel {

		// [0][s, t, r, i, n, g]
		// [1][s, t, r, i, n, g]
		// [2][s, t, r, i, n, g]
		// [3][s, t, r, i, n, g]
		private byte[][] inputs, outputs;

		/**
		 * 
		 * @param inputs  Inputs, formatted as [index][s, t, r, i, n, g]. Not checked
		 *                internally, so check them for rogue characters (only numeric
		 *                allowed) before sending them here.
		 * @param outputs Outputs, formatted as [index][s, t, r, i, n, g, check_digit]
		 *                (pass an empty array of the same dimensions as inputs, +1 on
		 *                the second dimension to let the check digit be appended). The
		 *                contents of this field will be overwritten!
		 */
		public LuhnKernel(byte[][] inputs, byte[][] outputs) {
			this.inputs = inputs;

			// Turn outputs into a blank array of the same length as inputs
			// Done like this to maintain the link of the constructor.outputs to
			// this.outputs
			outputs = new byte[inputs.length][];
			this.outputs = outputs;

			put(this.inputs);
			put(this.outputs);
		}

		@Override
		public void run() {

			// Obtain the ID of the core running this kernel instance
			int id = getGlobalId();
			if (id >= inputs.length) {
				System.out.println("Not using ID " + id);
				return;
			}

			// Get the target array (the string as a byte[])
			// This corresponds to the ID! (one core per input)
			byte[] target = inputs[id];

			System.out.println(id + ": target=" + Arrays.toString(target));

			// Generate digit
			int digit = new Luhn().generate(new String(target, StandardCharsets.UTF_8));

			System.out.println(id + ": digit=" + digit);

			// Store payload and check digit to outputs
			byte[] temp = new byte[target.length + 1];
			for (int i = 0; i < target.length; i++) {
				temp[i] = target[i];
			}
			temp[temp.length - 1] = Character.toChars(digit)[0]; // Convert raw number (eg. '7') to its character (as a byte) and store back
			outputs[id] = temp;
			System.out.println(id + ": stored temp=" + Arrays.toString(temp));

		}

		public byte[][] getInputs() {
			return inputs;
		}

		public void setInputs(byte[][] inputs) {
			this.inputs = inputs;
		}

		public byte[][] getOutputs() {
			return outputs;
		}

		public void setOutputs(byte[][] outputs) {
			this.outputs = outputs;
		}

	}

}
