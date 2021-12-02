fun main() {

    // How many measurements are larger than the previous measurement?
    fun part1(input: List<Int>): Int {
        var result = 0
        for (i in 1..input.lastIndex) {
            if (input[i] > input[i-1]) result++
        }
        return result
    }

    // Consider sums of a three-measurement sliding window. How many sums are larger than the previous sum?
    fun part2(input: List<Int>): Int {
        if (input.size < 3) {
            return 0
        }

        val sums = input.mapIndexed { index, value ->
            if (index > input.lastIndex - 2) {
                0
            } else {
                value + input[index+1] + input[index+2]
            }
        }

        return part1(sums)
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day01_1_test").map { it.toInt() }
    check(part1(testInput1) == 7)

    val input1 = readInput("Day01_1").map { it.toInt() }
    println("part 1: ${part1(input1)}")

    // test if implementation meets criteria from the description, like:
    val testInput2 = readInput("Day01_2_test").map { it.toInt() }
    check(part2(testInput2) == 5)

    val input2 = readInput("Day01_2").map { it.toInt() }
    println("part 2: ${part2(input2)}")
}
