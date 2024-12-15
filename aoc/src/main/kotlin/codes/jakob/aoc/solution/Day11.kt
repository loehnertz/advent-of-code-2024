package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.isEven
import codes.jakob.aoc.shared.remap
import codes.jakob.aoc.shared.splitBySpace
import codes.jakob.aoc.shared.sum
import java.math.BigInteger

object Day11 : Solution() {
    override fun solvePart1(input: String): Any {
        val stones: List<Stone> = parseInput(input)
        return simulate(stones, 25)
    }

    override fun solvePart2(input: String): Any {
        val stones: List<Stone> = parseInput(input)
        return simulate(stones, 75)
    }

    private fun simulate(stones: List<Stone>, times: Int): BigInteger {
        return stones
            .groupBy { it }
            .mapValues { it.value.count().toBigInteger() }
            .remap(times) { stonesToCount: Map<Stone, BigInteger> ->
                val newStonesToCount = stonesToCount.toMutableMap()

                for ((stone, count) in stonesToCount) {
                    newStonesToCount.compute(stone) { _, c -> c!!.minus(count) }

                    val newStones: MutableList<Stone> = mutableListOf()
                    when {
                        stone == Stone(0) -> {
                            newStones.add(Stone(1))
                        }

                        stone.hasEvenDigits() -> {
                            newStones.addAll(stone.splitInHalf().toList())
                        }

                        else -> {
                            newStones.add(Stone(stone.value * 2024))
                        }
                    }
                    newStones.forEach { newStonesToCount.compute(it) { _, c -> c?.plus(count) ?: count } }
                }

                newStonesToCount
            }
            .values
            .sum()
    }

    private fun parseInput(input: String): List<Stone> {
        return input.splitBySpace().map { Stone(it.toLong()) }
    }

    @JvmInline
    private value class Stone(val value: Long) {
        fun hasEvenDigits(): Boolean {
            return value.toString().count().isEven()
        }

        fun splitInHalf(): Pair<Stone, Stone> {
            val stringValue = value.toString()
            val halfIndex = stringValue.count() / 2
            val firstHalf = stringValue.substring(0, halfIndex).toLong()
            val secondHalf = stringValue.substring(halfIndex).toLong()
            return Stone(firstHalf) to Stone(secondHalf)
        }
    }
}

fun main() {
    Day11.solve()
}
