fun main() {

    // - forward X increases the horizontal position by X units.
    // - down X increases the depth by X units.
    // - up X decreases the depth by X units.
    // Determine horizontal and depth position
    fun part1(input: List<String>): Pair<Int, Int> {
        var x = 0
        var y = 0
        val regex: Regex = """(\w+)\s(\d+)""".toRegex()

        input.forEach { line ->
            val (command, units) = regex.find(line)?.destructured ?: error("Did not parse!")
            when (command) {
                "forward" -> x += units.toInt()
                "down" -> y += units.toInt()
                "up" -> y -= units.toInt()
                else -> error("Unknown command $command. Line: '$line'")
            }
        }
        return Pair(x, y)
    }

    // - down X increases your aim by X units.
    // - up X decreases your aim by X units.
    // - forward X does two things:
    //   - It increases your horizontal position by X units.
    //   - It increases your depth by your aim multiplied by X.
    // Determine horizontal position (x), depth (y) and aim
    fun part2(input: List<String>): Triple<Int, Int, Int> {
        var x = 0
        var y = 0
        var aim = 0
        val regex: Regex = """(\w+)\s(\d+)""".toRegex()

        input.forEach { line ->
            val (command, units) = regex.find(line)?.destructured ?: error("Did not parse!")
            when (command) {
                "forward" -> {
                    x += units.toInt()
                    y += aim * units.toInt()
                }
                "down" -> aim += units.toInt()
                "up" -> aim -= units.toInt()
                else -> error("Unknown command $command. Line: '$line'")
            }
        }
        return Triple(x, y, aim)
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day02_test")
    check(part1(testInput1).let { pair -> pair.first * pair.second} == 150)

    val input1 = readInput("Day02")
    println("part 1: ${part1(input1).let { pair -> pair.first * pair.second}}")

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1).let { triple -> triple.first * triple.second} == 900)

    println("part 2: ${part2(input1).let { triple -> triple.first * triple.second}}")
}
