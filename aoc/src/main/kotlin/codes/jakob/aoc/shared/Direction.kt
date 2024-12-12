package codes.jakob.aoc.shared

/**
 * This class represents a direction on a 2D grid.
 */
enum class SimpleDirection(val expanded: ExpandedDirection) {
    NORTH(ExpandedDirection.NORTH),
    EAST(ExpandedDirection.EAST),
    SOUTH(ExpandedDirection.SOUTH),
    WEST(ExpandedDirection.WEST);

    fun turn(turnDirection: TurnDirection): SimpleDirection {
        return when (this) {
            NORTH -> when (turnDirection) {
                TurnDirection.LEFT -> WEST
                TurnDirection.RIGHT -> EAST
            }
            EAST -> when (turnDirection) {
                TurnDirection.LEFT -> NORTH
                TurnDirection.RIGHT -> SOUTH
            }
            SOUTH -> when (turnDirection) {
                TurnDirection.LEFT -> EAST
                TurnDirection.RIGHT -> WEST
            }
            WEST -> when (turnDirection) {
                TurnDirection.LEFT -> SOUTH
                TurnDirection.RIGHT -> NORTH
            }
        }
    }
    
    enum class TurnDirection {
        LEFT,
        RIGHT
    }

    companion object {
        fun fromExpandedDirection(expandedDirection: ExpandedDirection): SimpleDirection {
            return entries.first { it.expanded == expandedDirection }
        }
    }
}

/**
 * This class represents a direction on a 2D grid, including the diagonals.
 */
enum class ExpandedDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST
}
