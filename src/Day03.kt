import kotlin.math.pow

fun main() {

    fun isAvgBitHigh(input: List<List<String>>, digitIndex: Int): Boolean {
        val ones = input.map{ digitsForLine -> digitsForLine[digitIndex] }.count { digit -> digit == "1" }
        val zeroes = input.size - ones
        return ones >= zeroes
    }

    fun part1(input: List<String>): Pair<Int, Int> {
        val digits = input.map { textLine -> textLine.split("").filter(String::isNotBlank) }
        var gammaRateString = ""

        for (i in digits[0].indices) {
            gammaRateString += if (isAvgBitHigh(digits, i)) "1" else "0"
        }

        val gammaRate = Integer.parseInt(gammaRateString, 2)
        val bitMask = 2F.pow(digits[0].size).toInt()-1
        val epsilonRate = gammaRate.xor(bitMask)

        println("Gamma rate: $gammaRate, epsilon rate: $epsilonRate")
        return Pair(gammaRate, epsilonRate)
    }

    fun part2Filter(input: List<String>, flipped: Boolean = false): String {
        var inputDigits = input.map { line -> line.split("").filter(String::isNotBlank) }
        val bitString = if (flipped) listOf("0", "1") else listOf("1", "0")
        loop@ for(i in inputDigits.indices) {
            val keep = if (isAvgBitHigh(inputDigits, i)) bitString[0] else bitString[1]
            inputDigits = inputDigits.filter { digits -> digits[i] == keep }
            if (inputDigits.size <= 1) break@loop
        }
        return inputDigits[0].joinToString("")
    }

    fun part2(input: List<String>): Pair<Int, Int> {
        val o2Rating = Integer.parseInt(part2Filter(input), 2)
        val co2Rating = Integer.parseInt(part2Filter(input, true), 2)
        println("O2 Rating: $o2Rating, CO2 Rating: $co2Rating")
        return Pair(o2Rating, co2Rating)
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day03_test")
    check(part1(testInput1).let { pair -> pair.first * pair.second} == 198)

    val input1 = readInput("Day03")
    println("part 1: ${part1(input1).let { pair -> pair.first * pair.second}}")
    // part 1: 2967914

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1).let { pair -> pair.first * pair.second} == 230)
    println("part 2: ${part2(input1).let { pair -> pair.first * pair.second}}")
    // 7041258
}
