package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.isEven
import codes.jakob.aoc.shared.parseInt
import codes.jakob.aoc.shared.splitByCharacter
import codes.jakob.aoc.shared.swap

object Day09 : Solution() {
    override fun solvePart1(input: String): Any {
        val blocks: MutableList<String> = parseBlocks(input).flatten().toMutableList()

        var start = 0
        var end = blocks.count() - 1
        while (start < end) {
            if (blocks[start] == ".") {
                blocks.swap(start, end)
                while (blocks[end] == ".") end--
            }
            start++
        }

        return blocks.calculateChecksum()
    }

    override fun solvePart2(input: String): Any {
        val blocks: MutableList<String> = parseBlocks(input).flatten().toMutableList()

        var start = 0
        var end = blocks.count() - 1
        while (start < end) {
            val startBegin = start
            val endBegin = end
            
            var startSpace = 0
            while (blocks[start] == ".") {
                start++
                startSpace++
            }
            if (startSpace == 0) {
                start++
                continue
            }
            
            var endSpace = 0
            val endSymbol = blocks[end]
            while (blocks[end] == endSymbol) {
                end--
                endSpace++
            }
            
            if (startSpace >= endSpace) {
                start = startBegin
                end = endBegin
                while (endSpace > 0) {
                    blocks.swap(start, end)
                    start++
                    end--
                    endSpace--
                }
            } else {
                end = endBegin
            }
        }

        return blocks.calculateChecksum()
    }

    private fun parseBlocks(input: String): List<List<String>> {
        fun explode(digit: Int, index: Int): List<String> {
            val character: String = if (index.isEven()) (index / 2).toString() else "."
            return List(digit) { character }
        }

        return input
            .splitByCharacter()
            .map { it.parseInt() }
            .mapIndexed { index, digit -> explode(digit, index) }
    }

    private fun List<String>.calculateChecksum(): Long {
        return this
            .filter { it != "." }
            .mapIndexed { index, id -> index * id.toLong() }
            .sum()
    }
}

fun main() {
    Day09.solve()
}
