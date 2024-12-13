package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.Grid
import codes.jakob.aoc.shared.parseGrid

object Day12 : Solution() {
    override fun solvePart1(input: String): Any {
        val grid: Grid<Char> = input.parseGrid { it }

        fun findCellsInRegion(
            currentCell: Grid.Cell<Char>,
            visitedCells: Set<Grid.Cell<Char>> = emptySet(),
        ): Set<Grid.Cell<Char>> {
            if (currentCell in visitedCells) return visitedCells

            return currentCell.getAdjacent(diagonally = false)
                .mapNotNull { it.value }
                .filter { it.content.value == currentCell.content.value }
                .fold(visitedCells + currentCell) { visited, adjacent ->
                    findCellsInRegion(adjacent, visited)
                }
        }

        val (_, totalCost) = grid.cells
            .fold(emptySet<Grid.Cell<Char>>() to 0) { (visitedCells, totalCost), currentCell ->
                if (currentCell in visitedCells) {
                    visitedCells to totalCost
                } else {
                    val cellsInRegion: Set<Grid.Cell<Char>> = findCellsInRegion(currentCell)
                    val regionCost: Int = cellsInRegion.count() * cellsInRegion.sumOf { it.countRequiredFences() }
                    visitedCells + cellsInRegion to totalCost + regionCost
                }
            }

        return totalCost
    }

    override fun solvePart2(input: String): Any {
        TODO("Not yet implemented")
    }

    private fun Grid.Cell<Char>.countRequiredFences(): Int {
        return this.getAdjacent(diagonally = false).count { this.content.value != it.value?.content?.value }
    }
}

fun main() {
    Day12.solve()
}
