package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.splitByLines
import codes.jakob.aoc.shared.splitBySpace
import codes.jakob.aoc.shared.toPair
import kotlin.math.abs

object Day01 : Solution() {
    override fun solvePart1(input: String): Any {
        return parseInput(input)
            .let { (left, right) -> left.sorted() zip right.sorted() }
            .sumOf { (left, right) -> abs(left - right) }
    }

    override fun solvePart2(input: String): Any {
        return parseInput(input)
            .let { (left, right) ->
                left.map { leftValue ->
                    leftValue * right.count { rightValue -> leftValue == rightValue }
                }
            }
            .sum()
    }

    private fun parseInput(input: String): Pair<List<Int>, List<Int>> {
        return input
            .splitByLines()
            .map { line -> line.splitBySpace().map { it.toInt() }.toPair() }
            .unzip()
    }
}

fun main() {
    Day01.solve()
}
