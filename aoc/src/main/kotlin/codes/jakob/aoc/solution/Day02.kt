package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.splitByLines
import codes.jakob.aoc.shared.splitBySpace
import kotlin.math.abs

object Day02 : Solution() {
    override fun solvePart1(input: String): Any {
        return parseInput(input)
            .count { report -> report.isSafe() }
    }

    override fun solvePart2(input: String): Any {
        return parseInput(input)
            .count { report -> report.isSafeWithTolerance() }
    }

    private fun parseInput(input: String): List<Report> {
        return input
            .splitByLines()
            .map { line -> line.splitBySpace().map { Report.Level(it.toInt()) }.let { Report(it) } }
    }

    private data class Report(
        val levels: List<Level>
    ) {
        fun calculateDirectionChanges(): List<Pair<ChangeDirection, Int>>? {
            return levels
                .zipWithNext()
                .map { (a, b) ->
                    val direction = when {
                        a.value < b.value -> ChangeDirection.INCREASING
                        a.value > b.value -> ChangeDirection.DECREASING
                        else -> return null
                    }
                    val change = abs(a.value - b.value)
                    return@map direction to change
                }
        }

        @JvmInline
        value class Level(val value: Int)

        companion object {
            enum class ChangeDirection {
                INCREASING,
                DECREASING,
            }
        }
    }

    private fun Report.isSafe(): Boolean {
        val directionChanges = this.calculateDirectionChanges() ?: return false
        val firstDirection = directionChanges.first().first
        return directionChanges.all { (direction, change) ->
            direction == firstDirection && change in 1..3
        }
    }

    private fun Report.isSafeWithTolerance(): Boolean {
        fun removeLevelAtEachIndex(): Sequence<Report> {
            return levels.indices.asSequence().map { index ->
                levels.toMutableList().apply { removeAt(index) }.toList().let { Report(it) }
            }
        }

        return removeLevelAtEachIndex().any { it.isSafe() }
    }
}

fun main() {
    Day02.solve()
}
