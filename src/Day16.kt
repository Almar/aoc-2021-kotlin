interface BitsPackage {
    val version: Int
    val type: Int
    val bits: String

    fun getPackages(): List<BitsPackage>
    fun getValue(): Long
}

data class LiteralPackage(
    override val version: Int,
    override val type: Int,
    override val bits: String,
    private val value: Long
) : BitsPackage {
    override fun getPackages(): List<BitsPackage> {
        return listOf(this)
    }

    override fun getValue(): Long {
        return value
    }
}

data class OperatorPackage(
    override val version: Int,
    override val type: Int,
    override val bits: String,
    val subPackages: List<BitsPackage>
) : BitsPackage {
    override fun getPackages(): List<BitsPackage> {
        return listOf(this) + subPackages.flatMap { it.getPackages() }
    }

    override fun getValue(): Long {
        // Packets with type ID 0 are sum packets - their value is the sum of the values of their sub-packets. If they only have a single sub-packet, their value is the value of the sub-packet.
        // Packets with type ID 1 are product packets - their value is the result of multiplying together the values of their sub-packets. If they only have a single sub-packet, their value is the value of the sub-packet.
        // Packets with type ID 2 are minimum packets - their value is the minimum of the values of their sub-packets.
        // Packets with type ID 3 are maximum packets - their value is the maximum of the values of their sub-packets.
        // Packets with type ID 5 are greater than packets - their value is 1 if the value of the first sub-packet is greater than the value of the second sub-packet; otherwise, their value is 0. These packets always have exactly two sub-packets.
        // Packets with type ID 6 are less than packets - their value is 1 if the value of the first sub-packet is less than the value of the second sub-packet; otherwise, their value is 0. These packets always have exactly two sub-packets.
        // Packets with type ID 7 are equal to packets - their value is 1 if the value of the first sub-packet is equal to the value of the second sub-packet; otherwise, their value is 0. These packets always have exactly two sub-packets.
        return when (type) {
            0 -> subPackages.sumOf { it.getValue() }
            1 -> subPackages.map { it.getValue() }.reduce { acc, value -> acc * value }
            2 -> subPackages.minOf { it.getValue() }
            3 -> subPackages.maxOf { it.getValue() }
            5 -> if (subPackages[0].getValue() > subPackages[1].getValue()) 1 else 0
            6 -> if (subPackages[0].getValue() < subPackages[1].getValue()) 1 else 0
            7 -> if (subPackages[0].getValue() == subPackages[1].getValue()) 1 else 0
            else -> error("Unknown type $type")
        }
    }
}

class BitReader(val bits: String) {
    var index = 0
    var subIndex = 0

    fun next(n: Int): String {
        val start = index
        index += n
        return bits.substring(start, index)
    }

    fun peek(n: Int): String {
        val start = index
        return bits.substring(start, index + n)
    }

    fun hasMore(): Boolean {
        return bitsRemaining().contains('1')
    }

    fun bitsRead(): String {
        return bits.substring(subIndex, index)
    }

    fun bitsRemaining(): String {
        return bits.substring(index)
    }

    fun resetBitsRead() {
        subIndex = index
    }

    companion object {
        fun fromHexString(hexString: String): BitReader {
            val bits = hexString.asSequence().map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")
            return BitReader(bits)
        }
    }
}

