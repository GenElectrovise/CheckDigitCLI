package checkdigitcli.algorithm;

public interface Algorithm {

	/**
	 * 
	 * @param input The bytes of a UTF-8 string of digits to generate for
	 * @return
	 */
	int generate(byte[] input);

	String getName();
}
