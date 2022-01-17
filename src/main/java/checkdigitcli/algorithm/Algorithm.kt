package checkdigitcli.algorithm

interface Algorithm {
    /**
     *
     * @param input The bytes of a UTF-8 string of digits to generate for
     * @return
     */
    fun generate(input: ByteArray?): Int
    val name: String?
}