fun main() {

    fun part1(input: List<String>): Int {
        return input.map { line ->
            line
                .split(" | ")[1]
                .split(" ")
                .count { s -> s.length in listOf(2, 3, 4, 7) }
        }.sum()
    }

    fun getSortedSequence(sequence: String): String {
        return sequence.asIterable().sorted().joinToString("")
    }

    fun part2(input: List<String>): Int {
        //  aaaa
        // b    c
        // b    c
        //  dddd
        // e    f
        // e    f
        //  gggg

        val result = input.map { line ->
            val (digitsInput, digitsOfNumber) = line.split(" | ").map { s -> s.split(' ') }
            val digits = digitsInput.map { d -> getSortedSequence(d) }
            val length5 = digits.filter { s -> s.length == 5 } // digits 2, 3 and 5
            val length6 = digits.filter { s -> s.length == 6 } // digits 0, 6 and 9

            val str1 = digits.first { s -> s.length == 2 }
            val str4 = digits.first { s -> s.length == 4 }
            val str7 = digits.first { s -> s.length == 3 }
            val str8 = digits.first { s -> s.length == 7 }

            val digitMap = mutableMapOf(
                Pair(str1, "1"),
                Pair(str4, "4"),
                Pair(str7, "7"),
                Pair(str8, "8"),
            )

            // 6: digit with 6 segments that does not have all segments of 7 (0 and 9 do)
            val str6 = length6.first { s -> !(s.asIterable().toList().containsAll(str7.asIterable().toList())) }
            digitMap[str6] = "6"
            val segmentF = str1.asIterable().first { char -> str6.asIterable().contains(char) }
            val segmentC = str1.asIterable().first { c -> c != segmentF }

            // 5: digit with 5 segments that does not have segment 'c' (2 and 3 do)
            val str5 = length5.first { s -> !s.contains(segmentC) }
            digitMap[str5] = "5"

            val s = ""
            s.length

            // 9: digit with 6 segments that has all segments of 5 (0 does not).
            val str9 = length6.filter { s -> s != str6 }.first { s -> s.asIterable().toList().containsAll(str5.asIterable().toList()) }
            digitMap[str9] = "9"

            // 0: digit with 6 segments that is not 6 or 9
            val str0 = length6.first { s -> s != str6 && s != str9 }
            digitMap[str0] = "0"

            // 2: digit with 5 segments that does not have segment 'f' (3 does)
            val str2 = length5.first { s -> !s.contains(segmentF) }
            digitMap[str2] = "2"

            // 3: digit with 5 segments that is not 2 or 5
            val str3 = length5.first { s -> s != str2 && s != str5 }
            digitMap[str3] = "3"

            digitsOfNumber.map { d -> digitMap[getSortedSequence(d)] }.joinToString("").toInt()
        }.sum()

        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day08_test")
    println("part 1 test: ${part1(testInput1)}")
    check(part1(testInput1) == 26)

    val input1 = readInput("Day08")
    println("part 1: ${part1(input1)}")

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 61229)

    println("part 2: ${part2(input1)}") // 933305
}
