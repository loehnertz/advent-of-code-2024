package codes.jakob.aoc.shared

import codes.jakob.aoc.shared.ExpandedDirection.*


/**
 * A grid is a two-dimensional collection of cells.
 * Each cell has a value and a set of coordinates.
 * The coordinates are used to identify the cell in the grid.
 * The value is lazily evaluated.
 *
 * @param T The type of the value of each cell
 */
@Suppress("MemberVisibilityCanBePrivate")
class Grid<T>(input: List<List<(Cell<T>) -> T>>, private val eager: Boolean = false) {
    constructor(
        coordinateValues: Map<Coordinates, (Cell<T>) -> T>,
        defaultValueConstructor: (Cell<T>) -> T,
        eager: Boolean = false,
    ) : this(fromCoordinatesValues(coordinateValues, defaultValueConstructor), eager)

    val matrix: Matrix<T> = generateMatrix(input)
    val cells: LinkedHashSet<Cell<T>> = LinkedHashSet(matrix.flatten())
    val height: Int = matrix.size
    val width: Int = matrix.firstOrNull()?.size ?: 0

    operator fun get(coordinates: Coordinates): Cell<T>? = getAtCoordinates(coordinates)

    operator fun contains(coordinates: Coordinates): Boolean = this[coordinates] != null

    fun getAtCoordinates(coordinates: Coordinates): Cell<T>? = matrix.getOrNull(coordinates.y)?.getOrNull(coordinates.x)

    fun getAdjacent(coordinates: Coordinates, diagonally: Boolean = false): Map<ExpandedDirection, Cell<T>?> {
        return listOfNotNull(
            NORTH to getInDirection(coordinates, NORTH),
            if (diagonally) NORTH_EAST to getInDirection(coordinates, NORTH_EAST) else null,
            EAST to getInDirection(coordinates, EAST),
            if (diagonally) SOUTH_EAST to getInDirection(coordinates, SOUTH_EAST) else null,
            SOUTH to getInDirection(coordinates, SOUTH),
            if (diagonally) SOUTH_WEST to getInDirection(coordinates, SOUTH_WEST) else null,
            WEST to getInDirection(coordinates, WEST),
            if (diagonally) NORTH_WEST to getInDirection(coordinates, NORTH_WEST) else null,
        ).toMap()
    }

    fun getInDirection(coordinates: Coordinates, direction: ExpandedDirection): Cell<T>? {
        return getAtCoordinates(coordinates.inDirection(direction))
    }

    fun <R> map(block: (Cell<T>) -> R): Grid<R> = fromMatrix(matrix.map { row -> row.map(block) })

    fun map(coordinates: Coordinates, block: (Cell<T>) -> T): Grid<T> {
        val desired: Cell<T> = getAtCoordinates(coordinates) ?: error("Coordinates do not exist in this grid")
        return fromMatrix(matrix.map { row ->
            row.map { cell -> if (desired == cell) block(cell) else cell.content.value }
        })
    }

    fun filter(block: (Cell<T>) -> Boolean): List<Cell<T>> {
        return cells.filter(block)
    }

    fun find(block: (Cell<T>) -> Boolean): Cell<T>? {
        return cells.find(block)
    }

    /**
     * Reduces the grid in the given direction.
     * The outer reduction is applied to each row/column of the grid in the chosen direction.
     * The inner reduction is applied to the result of the outer reduction.
     * The result of the inner reduction is returned.
     * The reduction is applied to the grid in the given direction.
     * For example, if the direction is [SimpleDirection.NORTH], the grid is transposed and the outer reduction is applied to each column.
     *
     * @param direction The direction in which the grid is reduced
     * @param outer The reduction applied to each row/column of the grid
     * @param inner The reduction applied to the result of the outer reduction
     * @return The result of the inner reduction
     */
    fun <R1, R2> reduce(
        direction: SimpleDirection,
        outer: (List<Cell<T>>) -> R1,
        inner: (List<R1>) -> R2,
    ): R2 {
        val matrixInDirection: Matrix<T> = when (direction) {
            SimpleDirection.NORTH -> matrix.transpose()
            SimpleDirection.EAST -> matrix.map { it.reversed() }
            SimpleDirection.SOUTH -> matrix.transpose().map { it.reversed() }
            SimpleDirection.WEST -> matrix
        }
        return inner(matrixInDirection.map(outer))
    }

    /**
     * Splits into many grids of the same size that are `1/parts` of the original grid.
     * If a cell is not evenly divisible by the number of parts, it will be excluded from the split.
     */
    fun split(parts: Int): List<Grid<T>> {
        val partWidth: Int = width / (parts / 2)
        val partHeight: Int = height / (parts / 2)

        val grids: MutableList<Grid<T>> = mutableListOf()
        var startX = 0
        var startY = 0
        var endX = partWidth
        var endY = partHeight
        for (part in 0 until parts) {
            val partMatrix: List<List<T>> =
                matrix.subList(startY, endY)
                    .map { row -> row.subList(startX, endX) }
                    .map { row -> row.map { cell -> cell.content.value } }
            grids += fromMatrix(partMatrix, eager)

            if (part.isEven()) {
                startX = endX + (if (width.isOdd()) 1 else 0)
                endX += partWidth + 1
            } else {
                startX = 0
                endX = partWidth
                startY = endY + (if (height.isOdd()) 1 else 0)
                endY += partHeight + 1
            }
        }

        return grids
    }

