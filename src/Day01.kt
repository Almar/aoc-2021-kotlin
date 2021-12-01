fun main() {
    fun part1(input: List<Int>): Int {
        var result = 0
        for (i in 1..input.lastIndex) {
            if (input[i] > input[i-1]) result++
        }
        return result
    }

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

    val input1Test = readInput("Day01_1_test").map { it.toInt() }
    check(part1(input1Test) == 7)

    val input1 = readInput("Day01_1").map { it.toInt() }
    println("part 1: ${part1(input1)}")

    val input2Test = readInput("Day01_2_test").map { it.toInt() }
    check(part2(input2Test) == 5)

    val input2 = readInput("Day01_2").map { it.toInt() }
    println("part 2: ${part2(input2)}")
}
