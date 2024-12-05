package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.ExpandedDirection
import codes.jakob.aoc.shared.ExpandedDirection.*
import codes.jakob.aoc.shared.Grid
import codes.jakob.aoc.shared.parseGrid

object Day04 : Solution() {
    private val XMAS_CHARACTERS: List<Char> = listOf('X', 'M', 'A', 'S')
    private val DIAGONAL_DIRECTION_PAIRS: List<Pair<ExpandedDirection, ExpandedDirection>> = listOf(
        NORTH_WEST to SOUTH_EAST,
        NORTH_EAST to SOUTH_WEST,
    )

    override fun solvePart1(input: String): Any {
        val grid: Grid<Char> = input.parseGrid { it }
        val xmasStartCells: List<Grid.Cell<Char>> = grid
            .filter { it.content.value == 'X' }
            .flatMap { cell: Grid.Cell<Char> ->
                fun isXmasStartCell(cell: Grid.Cell<Char>, direction: ExpandedDirection): Boolean {
                    tailrec fun checkSequence(adjacentCell: Grid.Cell<Char>?, xmasIndex: Int): Boolean {
                        return when {
                            adjacentCell == null -> false
                            XMAS_CHARACTERS[xmasIndex] != adjacentCell.content.value -> false
                            xmasIndex == XMAS_CHARACTERS.size -> true
                            else -> checkSequence(adjacentCell.getInDirection(direction), xmasIndex + 1)
                        }
                    }
                    return checkSequence(cell.getInDirection(direction), 1)
                }

                ExpandedDirection.entries.mapNotNull { direction: ExpandedDirection ->
                    if (isXmasStartCell(cell, direction)) cell else null
                }
            }
        return xmasStartCells.count()
    }

    override fun solvePart2(input: String): Any {
        val grid: Grid<Char> = input.parseGrid { it }
        val xmasMiddleCells: List<Grid.Cell<Char>> = grid
            .filter { it.content.value == 'A' }
            .filter { cell: Grid.Cell<Char> ->
                fun isMatchingXmasPair(
                    cell: Grid.Cell<Char>,
                    directionA: ExpandedDirection,
                    directionB: ExpandedDirection,
                ): Boolean {
                    val adjacentCellA: Grid.Cell<Char> = cell.getInDirection(directionA) ?: return false
                    val adjacentCellB: Grid.Cell<Char> = cell.getInDirection(directionB) ?: return false
                    val characters: Set<Char> = setOf(adjacentCellA.content.value, adjacentCellB.content.value)
                    return characters.count() == 2 && characters.contains('M') && characters.contains('S')
                }

                DIAGONAL_DIRECTION_PAIRS.all { (directionA: ExpandedDirection, directionB: ExpandedDirection) ->
                    isMatchingXmasPair(cell, directionA, directionB)
                }
            }
        return xmasMiddleCells.count()
    }
}

fun main() {
    Day04.solve()
}
