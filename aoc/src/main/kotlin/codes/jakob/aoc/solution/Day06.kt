package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.Grid
import codes.jakob.aoc.shared.SimpleDirection
import codes.jakob.aoc.shared.SimpleDirection.NORTH
import codes.jakob.aoc.shared.SimpleDirection.TurnDirection.RIGHT
import codes.jakob.aoc.shared.parseGrid

object Day06 : Solution() {
    override fun solvePart1(input: String): Any {
        val grid: Grid<PositionObject> = input.parseGrid { PositionObject.fromChar(it) }
        val guardCell: Grid.Cell<PositionObject> = grid.find { it.content.value == PositionObject.GUARD }!!

        tailrec fun walk(
            currentCell: Grid.Cell<PositionObject>,
            currentDirection: SimpleDirection,
            visitedCells: Set<Grid.Cell<PositionObject>>,
        ): Set<Grid.Cell<PositionObject>> {
            val cellInFront: Grid.Cell<PositionObject>? = currentCell.getInDirection(currentDirection)
            return when (cellInFront?.content?.value) {
                // Continue moving forward
                PositionObject.EMPTY, PositionObject.GUARD -> {
                    walk(
                        cellInFront,
                        currentDirection,
                        visitedCells + cellInFront,
                    )
                }

                // Turn right and continue
                PositionObject.OBSTACLE -> {
                    walk(
                        currentCell,
                        currentDirection.turn(RIGHT),
                        visitedCells,
                    )
                }

                // Walked to the end of grid
                null -> visitedCells
            }
        }

        return walk(guardCell, NORTH, setOf(guardCell)).count()
    }

    override fun solvePart2(input: String): Any {
        val grid: Grid<PositionObject> = input.parseGrid { PositionObject.fromChar(it) }

        tailrec fun containsLoop(
            currentCell: Grid.Cell<PositionObject>,
            currentDirection: SimpleDirection,
            visitedCells: Set<Grid.Cell<PositionObject>>,
        ): Boolean {
            if (visitedCells.count() > 1 && currentCell == visitedCells.first() && currentDirection == NORTH) {
                return true
            }

            val cellInFront: Grid.Cell<PositionObject>? = currentCell.getInDirection(currentDirection)
            return when (cellInFront?.content?.value) {
                // Continue moving forward
                PositionObject.EMPTY, PositionObject.GUARD -> {
                    containsLoop(
                        cellInFront,
                        currentDirection,
                        visitedCells + cellInFront,
                    )
                }

                // Turn right and continue
                PositionObject.OBSTACLE -> {
                    containsLoop(
                        currentCell,
                        currentDirection.turn(RIGHT),
                        visitedCells,
                    )
                }

                // Walked to the end of grid
                null -> false
            }
        }

        val newGrids = grid
            .filter { it.content.value == PositionObject.EMPTY }
            .map { grid.replaceCell(it.coordinates) { PositionObject.OBSTACLE } }
            .filter { newGrid ->
                val guardCell: Grid.Cell<PositionObject> = newGrid.find { it.content.value == PositionObject.GUARD }!!
                containsLoop(guardCell, NORTH, setOf(guardCell)) 
            }
        
        return newGrids.count()
    }

    private enum class PositionObject {
        EMPTY,
        OBSTACLE,
        GUARD;

        companion object {
            fun fromChar(char: Char): PositionObject {
                return when (char) {
                    '.' -> EMPTY
                    '#' -> OBSTACLE
                    '^' -> GUARD
                    else -> throw IllegalArgumentException("Invalid character: $char")
                }
            }
        }
    }
}

fun main() {
    Day06.solve()
}
