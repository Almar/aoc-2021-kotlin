data class Point(val x: Int, val y: Int)
data class Line(val point1: Point, val point2: Point)

fun main() {

    fun getLines(input: List<String>): List<Line> {
        return input.map { lineStr ->
            lineStr
                .split(" -> ", ",")
                .let { points ->
                    Line(
                        Point(points[0].toInt(), points[1].toInt()),
                        Point(points[2].toInt(), points[3].toInt())
                    )
                }
        }
    }

    fun drawLineInMatrix(line: Line, matrix: List<MutableList<Int>>) {
        val rangeX = if (line.point1.x <= line.point2.x) line.point1.x..line.point2.x else line.point1.x downTo line.point2.x
        val rangeY = if (line.point1.y <= line.point2.y) line.point1.y..line.point2.y else line.point1.y downTo line.point2.y

        if (line.point1.x == line.point2.x) {
            for (y in rangeY) {
                matrix[y][line.point1.x] += 1
            }
        } else {
            val deltaY = if (line.point1.y == line.point2.y) 0 else if (line.point1.y < line.point2.y) 1 else -1
            var y = line.point1.y
            for (x in rangeX) {
                matrix[y][x] += 1
                y += deltaY
            }
        }
    }

    fun getMatrix(lines: List<Line>): List<List<Int>> {
        val maxX = lines.fold(0) { acc: Int, line -> line.point1.x.coerceAtLeast(line.point2.x).coerceAtLeast(acc) }
        val maxY = lines.fold(0) { acc: Int, line -> line.point1.y.coerceAtLeast(line.point2.y).coerceAtLeast(acc) }
        val matrix = List(maxY + 1) { _ -> MutableList(maxX + 1) { _ -> 0 } }

        lines.forEach { line -> drawLineInMatrix(line, matrix) }

        return matrix
    }

    fun part1(input: List<String>): Int {
        val lines = getLines(input)
        val linesFiltered = lines.filter { line -> line.point1.x == line.point2.x || line.point1.y == line.point2.y }
        val matrix = getMatrix(linesFiltered)
        return matrix.sumOf { row -> row.fold(0) { acc: Int, cell -> if (cell > 1) acc + 1 else acc } }
    }

    fun part2(input: List<String>): Int {
        val lines = getLines(input)
        val matrix = getMatrix(lines)
        return matrix.sumOf { row -> row.fold(0) { acc: Int, cell -> if (cell > 1) acc + 1 else acc } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day05_test")
    check(part1(testInput1) == 5)

    val input1 = readInput("Day05")
    println("part 1: ${part1(input1)}")

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 12)

    println("part 2: ${part2(input1)}")
}
