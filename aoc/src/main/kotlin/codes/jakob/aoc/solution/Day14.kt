package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.Coordinates
import codes.jakob.aoc.shared.Grid
import codes.jakob.aoc.shared.multiply
import codes.jakob.aoc.shared.splitByLines

object Day14 : Solution() {
    private val INPUT_PATTERN = Regex("""p=(-?\d+),(-?\d+)\s+v=(-?\d+),(-?\d+)""")

    override fun solvePart1(input: String): Any {
        val grid: Grid<MutableSet<Robot>> = parseInput(input)

        repeat(100) { grid.walkRobotsOneTile() }

        return grid.split(4)
            .map { quadrant -> quadrant.cells.flatMap { it.content.value }.distinct().count() }
            .multiply()
    }

    override fun solvePart2(input: String): Any {
        val grid: Grid<MutableSet<Robot>> = parseInput(input)

        return generateSequence(1) { it + 1 }
            .onEach { grid.walkRobotsOneTile() }
            .first {
                val robotsSurroundedByRobots: Int = grid.cells.count { cell ->
                    cell.getAdjacent(diagonally = true).all { it.value?.content?.value?.isNotEmpty() ?: false }
                }
                robotsSurroundedByRobots > grid.cells.count() / 100
            }
    }

    private fun Grid<MutableSet<Robot>>.walkRobotsOneTile() {
        for ((robot: Robot, coordinates: Coordinates) in this.findRobotsToCoordinates()) {
            val currentCell: Grid.Cell<MutableSet<Robot>> = this[coordinates]!!
            currentCell.content.value.remove(robot)

            val newCoordinates: Coordinates = coordinates.plus(robot.velocity, this, wrapAround = true)!!
            val newCell: Grid.Cell<MutableSet<Robot>> = this[newCoordinates]!!
            newCell.content.value.add(robot)
        }
    }

    private fun Grid<MutableSet<Robot>>.findRobotsToCoordinates(): Map<Robot, Coordinates> {
        return this
            .filter { it.content.value.isNotEmpty() }
            .map { it.content.value to it.coordinates }
            .flatMap { (robots, coordinates) -> robots.map { it to coordinates } }
            .toMap()
    }

    private fun parseInput(input: String): Grid<MutableSet<Robot>> {
        val robots: Set<Robot> = input.splitByLines().map {
            val (px, py, vx, vy) = INPUT_PATTERN.find(it)!!.destructured
            Robot(Coordinates(px.toInt(), py.toInt()), Coordinates(vx.toInt(), vy.toInt()))
        }.toSet()
        val coordinateValues: Map<Coordinates, (Grid.Cell<MutableSet<Robot>>) -> MutableSet<Robot>> = robots
            .groupBy { it.startCoordinates }
            .mapValues { (_, robots) -> { robots.toMutableSet() } }
        return Grid(
            coordinateValues = coordinateValues,
            defaultValueConstructor = { mutableSetOf() },
            eager = true,
        )
    }

    private data class Robot(
        val startCoordinates: Coordinates,
        val velocity: Coordinates,
    )
}

fun main() {
    Day14.solve()
}
