package checkdigitcli.algorithm;

import java.util.HashMap;
import java.util.Map;

import checkdigitcli.main.CheckDigitCLIException;

public class Algorithms {

	public static final Map<String, Algorithm> ALGORITHMS;

	static {
		ALGORITHMS = new HashMap<>();
		
		Luhn luhn = new Luhn();
		ALGORITHMS.put(luhn.getName(), luhn);
	}

	public static Algorithm getAlgorithm(String algorithmName) throws CheckDigitCLIException {

		// Check for algorithm internally
		if (Algorithms.ALGORITHMS.containsKey(algorithmName)) {
			Algorithm a = Algorithms.ALGORITHMS.get(algorithmName);
			System.out.println("Using algorithm " + a.getClass().getSimpleName());
			return a;
		}

		throw new CheckDigitCLIException("No algorithm with name " + algorithmName);
	}
}