    fun replaceCell(coordinates: Coordinates, newContent: (Cell<T>) -> T): Grid<T> {
        val cell: Cell<T> = getAtCoordinates(coordinates) ?: error("Coordinates do not exist in this grid")

        val newCell: Cell<T> = Cell(this, cell.coordinates, newContent)
        val newMatrix: MutableList<List<Cell<T>>> = matrix.toMutableList()
        val newRow: MutableList<Cell<T>> = newMatrix[cell.coordinates.y].toMutableList()

        newMatrix[cell.coordinates.y] = newRow
        newRow[cell.coordinates.x] = newCell

        return Grid(newMatrix.map { it.map { cell -> { cell.content.value } } })
    }

    private fun generateMatrix(input: List<List<(Cell<T>) -> T>>): List<List<Cell<T>>> {
        return input.mapIndexed { y: Int, row: List<(Cell<T>) -> T> ->
            row.mapIndexed { x: Int, valueConstructor: (Cell<T>) -> T ->
                Cell(this, x, y, valueConstructor).apply {
                    if (eager) content.value
                }
            }
        }
    }

    class Cell<T>(
        private val grid: Grid<T>,
        val coordinates: Coordinates,
        valueConstructor: (Cell<T>) -> T,
    ) {
        constructor(
            grid: Grid<T>,
            x: Int,
            y: Int,
            valueConstructor: (Cell<T>) -> T,
        ) : this(grid, Coordinates(x, y), valueConstructor)

        val content: Lazy<T> = lazy { valueConstructor(this) }

        fun getAdjacent(diagonally: Boolean = false): Map<ExpandedDirection, Cell<T>?> {
            return grid.getAdjacent(coordinates, diagonally)
        }

        fun distanceTo(other: Cell<T>, diagonally: Boolean = false): Int {
            require(other in grid.cells) { "Start and end point are not in the same grid" }
            return this.coordinates.distanceTo(other.coordinates, diagonally)
        }

        fun getInDirection(direction: SimpleDirection): Cell<T>? {
            return getInDirection(direction.expanded)
        }

        fun getInDirection(direction: ExpandedDirection): Cell<T>? {
            return grid.getInDirection(coordinates, direction)
        }

        /**
         * Maps the cell in the given direction.
         * The mapping is applied to each cell in the given direction.
         */
        fun <R> mapInDirection(direction: ExpandedDirection, block: (Cell<T>) -> R): List<R> {
            val mapped: MutableList<R> = mutableListOf()
            var currentCell: Cell<T>? = this
            while (currentCell != null) {
                currentCell = currentCell.getInDirection(direction)
                if (currentCell != null) mapped += block(currentCell)
            }
            return mapped
        }

        /**
         * Reduces the cell in the given direction.
         * The accumulator is applied to each cell in the given direction.
         */
        fun <R> foldInDirection(
            direction: ExpandedDirection,
            initial: R,
            accumulator: (R, Cell<T>) -> R,
        ): R {
            var reduced: R = initial
            var currentCell: Cell<T>? = this
            while (currentCell != null) {
                currentCell = currentCell.getInDirection(direction)
                if (currentCell != null) reduced = accumulator(reduced, currentCell)
            }
            return reduced
        }

        /**
         * Folds the cell in every direction.
         * The accumulator is applied to each cell in every direction.
         */
        fun <R> foldInEveryDirection(
            initial: R,
            accumulator: (R, Cell<T>) -> R,
        ): Map<ExpandedDirection, R> {
            return entries.associateWith { this.foldInDirection(it, initial, accumulator) }
        }

        override fun toString(): String {
            return "Cell(value=$content, coordinates=$coordinates)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cell<*>

            if (grid != other.grid) return false
            if (coordinates != other.coordinates) return false

            return true
        }

        override fun hashCode(): Int {
            var result: Int = grid.hashCode()
            result = 31 * result + coordinates.hashCode()
            return result
        }
    }

    companion object {
        fun <T> fromMatrix(matrix: List<List<T>>, eager: Boolean = false): Grid<T> {
            return Grid(matrix.map { inner -> inner.map { value -> { value } } }, eager)
        }

        private fun <T> fromCoordinatesValues(
            coordinateValues: Map<Coordinates, (Cell<T>) -> T>,
            defaultValueConstructor: (Cell<T>) -> T,
        ): List<List<(Cell<T>) -> T>> {
            val maxX: Int = coordinateValues.keys.maxOf { it.x } + 1
            val maxY: Int = coordinateValues.keys.maxOf { it.y } + 1
            return List(maxY) { y: Int ->
                List(maxX) { x: Int ->
                    coordinateValues[Coordinates(x, y)] ?: defaultValueConstructor
                }
            }
        }
    }
}

typealias Matrix<T> = List<List<Grid.Cell<T>>>
