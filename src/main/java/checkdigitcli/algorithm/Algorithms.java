package checkdigitcli.algorithm;

import java.util.HashMap;
import java.util.Map;

public class Algorithms {

	public static final Map<String, Algorithm> ALGORITHMS;

	static {
		ALGORITHMS = new HashMap<>();
		ALGORITHMS.put("luhn", new Luhn());
	}
}
