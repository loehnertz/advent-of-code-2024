package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.plus
import codes.jakob.aoc.shared.splitByLines
import codes.jakob.aoc.shared.takeAndDrop
import codes.jakob.aoc.solution.Day07.Operation.*

object Day07 : Solution() {
    override fun solvePart1(input: String): Any {
        val equations: List<Equation> = parseInput(input)
        return evaluate(equations, setOf(ADDITION, MULTIPLICATION))
    }

    override fun solvePart2(input: String): Any {
        val equations: List<Equation> = parseInput(input)
        return evaluate(equations, setOf(ADDITION, MULTIPLICATION, CONCATENATION))
    }

    private fun evaluate(equations: List<Equation>, operations: Set<Operation>): Long {
        fun fold(valuesLeft: List<Long>, targetValue: Long, operations: Set<Operation>): Boolean {
            if (valuesLeft.size == 1) return valuesLeft.first() == targetValue

            val (valuesToTry: List<Long>, remainingValues: List<Long>) = valuesLeft.takeAndDrop(2)
            val (leftValue: Long, rightValue: Long) = valuesToTry

            for (operation: Operation in operations) {
                val result: Long = operation.evaluate(leftValue, rightValue)
                if (fold(result + remainingValues, targetValue, operations)) {
                    return true
                }
            }

            return false
        }

        return equations
            .filter { fold(it.values, it.targetValue, operations) }
            .sumOf { it.targetValue }
    }

    private fun parseInput(input: String): List<Equation> {
        return input
            .splitByLines()
            .map { line ->
                val (targetValue, operators) = line.split(": ")
                Equation(
                    targetValue = targetValue.toLong(),
                    values = operators.split(" ").map { it.toLong() }
                )
            }
    }

    private data class Equation(
        val targetValue: Long,
        val values: List<Long>,
    )

    private enum class Operation(val operation: (Long, Long) -> Long) {
        ADDITION({ left, right -> left + right }),
        MULTIPLICATION({ left, right -> left * right }),
        CONCATENATION({ left, right -> "$left$right".toLong() });

        fun evaluate(leftValue: Long, rightValue: Long): Long = operation(leftValue, rightValue)
    }
}

fun main() {
    Day07.solve()
}
