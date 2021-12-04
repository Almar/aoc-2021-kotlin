typealias Board = List<MutableList<Pair<Int, Boolean>>>

fun main() {

    fun processInput(input: List<String>): Pair<List<Int>, List<Board>> {
        val numberDraws = input[0].split(",").map(String::toInt)
        val boards = mutableListOf<Board>()

        for (i in 1..input.lastIndex step 6) {
            val boardInput = input.subList(i + 1, i + 6)
            val board: Board = boardInput.map { line ->
                line
                    .split(" ")
                    .filter(String::isNotBlank)
                    .map { Pair(it.toInt(), false) }.toMutableList()
            }
            boards.add(board)
        }

        return Pair(numberDraws, boards)
    }

    fun isWinningBoard(board: Board, drawnNumber: Int): Boolean {
        var positionOnBoard: Pair<Int, Int>? = null

        loop@ for (y in 0..4) {
            for (x in 0..4) {
                if (board[y][x].first == drawnNumber) {
                    board[y][x] = board[y][x].copy(second = true)
                    positionOnBoard = Pair(x, y)
                    break@loop
                }
            }
        }

        if (positionOnBoard != null) {
            val horizontal = board[positionOnBoard.second]
            val vertical = board.map { horz -> horz[positionOnBoard.first] }
            return horizontal.all { it.second } || vertical.all { it.second }
        }

        return false
    }

    fun getBoardScore(board: Board, lastNumberDrawn: Int): Int {
        val sumNumbersNotDrawn = board.fold(0) { acc, horizontal ->
            horizontal.fold(acc) { acc2, vertical -> if (vertical.second) acc2 else acc2 + vertical.first } }

        println("score: $sumNumbersNotDrawn * $lastNumberDrawn = ${sumNumbersNotDrawn * lastNumberDrawn}")
        return sumNumbersNotDrawn * lastNumberDrawn
    }

    fun part1(input: List<String>): Int {
        val (numberDraws, boards) = processInput(input)

        for (i in numberDraws.indices) {
            val drawnNumber = numberDraws[i]
            for (j in boards.indices) {
                if (isWinningBoard(boards[j], drawnNumber)) {
                    // println("winning board")
                    // println(boards[j])
                    // println("Numbers drawn: ${numberDraws.subList(0, i+1)}")
                    return getBoardScore(boards[j], drawnNumber)
                }
            }
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val (numberDraws, boardsInput) = processInput(input)
        var boards = boardsInput

        for (i in numberDraws.indices) {
            val drawnNumber = numberDraws[i]

            if (boards.size > 1) {
                boards = boards.filter { board -> !isWinningBoard(board, drawnNumber) }
            } else {
                if (isWinningBoard(boards[0], drawnNumber)) {
                    // println("winning board")
                    // println(boards[0])
                    // println("Numbers drawn: ${numberDraws.subList(0, i+1)}")
                    return getBoardScore(boards[0], drawnNumber)
                }
            }
        }

        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day04_test")
    check(part1(testInput1) == 4512)

    val input1 = readInput("Day04")
    println("part 1: ${part1(input1)}")

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 1924)

    println("part 2: ${part2(input1)}")
}
