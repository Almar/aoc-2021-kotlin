data class Cave(val x: Int, val y: Int, val riskLevel: Int, var minRiskLevelFromStart: Int? = null)
typealias CaveSystem = List<List<Cave>>

fun main() {

    fun getCaveSystem(input: List<String>): CaveSystem {
        return input.mapIndexed { y, s -> s.mapIndexed { x, c -> Cave(x, y, c.digitToInt()) } }
    }

    fun getNextCaveSystemTile(tile: CaveSystem, x5: Int, y5: Int): CaveSystem {
        return tile.mapIndexed { indexY, row ->
            row.mapIndexed { indexX, cave ->
                val newX = indexX + (x5 * tile[0].size)
                val newY = indexY + (y5 * tile.size)
                val newRiskLevel = if (cave.riskLevel == 9) 1 else cave.riskLevel + 1
                Cave(newX, newY, newRiskLevel)
            }
        }
    }

    fun getCaveSystemTimes5(input: List<String>): CaveSystem {
        // find all 25 tiles of the 5X cave system
        val tiles = buildList<CaveSystem> {
            add(getCaveSystem(input))
            for (i in 1..24) {
                val x5 = i % 5
                val y5 = i / 5
                val prevTile = if (x5 == 0) this[i - 5] else this[i - 1]
                add(getNextCaveSystemTile(prevTile, x5, y5))
            }
        }

        // combine the 25 tiles to create the 5X cave system
        val caveSystemTimes5X = buildList<List<Cave>> {
            for (tilesY in 0..4) {
                for (rowInTileY in tiles[0].indices) {
                    // combine rows of the five tiles to one row in the caveSystem5X
                    add(tiles[tilesY*5][rowInTileY] + tiles[tilesY*5+1][rowInTileY] + tiles[tilesY*5+2][rowInTileY] + tiles[tilesY*5+3][rowInTileY] + tiles[tilesY*5+4][rowInTileY])
                }
            }
        }

        // val formatter = DecimalFormat("00")
        // println("new caveSystem5X:")
        // caveSystemTimes5X.forEach { line -> println(line.map { it.riskLevel }.joinToString("")) }
        // caveSystemTimes5X.forEach { line -> println(line.map { "(${formatter.format(it.x)},${formatter.format(it.y)})" }.joinToString("")) }

        return caveSystemTimes5X
    }

    fun backTrack(cave: Cave, minRiskLevelFromStart: Int, path: List<Cave>, caveSystem: CaveSystem) {
        val newMinRiskLevelFromStart = cave.riskLevel + minRiskLevelFromStart
        if (newMinRiskLevelFromStart < cave.minRiskLevelFromStart!!) {
            cave.minRiskLevelFromStart = newMinRiskLevelFromStart

            // Now that we found a 'cheaper' alternative path, we might need to update our neighbors as well
            val newPath = path + cave
            buildList {
                add(if (cave.x > 0) caveSystem[cave.y][cave.x - 1] else null)
                add(if (cave.y > 0) caveSystem[cave.y - 1][cave.x] else null)
                add(if (cave.x < caveSystem[0].lastIndex) caveSystem[cave.y][cave.x + 1] else null)
                add(if (cave.y < caveSystem.lastIndex) caveSystem[cave.y + 1][cave.x] else null)
            }.filterNotNull().forEach { neighborCave ->
                if (neighborCave.minRiskLevelFromStart != null && neighborCave !in path) {
                    backTrack(neighborCave, newMinRiskLevelFromStart, newPath, caveSystem)
                }
            }
        }
    }

    fun findMinRiskLevelPaths(caveSystem: CaveSystem) {
        for (y in caveSystem.indices) {
            for (x in caveSystem[y].indices) {
                val cave = caveSystem[y][x]
                val caveWest = if (x > 0) caveSystem[y][x - 1] else null
                val caveNorth = if (y > 0) caveSystem[y - 1][x] else null

                if (caveWest == null && caveNorth == null) {
                    cave.minRiskLevelFromStart = 0
                } else if (caveNorth == null) {
                    cave.minRiskLevelFromStart = caveWest!!.minRiskLevelFromStart!! + cave.riskLevel
                } else if (caveWest == null) {
                    cave.minRiskLevelFromStart = caveNorth.minRiskLevelFromStart!! + cave.riskLevel
                } else if (caveWest.minRiskLevelFromStart!! < caveNorth.minRiskLevelFromStart!!) {
                    cave.minRiskLevelFromStart = caveWest.minRiskLevelFromStart!! + cave.riskLevel
                    backTrack(caveNorth, cave.minRiskLevelFromStart!!, listOf(cave), caveSystem)
                } else {
                    cave.minRiskLevelFromStart = caveNorth.minRiskLevelFromStart!! + cave.riskLevel
                    backTrack(caveWest, cave.minRiskLevelFromStart!!, listOf(cave), caveSystem)
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val caveSystem = getCaveSystem(input)
        findMinRiskLevelPaths(caveSystem)

        // val formatter = DecimalFormat("00")
        // caveSystem.forEach { line -> println(line.map { formatter.format(it.minRiskLevelFromStart) }.joinToString(",")) }

        return caveSystem.last().last().minRiskLevelFromStart!!
    }

    fun part2(input: List<String>): Int {
        val caveSystem = getCaveSystemTimes5(input)
        findMinRiskLevelPaths(caveSystem)
        return caveSystem.last().last().minRiskLevelFromStart!!
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day15_test")
    check(part1(testInput1) == 40)

    val input1 = readInput("Day15")
    println("part 1: ${part1(input1)}") // 687

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 315)

    println("part 2: ${part2(input1)}") // 2957
}
