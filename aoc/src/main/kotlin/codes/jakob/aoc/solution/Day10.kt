package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.Grid
import codes.jakob.aoc.shared.parseGrid
import codes.jakob.aoc.shared.parseInt

object Day10 : Solution() {
    override fun solvePart1(input: String): Any {
        return input
            .parseGrid { it.parseInt() }
            .findStartingCells()
            .sumOf { findTrailheadEndpoints(it).count() }
    }

    override fun solvePart2(input: String): Any {
        return input
            .parseGrid { it.parseInt() }
            .findStartingCells()
            .sumOf { computeTrailheadRating(it) }
    }

    private fun findTrailheadEndpoints(currentCell: Grid.Cell<Int>): Set<Grid.Cell<Int>> {
        return if (currentCell.content.value == 9) {
            setOf(currentCell)
        } else {
            currentCell.getAdjacent(diagonally = false)
                .values
                .asSequence()
                .filterNotNull()
                .filter { adjacentCell -> adjacentCell.content.value == currentCell.content.value + 1 }
                .map { findTrailheadEndpoints(it) }
                .flatten()
                .toSet()
        }
    }

    private fun computeTrailheadRating(currentCell: Grid.Cell<Int>): Int {
        return if (currentCell.content.value == 9) {
            1
        } else {
            currentCell.getAdjacent(diagonally = false)
                .values
                .filterNotNull()
                .filter { adjacentCell -> adjacentCell.content.value == currentCell.content.value + 1 }
                .sumOf { computeTrailheadRating(it) }
        }
    }

    private fun Grid<Int>.findStartingCells(): Set<Grid.Cell<Int>> {
        return this.filter { it.content.value == 0 }.toSet()
    }
}

fun main() {
    Day10.solve()
}
