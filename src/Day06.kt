fun main() {
    fun part1(input: List<Int>): Int {
        val fish = input.toMutableList()
        for (day in 1..80) {
            var newFish = 0
            fish.forEachIndexed() { index, f ->
                if (f == 0) {
                    newFish++
                    fish[index] = 6
                } else {
                    fish[index] = f - 1
                }
            }
            fish.addAll(MutableList(newFish) { _ -> 8 })
        }
        return fish.size
    }

    fun part2(input: List<Int>): Long {
        val fishCycles = MutableList(9) { _ -> 0L }
        input.forEach { i -> fishCycles[i] += 1L }

        for (day in 1..256) {
            val atZero = fishCycles[0]
            for (i in 0..7) {
                fishCycles[i] = fishCycles[i + 1]
            }
            fishCycles[6] += atZero
            fishCycles[8] = atZero
        }

        return fishCycles.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day06_test")[0].split(",").map { it.toInt() }
    check(part1(testInput1) == 5934)

    val input1 = readInput("Day06")[0].split(",").map { it.toInt() }
    println("part 1: ${part1(input1)}") // 362740

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 26984457539)

    println("part 2: ${part2(input1)}") // 1644874076764
}
