package codes.jakob.aoc.solution

object Day03 : Solution() {
    private val MUL_PATTERN = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
    private val DODONT_PATTERN = Regex("(do\\(\\))|(don't\\(\\))")

    override fun solvePart1(input: String): Any {
        return MUL_PATTERN.findAll(input).sumOf {
            val (a, b) = it.destructured
            a.toLong() * b.toLong()
        }
    }

    override fun solvePart2(input: String): Any {
        val instructionByIndex: Map<Int, Instruction> = DODONT_PATTERN.findAll(input).associate {
            it.range.last to Instruction.fromString(it.value)
        }
        return MUL_PATTERN.findAll(input).mapNotNull {
            val (a, b) = it.destructured
            val instruction = instructionByIndex.findClosestPreviousInstruction(it.range.first)
            if (instruction == Instruction.DO) {
                a.toLong() * b.toLong()
            } else null
        }.sum()
    }

    private fun Map<Int, Instruction>.findClosestPreviousInstruction(index: Int): Instruction {
        return this.entries.findLast { (instructionIndex, _) -> instructionIndex < index }?.value ?: Instruction.DO
    }

    private enum class Instruction {
        DO,
        DONT;

        companion object {
            fun fromString(value: String): Instruction {
                return when (value) {
                    "do()" -> DO
                    "don't()" -> DONT
                    else -> throw IllegalArgumentException("Unknown instruction: $value")
                }
            }
        }
    }
}

fun main() {
    Day03.solve()
}
