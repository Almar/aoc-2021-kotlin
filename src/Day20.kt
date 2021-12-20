fun main() {

    fun getPuzzleInput(input: List<String>): Pair<String, List<List<String>>> {
        val algo = input[0]
        var image = input
            .subList(2, input.size)
            .map { s -> s.asIterable().toList().map { if (it == '.') "0" else "1" } }
            .toMutableList()
        return Pair(algo, image)
    }

    fun increaseImageDimension(image: List<List<String>>, nrPixels: Int): List<List<String>> {
        val result = image.map { l -> l.toMutableList() }.toMutableList()
        result.forEach { it.addAll(0, List(nrPixels) { _ -> "0"}) }
        result.forEach { it.addAll(List(nrPixels) { _ -> "0"}) }
        val horizontalPadding = MutableList(nrPixels) { _ -> MutableList(result[0].size) { _ -> "0" }}
        return horizontalPadding + result + horizontalPadding
    }

    fun enhanceImage(image: List<List<String>>, algo: String): List<List<String>> {
        val result = image.map { l -> l.toMutableList() }.toMutableList()

        for (y in 1..image.lastIndex - 1) {
            for (x in 1..image[y].lastIndex - 1) {
                val grid = image[y - 1].subList(x - 1, x + 2) + image[y].subList(x - 1, x + 2) + image[y + 1].subList(x - 1, x + 2)
                val pixelValue = grid.joinToString("").toInt(2)
                result[y][x] = if (algo[pixelValue] == '#') "1" else "0"
            }
        }
        return result
    }

    fun enhanceImageMultipleTimes(image: List<List<String>>, algo: String, numberOfTimes: Int): List<List<String>> {
        val padding = 2*numberOfTimes

        var enhancedImage = increaseImageDimension(image, padding)
        for (i in 1..numberOfTimes) {
            enhancedImage = enhanceImage(enhancedImage, algo)
        }

        val start = padding-numberOfTimes
        return enhancedImage.subList(start, enhancedImage.size-(start)).map { l -> l.subList(start, l.size-start) }
    }

    fun part1(input: List<String>): Int {
        val (algo, image) = getPuzzleInput(input)
        val enhanceImage = enhanceImageMultipleTimes(image, algo, 2)
        return enhanceImage.flatten().count { x -> x == "1" }
    }

    fun part2(input: List<String>): Int {
        val (algo, image) = getPuzzleInput(input)
        val enhanceImage = enhanceImageMultipleTimes(image, algo, 50)
        return enhanceImage.flatten().count { x -> x == "1" }
    }

    /**
     * The first char of the 'enhancement algorithm' is a # -> all dark pixels surrounded by dark pixes will become
     * light pixels. This means that after the first transformation the whole 'infinite image' lights up, except for
     * the actual image.
     * To come to the right answer you need to start with a sufficiently large image (the actual image surrounded
     * with dark pixels). After the transformation you need take only the transformation of the original image into
     * account (the transformation of the original image will grow the image by one pixel in all directions)
     */


    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day20_test")
    check(part1(testInput1) == 35)

    val input1 = readInput("Day20")
    println("part 1: ${part1(input1)}") // 5464

    // test if implementation meets criteria from the description, like:
    check(part2(testInput1) == 3351)

    println("part 2: ${part2(input1)}") // 19228
}
