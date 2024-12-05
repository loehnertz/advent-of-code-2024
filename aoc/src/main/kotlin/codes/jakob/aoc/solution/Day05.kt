package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.*

object Day05 : Solution() {
    override fun solvePart1(input: String): Any {
        val (rules: List<Rule>, updates: List<Update>) = parseInput(input)
        val findValidUpdates: List<Update> = RuleSolver(rules, updates).findValidUpdates()
        return sumUpdatePages(findValidUpdates)
    }

    override fun solvePart2(input: String): Any {
        val (rules: List<Rule>, updates: List<Update>) = parseInput(input)
        val ruleSolver = RuleSolver(rules, updates)
        val invalidUpdates: List<Update> = updates - ruleSolver.findValidUpdates().toSet()
        val validatedUpdates: List<Update> = invalidUpdates.map { update ->
            fun tryToValidate(update: Update): Update {
                return update.pagesNumbers.fold(update) { toBeSortedUpdate, pageNumber ->
                    ruleSolver.doesNotAdheresToRules(toBeSortedUpdate, pageNumber)
                        .fold(toBeSortedUpdate) { update, rule ->
                            val pageNumbers = update.pagesNumbers.toMutableList()
                            pageNumbers.remove(rule.pageB)
                            pageNumbers.addLast(rule.pageB)
                            Update(pageNumbers)
                        }
                }
            }

            var maybeValidatedUpdate = tryToValidate(update)
            while (!ruleSolver.isValidUpdate(maybeValidatedUpdate)) {
                maybeValidatedUpdate = tryToValidate(maybeValidatedUpdate)
            }
            maybeValidatedUpdate
        }
        return sumUpdatePages(validatedUpdates)
    }

    private fun sumUpdatePages(updates: List<Update>): Int {
        return updates
            .map { it.pagesNumbers.middleOrNull()!! }
            .sumOf { it.value }
    }

    private fun parseInput(input: String): Pair<List<Rule>, List<Update>> {
        fun parseRules(rules: List<String>): List<Rule> {
            return rules
                .filterNot { it.isBlank() }
                .map { rule ->
                    val (pageA, pageB) = rule.split("|").map { PageNumber(it.toInt()) }
                    Rule(pageA, pageB)
                }
        }

        fun parseUpdates(updates: List<String>): List<Update> {
            return updates
                .filterNot { it.isBlank() }
                .map { update ->
                    Update(update.splitByComma().map { PageNumber(it.toInt()) })
                }
        }

        return input
            .splitByDoubleNewLine()
            .map { it.splitByLines() }
            .map({ parseRules(it) }, { parseUpdates(it) })
    }

    private class RuleSolver(
        rules: List<Rule>,
        private val updates: List<Update>,
    ) {
        private val rulesByPageA: Map<PageNumber, List<Rule>> = rules.groupBy { it.pageA }

        fun findValidUpdates(): List<Update> {
            return updates.filter { isValidUpdate(it) }
        }

        fun isValidUpdate(update: Update): Boolean {
            return update.pagesNumbers.all { pageNumber ->
                adheresToRules(update, pageNumber)
            }
        }

        fun adheresToRules(update: Update, pageNumber: PageNumber): Boolean {
            return doesNotAdheresToRules(update, pageNumber).isEmpty()
        }

        fun doesNotAdheresToRules(update: Update, pageNumber: PageNumber): Set<Rule> {
            require(update.contains(pageNumber)) { "The update does not contain the page number." }
            return rulesByPageA[pageNumber]?.filterNot { rule ->
                if (update.contains(rule.pageB)) {
                    update.indexOf(rule.pageB) > update.indexOf(rule.pageA)
                } else true
            }?.toSet() ?: emptySet()
        }
    }

    @JvmInline
    private value class PageNumber(val value: Int)

    private data class Rule(
        val pageA: PageNumber,
        val pageB: PageNumber,
    )

    private data class Update(
        val pagesNumbers: List<PageNumber>,
    ) {
        fun contains(pageNumber: PageNumber): Boolean = pagesNumbers.contains(pageNumber)

        fun indexOf(page: PageNumber): Int = pagesNumbers.indexOf(page)
    }
}

fun main() {
    Day05.solve()
}
