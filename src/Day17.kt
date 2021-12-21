import kotlin.math.abs

class TargetArea(val rangeX: IntRange, val rangeY: IntRange) {

    fun contains(point: Point): Boolean {
        return rangeX.contains(point.x) && rangeY.contains(point.y)
    }

    fun relativePosition(point: Point, velocityX: Int, velocityY: Int): Int {
        if (contains(point)) {
            return 0
        } else if (velocityY >= 0) {
            return -1
        } else {
            val overShootX = (point.x < rangeX.first && velocityX <= 0) || (point.x > rangeX.last && velocityX >= 0)
            val overShootY = (point.y < rangeY.first && velocityY <= 0) || (point.y > rangeY.last && velocityY >= 0)
            return if (overShootX || overShootY) 1 else -1
        }
    }

    fun copy(rangeX: IntRange = this.rangeX, rangeY: IntRange = this.rangeY): TargetArea {
        return TargetArea(rangeX, rangeY)
    }

    companion object {
        fun fromPuzzleInput(input: String): TargetArea {
            val regex = """target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""".toRegex()
            val (x1, x2, y1, y2) = regex.find(input)?.destructured?.toList()?.map { it.toInt() }
                ?: error("Did not parse!")
            val xRange = if (x1 <= x2) IntRange(x1, x2) else IntRange(x2, x1)
            val yRange = if (y1 <= y2) IntRange(y1, y2) else IntRange(y2, y1)
            return TargetArea(xRange, yRange)
        }
    }
}

fun main() {
    fun getTrajectory(initVelocityX: Int, initVelocityY: Int, targetArea: TargetArea): List<Point> {
        var velocityX = initVelocityX
        var velocityY = initVelocityY
        return buildList {
            add(Point(0, 0))
            while (targetArea.relativePosition(this[this.lastIndex], velocityX, velocityY) < 0) {
                val previousPoint = this[this.lastIndex]
                add(Point(previousPoint.x + velocityX, previousPoint.y + velocityY))
                velocityX = if (velocityX > 0) velocityX - 1 else if (velocityX < 0) velocityX + 1 else 0
                velocityY -= 1
            }
        }
    }

    fun part1(input: String): Int {
        val targetArea = TargetArea.fromPuzzleInput(input).copy(rangeX = 0..0)
        val initVelocityY = abs(targetArea.rangeY.first) - 1
        return getTrajectory(0, initVelocityY, targetArea).maxOf { it.y }
    }

    fun part2(input: String): Int {
        val targetArea = TargetArea.fromPuzzleInput(input)
        val rangeX = 1..targetArea.rangeX.last
        val rangeY = targetArea.rangeY.first until abs(targetArea.rangeY.first)

        val solutions = buildList {
            for (velocityX in rangeX) {
                for (velocityY in rangeY) {
                    if (targetArea.contains(getTrajectory(velocityX, velocityY, targetArea).last())) {
                        add(Pair(velocityX, velocityY))
                    }
                }
            }
        }

        return solutions.size
    }

    /** Tests
    val testTargetArea = TargetArea.fromPuzzleInput("target area: x=20..30, y=-10..-5")

    var trajectory = getTrajectory(7, 2, testTargetArea)
    println("Should hit target (7,2): ${trajectory} --> ${if (testTargetArea.contains(trajectory.last())) "hit" else "miss"}")

    trajectory = getTrajectory(6, 3, testTargetArea)
    println("Should hit target (6,3): ${trajectory} --> ${if (testTargetArea.contains(trajectory.last())) "hit" else "miss"}")

    trajectory = getTrajectory(9, 0, testTargetArea)
    println("Should hit target (9,0): ${trajectory} --> ${if (testTargetArea.contains(trajectory.last())) "hit" else "miss"}")

    trajectory = getTrajectory(17, -4, testTargetArea)
    println("Should miss target (17,-4): ${trajectory} --> ${if (testTargetArea.contains(trajectory.last())) "hit" else "miss"}")

    trajectory = getTrajectory(6, 9, testTargetArea)
    println("Should hit target (6,9): ${trajectory} --> ${if (testTargetArea.contains(trajectory.last())) "hit" else "miss"}")
    */

    check(part1("target area: x=20..30, y=-10..-5") == 45)
    println("part 1: ${part1("target area: x=195..238, y=-93..-67")}") // 4278

    check(part2("target area: x=20..30, y=-10..-5") == 112)
    println("part 2: ${part2("target area: x=195..238, y=-93..-67")}")
}
