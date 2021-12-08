import kotlin.math.abs

fun main() {

    fun part1(input: List<Int>): Int {
        val min = input.minOf{ x -> x }
        val max = input.maxOf{ x -> x }
        var minSteps = Int.MAX_VALUE
        for (i in min..max) {
            var steps = 0
            input.forEach { x -> steps += Math.abs(x-i) }
            minSteps = minSteps.coerceAtMost(steps)
        }
        println("Minimal steps: $minSteps")
        return minSteps
    }

    fun part2(input: List<Int>): Int {
        val min = input.minOf{ x -> x }
        val max = input.maxOf{ x -> x }
        val costs = buildList(max) {
            var prev = 0
            add(prev)
            for (i in 1..max) {
                add(prev + i)
                prev += i
            }
        }

        var minSteps = Int.MAX_VALUE
        for (i in min..max) {
            var steps = 0
            input.forEach { x -> steps += costs[abs(x-i)] }
            minSteps = minSteps.coerceAtMost(steps)
        }
        println("Minimal steps: $minSteps")
        return minSteps
    }

    fun part2b(input: List<Int>): Int {
        val min = input.minOf{ x -> x }
        val max = input.maxOf{ x -> x }
        var minSteps = Int.MAX_VALUE
        for (i in min..max) {
            val steps = input.map { x -> abs(x - i) }.sumOf { delta -> delta * (delta + 1) / 2 }
            minSteps = minSteps.coerceAtMost(steps)
        }
        return minSteps
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day07_test")[0].split(",").map { it.toInt() }
    check(part1(testInput1) == 37)

    val input1 = readInput("Day07")[0].split(",").map { it.toInt() }
    println("part 1: ${part1(input1)}") // 347449

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 168)

    println("part 2: ${part2(input1)}") // 98039527
    println("part 2b: ${part2b(input1)}") // 98039527
}
