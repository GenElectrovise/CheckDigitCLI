package checkdigitcli.algorithm

import checkdigitcli.main.CheckDigitCLIException

object Algorithms {
    private val ALGORITHMS: MutableMap<String, Algorithm> = HashMap()

    init {

        val luhn = Luhn()
        ALGORITHMS[luhn.name] = luhn
    }

    @Throws(CheckDigitCLIException::class)
    fun getAlgorithm(algorithmName: String): Algorithm {

        // Check for algorithm internally
        if (ALGORITHMS.containsKey(algorithmName)) {
            val a = ALGORITHMS[algorithmName]
            println("Using algorithm " + a!!.javaClass.simpleName)
            return a
        }
        throw CheckDigitCLIException("No algorithm with name $algorithmName")
    }
}