import java.lang.Integer.max
import java.lang.Math.min

fun main() {
    data class Cuboid(val rangeX: IntRange, val rangeY: IntRange, val rangeZ: IntRange, val on: Boolean = false) {

        fun getOverlapOrNull(other: Cuboid): Cuboid? {
            val minX = other.rangeX.first.coerceAtLeast(rangeX.first)
            val maxX = other.rangeX.last.coerceAtMost(rangeX.last)
            val minY = other.rangeY.first.coerceAtLeast(rangeY.first)
            val maxY = other.rangeY.last.coerceAtMost(rangeY.last)
            val minZ = other.rangeZ.first.coerceAtLeast(rangeZ.first)
            val maxZ = other.rangeZ.last.coerceAtMost(rangeZ.last)

            return if (minX <= maxX && minY <= maxY && minZ <= maxZ)
                Cuboid(minX..maxX, minY..maxY, minZ..maxZ)
            else null
        }

        fun getNumberOfElementsNotOverlapped(others: List<Cuboid>): Long {
            var totalSubstract = 0L

            // find all parts of the cuboid that will be overwritten by others
            val overlapping = others.mapNotNull { getOverlapOrNull(it) }

            // determine how many elements should be subtracted from the cuboid, making sure we count each overlapping
            // element only once (we need to check whether the overlapping cuboids also overlap each other)
            loop@ for (i in overlapping.indices) {
                val othersOverlapping =
                    if (i < overlapping.lastIndex) overlapping.subList(i + 1, overlapping.size) else listOf()
                totalSubstract += overlapping[i].getNumberOfElementsNotOverlapped(othersOverlapping)
            }

            val totalElements = rangeX.count().toLong() * rangeY.count() * rangeZ.count()
            return totalElements - totalSubstract
        }
    }

    class Matrix(val rangeX: IntRange, val rangeY: IntRange, val rangeZ: IntRange, initVal: Byte = 0) {
        val elements =
            List(rangeX.count()) { _ -> List(rangeY.count()) { _ -> MutableList<Byte>(rangeZ.count()) { _ -> initVal } } }

        fun set(cuboid: Cuboid) {
            for (x in max(rangeX.first, cuboid.rangeX.first)..min(rangeX.last, cuboid.rangeX.last)) {
                for (y in max(rangeY.first, cuboid.rangeY.first)..min(rangeY.last, cuboid.rangeY.last)) {
                    for (z in max(rangeZ.first, cuboid.rangeZ.first)..min(rangeZ.last, cuboid.rangeZ.last)) {
                        elements[x - rangeX.first][y - rangeY.first][z - rangeZ.first] = if (cuboid.on) 1 else 0
                    }
                }
            }
        }

        fun numberOfElementsOn() = elements.sumOf { y -> y.sumOf { z -> z.sum().toLong() } }
    }

    fun getPuzzleData(input: List<String>): List<Cuboid> {
        val regex = """(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""".toRegex()
        return input.map { line ->
            val captureGroups = regex.find(line)?.destructured?.toList() ?: error("Did not parse!")
            val on = captureGroups[0] == "on"
            val x = captureGroups.subList(1, 3).map { it.toInt() }
                .let { x -> if (x[0] < x[1]) IntRange(x[0], x[1]) else IntRange(x[1], x[0]) }
            val y = captureGroups.subList(3, 5).map { it.toInt() }
                .let { y -> if (y[0] < y[1]) IntRange(y[0], y[1]) else IntRange(y[1], y[0]) }
            val z = captureGroups.subList(5, 7).map { it.toInt() }
                .let { z -> if (z[0] < z[1]) IntRange(z[0], z[1]) else IntRange(z[1], z[0]) }
            Cuboid(x, y, z, on)
        }
    }

    fun part1(input: List<String>): Long {
        val cuboids = getPuzzleData(input)
        val matrix = Matrix(-50..50, -50..50, -50..50)
        cuboids.forEach { cuboid -> matrix.set(cuboid) }
        return matrix.numberOfElementsOn()
    }

    fun part2(input: List<String>): Long {
        val cuboids = getPuzzleData(input)
        var totalOn = 0L

        // Due to the large size of the cuboids we cannot use the same (simple) strategy as used in part1 - the
        // resulting matrix would not fit into memory but even if it could, it would take a lot of time to process
        // everything. We need a smarter solution:
        // For each cuboid determine how many elements will be overwritten bij a following cuboid (reboot step)
        // Only count the elements that will not be overwritten. Even if overwritten by an "on" reboot step - those
        // "on" elements will be counted when that cuboid is processed.

        loop@ for (i in cuboids.indices) {
            if (!cuboids[i].on) continue@loop
            val others = if (i < cuboids.lastIndex) cuboids.subList(i + 1, cuboids.size) else listOf()
            val notOverlapped = cuboids[i].getNumberOfElementsNotOverlapped(others)
            totalOn += notOverlapped
        }

        return totalOn
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day22_test")
    check(part1(testInput1) == 590784L)

    val input1 = readInput("Day22")
    println("part 1: ${part1(input1)}")

    // test if implementation meets criteria from the description, like:
    val testInput2 = readInput("Day22_test2")
    check(part2(testInput2) == 2758514936282235L)

    println("part 2: ${part2(input1)}") //1211172281877240
}
