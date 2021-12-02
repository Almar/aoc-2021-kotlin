fun main() {

    // How many measurements are larger than the previous measurement?
    fun part1(input: List<Int>): Int {
        var result = 0
        for (i in 1..input.lastIndex) {
            if (input[i] > input[i-1]) result++
        }
        return result
    }

    // From JetBrains walk through:
    fun part1b(input: List<Int>): Int {
        return input.windowed(2).count { (a, b) -> a < b }
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

    // From JetBrains walk through:
    fun part2b(input: List<Int>): Int {
        return input.windowed(3).windowed(2).count { (a, b) -> a.sum() < b.sum() }
    }

    // From JetBrains walk through, iteration 2:
    // (A + B + C) < (B + C + D) ==> A < D
    fun part2c(input: List<Int>): Int {
        return input.windowed(4).count { it[0] < it[3] }
    }


    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day01_1_test").map { it.toInt() }
    check(part1(testInput1) == 7)

    val input1 = readInput("Day01_1").map { it.toInt() }
    println("part 1: ${part1(input1)}")
    println("part 1b: ${part1b(input1)}")

    // test if implementation meets criteria from the description, like:
    val testInput2 = readInput("Day01_2_test").map { it.toInt() }
    check(part2(testInput2) == 5)

    val input2 = readInput("Day01_2").map { it.toInt() }
    println("part 2: ${part2(input2)}")
    println("part 2b: ${part2b(input2)}")
    println("part 2c: ${part2c(input2)}")
}
