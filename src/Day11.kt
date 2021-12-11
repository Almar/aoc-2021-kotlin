fun main() {

    data class Dumbo(val x: Int, val y: Int, var level: Int, var flashed: Boolean = false)

    fun printMatrix(input: List<List<Dumbo>>, label: String) {
        println()
        println(label)
        input.forEach { println(it.map { it.level }.joinToString("")) }
    }

    fun getNeighbors(dumbo: Dumbo, matrix: List<List<Dumbo>>): List<Dumbo> {
        val neighbors = mutableListOf<Dumbo>()
        for (deltaY in -1..1) {
            for (deltaX in -1..1) {
                val neighborY = dumbo.y + deltaY
                val neighborX = dumbo.x + deltaX
                if (neighborY >= 0 && neighborY <= matrix.lastIndex && neighborX >= 0 && neighborX <= matrix[0].lastIndex && !(deltaY == 0 && deltaX == 0)) {
                    neighbors.add(matrix[neighborY][neighborX])
                }
            }
        }
        return neighbors
    }

    fun increaseAll(list: List<Dumbo>) {
        list.forEach { dumbo -> dumbo.level += 1 }
    }

    fun increaseAll(matrix: List<List<Dumbo>>) {
        matrix.flatten().forEach { dumbo -> dumbo.level += 1 }
    }

    fun resetAllLargerThanNine(matrix: List<List<Dumbo>>): Int {
        val flashed = matrix.flatten().filter { it.level > 9 }
        flashed.forEach { dumbo ->
            dumbo.level = 0
            dumbo.flashed = false
        }
        return flashed.size
    }

    fun checkFlash(dumbo: Dumbo, matrix: List<List<Dumbo>>) {
        if (dumbo.level > 9 && !dumbo.flashed) {
            dumbo.flashed = true
            val neighbors = getNeighbors(dumbo, matrix).filter { !it.flashed }
            increaseAll(neighbors)
            neighbors.forEach { dumbo -> checkFlash(dumbo, matrix) }
        }
    }

    fun checkFlash(matrix: List<List<Dumbo>>) {
        matrix.flatten().forEach { checkFlash(it, matrix) }
    }

    fun part1(input: List<String>): Int {
        val matrix = input.mapIndexed { indexY, line -> line.asIterable().toList().mapIndexed { indexX, char -> Dumbo(indexX, indexY, char.digitToInt()) } }
        var numberOfFlashes = 0
        // printMatrix(matrix, "Before any steps")

        for (i in 1..100) {
            increaseAll(matrix)
            checkFlash(matrix)
            numberOfFlashes += resetAllLargerThanNine(matrix)
            // printMatrix(matrix, "After step #$i, (total flashes so far $numberOfFlashes:")
        }
        return numberOfFlashes
    }

    fun part2(input: List<String>): Int {
        val matrix = input.mapIndexed { indexY, line -> line.asIterable().toList().mapIndexed { indexX, char -> Dumbo(indexX, indexY, char.digitToInt()) } }
        val numberOfDumbos = matrix.flatten().size
        var step = 0

        loop@ while (step < 999999) {
            step++
            increaseAll(matrix)
            checkFlash(matrix)

            if (resetAllLargerThanNine(matrix) == numberOfDumbos) {
                printMatrix(matrix, "All flashed!!!! $step:")
                break@loop
            }
        }

        return step
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day11_test")
    check(part1(testInput1) == 1656)

    val input1 = readInput("Day11")
    println("part 1: ${part1(input1)}") // 1700

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 195)

    println("part 2: ${part2(input1)}") // 273
}
