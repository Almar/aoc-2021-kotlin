fun main() {

    data class PuzzleInput(val template: String, val rules: Map<String, String>)

    fun getPuzzleInput(input: List<String>): PuzzleInput {
        val template = input[0]
        val rules = input.subList(2, input.size).map { it.split(" -> ") }.associate { Pair(it[0], it[1]) }
        return PuzzleInput(template, rules)
    }

    fun part1(input: List<String>): Int {
        val (template, rules) = getPuzzleInput(input)
        var polymer = template

        repeat(10) {
            var newPolymer = ""
            polymer.windowed(2) {
                newPolymer += it[0] + (rules[it]?: "?")
            }
            polymer = newPolymer + polymer[polymer.lastIndex]
        }
        return polymer.asSequence().toList().groupBy { it }.values.map { it.size }.sorted().let { it[it.lastIndex] - it[0] }
    }

    fun findTotals(pair: CharSequence, rules: Map<String, String>, depth: Int, cache: MutableMap<Pair<CharSequence, Int>, Map<Char, Long>>): Map<Char, Long> {
        if (depth == 40) {
            if (pair[0] == pair[1]) {
                return mapOf(Pair(pair[0], 2))
            } else {
                return pair.asSequence().associateWith { 1 }
            }
        } else {
            val fromCache = cache[Pair(pair, depth)]

            if (fromCache != null) {
                // println("$depth: $pair: $fromCache (from cache)")
                return fromCache
            } else {
                val newElement = rules[pair]!!
                val left = pair[0] + newElement
                val right = newElement + pair[1]

                val leftResult = findTotals(left, rules, depth+1, cache)
                val rightResult = findTotals(right, rules, depth+1, cache)
                val result = (leftResult.asSequence() + rightResult.asSequence()).groupBy { it.key }.mapValues { it.value.sumOf { it.value } }.toMutableMap()

                // the middle element is counted twice, so we correct for that
                result[newElement[0]] = result[newElement[0]]!! - 1L

                cache[Pair(pair, depth)] = result
                // println("$depth: $pair: ${pair[0]}${newElement}${pair[1]}:  $result (left: $leftResult, right: $rightResult")
                return result
            }
        }
    }

    fun part2(input: List<String>): Long {
        val (template, rules) = getPuzzleInput(input)
        val numberOfChars = template.windowed(2) { pair -> findTotals(pair, rules, 0, mutableMapOf()).asSequence().toList() }
            .flatten()
            .groupBy { it.key }
            .mapValues { it.value.sumOf { it.value } }
            .toMutableMap()

        // the middle element is counted twice, so we correct for that
        template.asSequence().toList().subList(1, template.length-1).forEach{c -> numberOfChars[c] = numberOfChars[c]!! - 1L}

        return numberOfChars.values.sorted().let { it[it.lastIndex] - it[0] }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day14_test")
    check(part1(testInput1) == 1588)

    val input1 = readInput("Day14")
    println("part 1: ${part1(input1)}")

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 2188189693529)

    println("part 2: ${part2(input1)}")
}
