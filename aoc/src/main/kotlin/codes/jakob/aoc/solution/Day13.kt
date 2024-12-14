package codes.jakob.aoc.solution

object Day13 : Solution() {
    private val INPUT_PATTERN = Regex("""(\w+\s\w+|\w+):\sX[+=](\d+),\sY[+=](\d+)""")

    override fun solvePart1(input: String): Any {
        val clawMachines: List<ClawMachine> = parseInput(input)
        return clawMachines
            .mapNotNull { clawMachine ->
                val buttonA = clawMachine.buttonA
                val buttonB = clawMachine.buttonB
                val prize = clawMachine.prize

                for (aTimes in 0..100) {
                    for (bTimes in 0..100) {
                        val xValue = buttonA.x * aTimes + buttonB.x * bTimes
                        val yValue = buttonA.y * aTimes + buttonB.y * bTimes

                        if (xValue == prize.x && yValue == prize.y) {
                            return@mapNotNull calculateCost(aTimes.toLong(), bTimes.toLong())
                        }
                    }
                }

                return@mapNotNull null
            }
            .sum()
    }

    override fun solvePart2(input: String): Any {
        TODO()
    }

    private fun calculateCost(aTimes: Long, bTimes: Long): Long {
        return aTimes * 3 + bTimes * 1
    }

    private fun parseInput(input: String): List<ClawMachine> {
        return input.split("\n\n").map {
            val (buttonA, buttonB, prize) = INPUT_PATTERN.findAll(it).map { match ->
                val (name, x, y) = match.destructured
                Value(Entity.fromString(name), x.toLong(), y.toLong())
            }.toList()
            ClawMachine(buttonA, buttonB, prize)
        }
    }

    private data class ClawMachine(
        val buttonA: Value,
        val buttonB: Value,
        val prize: Value,
    )

    private data class Value(
        val entity: Entity,
        val x: Long,
        val y: Long,
    )

    private enum class Entity {
        BUTTON_A,
        BUTTON_B,
        PRIZE;

        companion object {
            fun fromString(value: String): Entity {
                return when (value) {
                    "Button A" -> BUTTON_A
                    "Button B" -> BUTTON_B
                    "Prize" -> PRIZE
                    else -> throw IllegalArgumentException("Unknown entity: $value")
                }
            }
        }
    }
}

fun main() {
    Day13.solve()
}
