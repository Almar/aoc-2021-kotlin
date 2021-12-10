import java.util.Stack

fun main() {
    val closingScorePart1 = mapOf<Char, Int>(Pair(')', 3), Pair(']', 57), Pair('}', 1197), Pair('>', 25137))
    val closingScorePart2 = mapOf<Char, Int>(Pair(')', 1), Pair(']', 2), Pair('}', 3), Pair('>', 4))
    val opening = listOf('(', '[', '{', '<')
    val closing = listOf(')', ']', '}', '>')

    fun part1(input: List<String>): Int {
        return input.sumOf { s: String ->
            val stack = Stack<Char>()
            var lastChar: Char = ' '
            val invalid = !s.asIterable().all { c ->
                lastChar = c
                if (c in opening) {
                    stack.add(c)
                } else {
                    c == closing[opening.indexOf(stack.pop())]
                }
            }
            if (invalid) closingScorePart1[lastChar]!! else 0
        }
    }

    fun part2(input: List<String>): Long {
        val scores = input.mapNotNull { s: String ->
            val stack = Stack<Char>()
            val invalid = !s.asIterable().all { c ->
                if (c in opening) {
                    stack.add(c)
                } else {
                    c == closing[opening.indexOf(stack.pop())]
                }
            }
            if (invalid) null
            else stack.asReversed()
                .map { c -> closing[opening.indexOf(c)] }
                .fold(0L) { acc, c -> acc * 5 + closingScorePart2[c]!! }
        }.sorted()

        return scores[scores.size / 2]
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day10_test")
    check(part1(testInput1) == 26397)

    val input1 = readInput("Day10")
    println("part 1: ${part1(input1)}") // 464991

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 288957L)

    println("part 2: ${part2(input1)}") // 3662008566
}