fun main() {

    fun getVersionAndType(bits: String): Pair<Int, Int> {
        val version = bits.substring(0..2).toInt(2)
        val type = bits.substring(3..5).toInt(2)
        return Pair(version, type)
    }

    fun parseLiteralPackage(reader: BitReader): LiteralPackage {
        reader.resetBitsRead()
        val (version, type) = getVersionAndType(reader.next(6))
        var last = false
        var valueBits = ""
        while (!last) {
            val part = reader.next(5)
            last = part[0] == '0'
            valueBits += part.substring(1)
        }

        return LiteralPackage(version, type, reader.bitsRead(), valueBits.toLong(2))
    }

    fun parseOperatorPackage(reader: BitReader): OperatorPackage {
        reader.resetBitsRead()
        val (version, type) = getVersionAndType(reader.next(6))
        val lengthTypeId = reader.next(1)
        val subPackages = mutableListOf<BitsPackage>()

        if (lengthTypeId == "0") {
            val lengthInBits = reader.next(15).toInt(2)
            val subPackageReader = BitReader(reader.next(lengthInBits))
            while (subPackageReader.hasMore()) {
                val (_, t) = getVersionAndType(subPackageReader.peek(6))
                if (t == 4) {
                    subPackages.add(parseLiteralPackage(subPackageReader))
                } else {
                    subPackages.add(parseOperatorPackage(subPackageReader))
                }
            }
        } else {
            val lengthInPackages = reader.next(11).toInt(2)
            for (i in 1..lengthInPackages) {
                val (_, t) = getVersionAndType(reader.peek(6))
                if (t == 4) {
                    subPackages.add(parseLiteralPackage(reader))
                } else {
                    subPackages.add(parseOperatorPackage(reader))
                }
            }
        }

        return OperatorPackage(version, type, reader.bitsRead(), subPackages)
    }

    fun parsePackage(bits: String): BitsPackage {
        val reader = BitReader.fromHexString(bits)
        val (_, type) = getVersionAndType(reader.peek(6))
        return if (type == 4) {
            parseLiteralPackage(reader)
        } else {
            parseOperatorPackage(reader)
        }
    }

    fun part1(input: String): Int {
        return parsePackage(input).getPackages().sumOf { it.version }
    }

    fun part2(input: String): Long {
        return parsePackage(input).getValue()
    }

    // Test Literal
    check(parseLiteralPackage(BitReader.fromHexString("D2FE28")).getValue() == 2021L)

    // Test Operator
    val testOperator = parseOperatorPackage(BitReader.fromHexString("38006F45291200"))
    check(testOperator.subPackages[0].type == 4 && (testOperator.subPackages[0] as LiteralPackage).getValue() == 10L)
    check(testOperator.subPackages[1].type == 4 && (testOperator.subPackages[1] as LiteralPackage).getValue() == 20L)

    check(parsePackage("8A004A801A8002F478").getPackages().sumOf { it.version } == 16)
    check(parsePackage("620080001611562C8802118E34").getPackages().sumOf { it.version } == 12)
    check(parsePackage("C0015000016115A2E0802F182340").getPackages().sumOf { it.version } == 23)
    check(parsePackage("A0016C880162017C3686B18A3D4780").getPackages().sumOf { it.version } == 31)

    val input1 = readInput("Day16")
    println("part 1: ${part1(input1[0])}")

    // part 2 examples:
    // C200B40A82 finds the sum of 1 and 2, resulting in the value 3.
    check(parsePackage("C200B40A82").getValue() == 3L)

    // 04005AC33890 finds the product of 6 and 9, resulting in the value 54.
    check(parsePackage("04005AC33890").getValue() == 54L)

    // 880086C3E88112 finds the minimum of 7, 8, and 9, resulting in the value 7.
    check(parsePackage("880086C3E88112").getValue() == 7L)

    // CE00C43D881120 finds the maximum of 7, 8, and 9, resulting in the value 9.
    check(parsePackage("CE00C43D881120").getValue() == 9L)

    // D8005AC2A8F0 produces 1, because 5 is less than 15.
    check(parsePackage("D8005AC2A8F0").getValue() == 1L)

    // F600BC2D8F produces 0, because 5 is not greater than 15.
    check(parsePackage("F600BC2D8F").getValue() == 0L)

    // 9C005AC2F8F0 produces 0, because 5 is not equal to 15.
    check(parsePackage("9C005AC2F8F0").getValue() == 0L)

    // 9C0141080250320F1802104A08 produces 1, because 1 + 3 = 2 * 2.
    check(parsePackage("9C0141080250320F1802104A08").getValue() == 1L)

    println("part 2: ${part2(input1[0])}")
}
