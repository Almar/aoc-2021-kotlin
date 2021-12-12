fun main() {

    data class Cave(val code: String) {
        val isBig = code.uppercase() == code
        val neighbors = mutableSetOf<Cave>()
    }

    fun getCaveSystem(input: List<String>): Map<String, Cave> {
        val caveSystem = mutableMapOf<String, Cave>()
        input.map { s -> s.split("-") }.forEach { caveCodes ->
            val caveA = caveSystem[caveCodes[0]] ?: Cave(caveCodes[0])
            val caveB = caveSystem[caveCodes[1]] ?: Cave(caveCodes[1])
            caveA.neighbors.add(caveB)
            caveB.neighbors.add(caveA)
            caveSystem[caveA.code] = caveA
            caveSystem[caveB.code] = caveB
        }
        return caveSystem
    }

    fun findRoutes(cave: Cave, routeSoFar: MutableList<Cave>): Int {
        var numberOfRoutes = 0

        cave.neighbors
            .filter { neighbor -> neighbor.isBig || !routeSoFar.contains(neighbor) }
            .forEach { neighbor ->
                val routeThisBranch = routeSoFar.toMutableList()
                routeThisBranch.add(neighbor)
                if (neighbor.code == "end") {
                    // println(routeSoFar.map { it.code }.joinToString(",") + ",end")
                    numberOfRoutes += 1
                } else {
                    numberOfRoutes += findRoutes(neighbor, routeThisBranch.toMutableList())
                }
            }

        return numberOfRoutes
    }

    fun part1(input: List<String>): Int {
        val caveSystem = getCaveSystem(input)
        val start = caveSystem["start"]!!
        val numberOfRoutes = findRoutes(start, mutableListOf(start))

        // println("Routes found: $numberOfRoutes")

        return numberOfRoutes // 3576
    }

    fun findRoutes2(cave: Cave, routeSoFar: MutableList<Cave>): Int {
        var numberOfRoutes = 0
        val maxPasses = routeSoFar.filter { !it.isBig }.groupBy { it.code }.map { it.value.size }.maxOrNull() ?: 0

        cave.neighbors
            .filter { neighbor -> neighbor.code != "start" && (maxPasses < 2 || neighbor.isBig || !routeSoFar.contains(neighbor)) }
            .forEach { neighbor ->
                val routeThisBranch = routeSoFar.toMutableList()
                routeThisBranch.add(neighbor)
                if (neighbor.code == "end") {
                    // println(routeSoFar.map { it.code }.joinToString(",") + ",end")
                    numberOfRoutes += 1
                } else {
                    numberOfRoutes += findRoutes2(neighbor, routeThisBranch.toMutableList())
                }
            }

        return numberOfRoutes
    }

    fun part2(input: List<String>): Int {
        val caveSystem = getCaveSystem(input)
        val start = caveSystem["start"]!!
        val numberOfRoutes = findRoutes2(start, mutableListOf(start))

        // println("Routes found: $numberOfRoutes")

        return numberOfRoutes
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day12_test")
    val testInput2 = readInput("Day12_test2")
    val testInput3 = readInput("Day12_test3")
    check(part1(testInput1) == 10)
    check(part1(testInput2) == 19)
    check(part1(testInput3) == 226)

    val input1 = readInput("Day12")
    println("part 1: ${part1(input1)}") // 3576

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 36)
    check(part2(testInput2) == 103)
    check(part2(testInput3) == 3509)

    println("part 2: ${part2(input1)}") // 84271
}
