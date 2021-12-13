fun main() {

    data class PuzzleInput(val dots: List<Pair<Int, Int>>, val folds: List<Pair<String, Int>>)

    fun getPuzzleInput(input: List<String>): PuzzleInput {
        val dots = mutableListOf<Pair<Int, Int>>()
        val folds = mutableListOf<Pair<String, Int>>()
        val foldInstructionRegEx = """fold along ([xy])=(\d+)""".toRegex()

        input.forEach { line ->
            if (!line.isBlank()) {
                val matchResult = foldInstructionRegEx.matchEntire(line)
                if (matchResult != null) {
                    folds.add(Pair(matchResult.groups[1]!!.value, matchResult.groups[2]!!.value.toInt()))
                } else {
                    val (x, y) = line.split(",").map { it.toInt() }
                    dots.add(Pair(x,y))
                }
            }
        }
        return PuzzleInput(dots, folds)
    }

    fun getMatrix(puzzleInput: PuzzleInput): List<List<Char>> {
        val maxX = puzzleInput.dots.maxOf { dot -> dot.first }
        val maxY = puzzleInput.dots.maxOf { dot -> dot.second }
        val matrix = MutableList(maxY+1) { MutableList(maxX+1) { ' ' } }
        puzzleInput.dots.forEach { dot -> matrix[dot.second][dot.first] = '#' }
        return matrix
    }

    // Function takes not folding down the middle in account. Overlap, however, is discarded.
    fun foldMatrixHorizontally(location: Int, matrix: List<List<Char>>): List<List<Char>> {
        val topPart = matrix.subList(0, location).map { it.toMutableList() }.reversed()
        val bottomPart = matrix.subList(location + 1, matrix.size)

        for (y in 0..bottomPart.lastIndex.coerceAtMost(topPart.lastIndex)) {
            bottomPart[y].forEachIndexed { x, c ->
                if (c == '#') {
                    topPart[y][x] = '#'
                }
            }
        }

        return topPart.reversed()
    }

    // Function takes not folding down the middle in account. Overlap, however, is discarded.
    fun foldMatrixVertically(location: Int, matrix: List<List<Char>>): List<List<Char>> {
        val leftPart = matrix.map { line -> line.take(location).reversed().toMutableList() }.toMutableList()
        val rightPart = matrix.map { line -> line.takeLast( line.lastIndex - location) }

        for (y in leftPart.indices) {
            for (x in 0..rightPart[0].lastIndex.coerceAtMost(leftPart[0].lastIndex)) {
                if (rightPart[y][x] == '#') {
                    leftPart[y][x] = '#'
                }
            }
            leftPart[y] = leftPart[y].reversed().toMutableList()
        }

        return leftPart
    }

    fun foldMatrix(axis: String, location: Int, matrix: List<List<Char>>): List<List<Char>> {
        return if (axis == "x") foldMatrixVertically(location, matrix) else foldMatrixHorizontally(location, matrix)
    }

    fun part1(input: List<String>): Int {
        val puzzleInput = getPuzzleInput(input)
        var matrix = getMatrix(puzzleInput)

        // println("\nstart:")
        // matrix.forEach {line -> println(line.joinToString("")) }

        matrix = foldMatrix(puzzleInput.folds[0].first, puzzleInput.folds[0].second, matrix)

        // println("\nafter first fold:")
        // matrix.forEach {line -> println(line.joinToString("")) }

        return matrix.flatten().count { c -> c == '#' }
    }

    fun part2(input: List<String>) {
        val puzzleInput = getPuzzleInput(input)
        var matrix = getMatrix(puzzleInput)

        puzzleInput.folds.forEach { fold ->
            matrix = foldMatrix(fold.first, fold.second, matrix)
        }

        println("\nafter ${puzzleInput.folds.size} folds:")
        matrix.forEach {line -> println(line.joinToString("")) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day13_test")
    check(part1(testInput1) == 17)

    val input1 = readInput("Day13")
    println("part 1: ${part1(input1)}") // 850

    part2(input1) // AHGCPGAU
}
