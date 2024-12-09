package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.*

object Day08 : Solution() {
    override fun solvePart1(input: String): Any {
        val grid: Grid<Char> = input.parseGrid { it }
        return countAntinodesInGrid(grid) { (a: Coordinates, b: Coordinates) ->
            val howToReachBAntinode: List<ExpandedDirection> = a howToReach b
            val howToReachAAntinode: List<ExpandedDirection> = b howToReach a

            val antinodeB: Coordinates = b.walk(howToReachBAntinode)
            val antinodeA: Coordinates = a.walk(howToReachAAntinode)

            listOf(antinodeB, antinodeA)
        }
    }

    override fun solvePart2(input: String): Any {
        val grid: Grid<Char> = input.parseGrid { it }
        return countAntinodesInGrid(grid) { (a: Coordinates, b: Coordinates) ->
            tailrec fun walkToNextAntinode(
                start: Coordinates,
                steps: List<ExpandedDirection>,
                antinodes: Set<Coordinates>,
            ): Set<Coordinates> {
                if (steps.isEmpty()) return emptySet()
                if (start !in grid) return antinodes

                val end: Coordinates = start.walk(steps)
                return walkToNextAntinode(end, steps, antinodes.plus(end))
            }

            val howToReachBAntinode: List<ExpandedDirection> = a howToReach b
            val howToReachAAntinode: List<ExpandedDirection> = b howToReach a

            val antinodesB: Set<Coordinates> = walkToNextAntinode(a, howToReachBAntinode, emptySet())
            val antinodesA: Set<Coordinates> = walkToNextAntinode(b, howToReachAAntinode, emptySet())

            antinodesA + antinodesB
        }
    }

    private fun countAntinodesInGrid(
        grid: Grid<Char>,
        countingBlock: (Pair<Coordinates, Coordinates>) -> Collection<Coordinates>,
    ): Int {
        return findAntennaPairs(grid)
            .flatMap { (_, locations) -> locations.flatMap(countingBlock) }
            .distinct()
            .count { it in grid }
    }

    private fun findAntennaPairs(grid: Grid<Char>): Map<Char, List<Pair<Coordinates, Coordinates>>> {
        return grid
            .filter { cell ->
                cell.content.value.locationType == LocationType.ANTENNA
            }
            .map { cell ->
                cell.content.value to cell.coordinates
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.cartesianProduct(bothDirections = false, includeReflexive = false) }
    }

    private val Char.locationType: LocationType
        get() = when (this) {
            '.' -> LocationType.EMPTY
            '#' -> LocationType.ANTINODE
            else -> LocationType.ANTENNA
        }

    private enum class LocationType {
        EMPTY,
        ANTENNA,
        ANTINODE
    }
}

fun main() {
    Day08.solve()
}
