import java.util.LinkedList

fun main() {
    fun getNeighbors(x: Int, y: Int, maxX: Int, maxY: Int): List<Pair<Int, Int>> {
        val neighbors = mutableListOf<Pair<Int, Int>>()
        if (x > 0) {
            neighbors.add(Pair(x - 1, y))
        }
        if (y > 0) {
            neighbors.add(Pair(x, y - 1))
        }
        if (x < maxX) {
            neighbors.add(Pair(x + 1, y))
        }
        if (y < maxY) {
            neighbors.add(Pair(x, y + 1))
        }
        return neighbors
    }

    fun getNeighbors(x: Int, y: Int, matrix: List<List<Int>>): List<Pair<Int, Int>> {
        return getNeighbors(x, y, matrix[0].lastIndex, matrix.lastIndex)
    }

    fun getNeighbors(x: Int, y: Int, matrix: List<List<Pair<Int, Int?>>>): List<Pair<Int, Int>> {
        return getNeighbors(x, y, matrix[0].lastIndex, matrix.lastIndex)
    }

    fun hasAllHigherNeighbors(x: Int, y: Int, matrix: List<List<Int>>): Boolean {
        val candidate = matrix[y][x]
        return getNeighbors(x, y, matrix).all { (x, y) -> matrix[y][x] > candidate }
    }

    fun addValidNeighborsToQueue(x: Int, y: Int, basinNr: Int, matrix: List<List<Pair<Int, Int?>>>, queue: LinkedList<Triple<Int, Int, Int>>) {
        getNeighbors(x, y, matrix)
            .filter { (x, y) -> matrix[y][x].first < 9 }
            .map { (x, y) -> Triple(x, y, matrix[y][x].second) }
            .filter { (_, _, xyBasinNr) -> xyBasinNr == null }
            .forEach { (x, y, _) -> queue.addFirst(Triple(x, y, basinNr)) }
    }

    fun part1(input: List<List<Int>>): Int {
        var sumOfRiskLevel = 0
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (hasAllHigherNeighbors(x, y, input)) {
                    sumOfRiskLevel += input[y][x] + 1
                }
            }
        }
        return sumOfRiskLevel
    }

    fun part2(input: List<List<Int>>): Int {
        val matrix = input.map { it.map { x -> Pair<Int, Int?>(x, null) }.toMutableList() }
        val queue = LinkedList<Triple<Int, Int, Int>>()
        var nrOfBasins = 0

        for (y in input.indices) {
            for (x in input[y].indices) {
                if (hasAllHigherNeighbors(x, y, input)) {
                    val basinNr = nrOfBasins++
                    matrix[y][x] = matrix[y][x].copy(second = basinNr)
                    addValidNeighborsToQueue(x, y, basinNr, matrix, queue)
                }
            }
        }

        while (queue.size > 0) {
            val (x, y, basinNr) = queue.pop()
            matrix[y][x] = matrix[y][x].copy(second = basinNr)
            addValidNeighborsToQueue(x, y, basinNr, matrix, queue)
        }

        return matrix.reduce { acc, pairs -> (acc + pairs).toMutableList() }
            .filter { it.second != null }
            .groupBy { it.second }
            .map { it.value.size }
            .sortedDescending()
            .subList(0, 3)
            .reduce { acc, i -> acc * i }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day09_test").map { it.map { it.digitToInt() } }
    check(part1(testInput1) == 15)

    val input1 = readInput("Day09").map { it.map { it.digitToInt() } }
    println("part 1: ${part1(input1)}") // 480

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 1134)

    println("part 2: ${part2(input1)}") // 1045660
}
