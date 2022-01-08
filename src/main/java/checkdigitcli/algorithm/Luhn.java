package checkdigitcli.algorithm;

public class Luhn implements Algorithm {

	public static void main(String[] args) {
		System.out.println(new Luhn().generate("4502984895890288945890528934--8259057-8778425547425823468568964523469589282869gh34289342gh89gh82h8934g2h89gh8929gh0g3hhg3892h8h89"));
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

			// Sum the digits of the doubled number
			String sDoubled = String.valueOf(iDoubled);
			String[] dChars = sDoubled.split("");

			// If there are two digits, put their sum
			if (dChars.length == 2) {
				ints[index] = Integer.valueOf(dChars[0]) + Integer.valueOf(dChars[1]);
			}
			// Else (just one digit), put it straight away
			else {
				ints[index] = Integer.valueOf(dChars[0]);
			}
		}

		// Sum all of the digits
		int sum = 0;
		for (int i = 0; i < ints.length; i++) {
			sum += ints[i];
		}

		return 10 - (sum % 10);
	}

}
