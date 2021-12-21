fun main() {
    class DeterministicDie() {
        private var rollCount = 0
        fun roll() = rollCount++ % 100 + 1
        fun getRollCount() = rollCount
    }

    data class Player(var position: Int, var score: Int = 0)

    data class MultiVerse(val pp1: Int, val pp2: Int, val sp1: Int, val sp2: Int)

    fun part1(startPlayerOne: Int, startPlayerTwo: Int): Int {
        val players = listOf<Player>(Player(startPlayerOne-1), Player(startPlayerTwo-1))
        val die = DeterministicDie()
        var turnCount = 0

        while (players.none { player -> player.score >= 1000 }) {
            val player = players[turnCount++ % players.size]
            for (i in 1..3) {
                player.position = (player.position + die.roll()) % 10
            }
            player.score += player.position + 1
            // println("Player ${players.indexOf(player) + 1}: moves to space ${player.position + 1} for a total score of ${player.score}")
        }

        return players.find { player -> player.score < 1000 }!!.score * die.getRollCount()
    }

    fun part2(startPlayerOne: Int, startPlayerTwo: Int): Long {
        val initialMultiVerse = MultiVerse(startPlayerOne-1, startPlayerTwo-1, 0, 0)
        var multiVerseMap = mapOf(Pair(initialMultiVerse, 1L))
        val wins = mutableListOf(0L, 0L)
        var turn = 0

        // spread of scores after three rolls of three sided die (1x3, 3x4, etc)
        val scores = listOf(Pair(1,3), Pair(3,4), Pair(6,5), Pair(7,6), Pair(6,7), Pair(3,8), Pair(1,9))

        while (!multiVerseMap.isEmpty()) {
            val playerIndex = turn++ % 2
            val newMultiVerseMap = mutableMapOf<MultiVerse, Long>()
            multiVerseMap.entries.forEach { entry ->
                val multiVerse = entry.key
                val universes = entry.value
                for (score in scores) {
                    val newUniverses = universes * score.first
                    if (playerIndex == 0) {
                        val newPosition = (multiVerse.pp1 + score.second) % 10
                        val newScore = multiVerse.sp1 + newPosition + 1
                        if (newScore >= 21) {
                            wins[playerIndex] += newUniverses
                        } else {
                            val newMultiVerse = multiVerse.copy(pp1 = newPosition, sp1 = newScore)
                            val existingValue = newMultiVerseMap[newMultiVerse] ?: 0L
                            newMultiVerseMap.put(newMultiVerse, newUniverses + existingValue)
                        }
                    } else {
                        val newPosition = (multiVerse.pp2 + score.second) % 10
                        val newScore = multiVerse.sp2 + newPosition + 1
                        if (newScore >= 21) {
                            wins[playerIndex] += newUniverses
                        } else {
                            val newMultiVerse = multiVerse.copy(pp2 = newPosition, sp2 = newScore)
                            val existingValue = newMultiVerseMap[newMultiVerse] ?: 0L
                            newMultiVerseMap.put(newMultiVerse, newUniverses + existingValue)
                        }
                    }
                }
            }
            multiVerseMap = newMultiVerseMap
            println("Turn: $turn, Wins: $wins")
        }

        return wins.maxOf { it }
    }

    // test if implementation meets criteria from the description, like:
    check(part1(4,8) == 739785)

    println("part 1: ${part1(9, 4)}") // 998088

    // test if implementation meets criteria from the description, like:
    check(part2(4, 8) == 444356092776315)

    println("part 2: ${part2(9, 4)}") // 306621346123766
}
