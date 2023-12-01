package com.erm.adventofcode.twenty_three

import java.io.BufferedReader
import java.io.InputStreamReader
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AdventOfCode23 {
    @Test
    fun day1_part1() {
        var value = 0L
        readLinesFromTextInput("day1input_pt1.txt") {

            val numberRegex = "\\d+".toRegex()
            // Groupings of numbers ["123"],["1"],['6345']
            val numbers = numberRegex.findAll(it)
                .map { it.value }
                .toList()

            if (numbers.isEmpty()) return@readLinesFromTextInput true

            // take first char of first group
            val firstDigit = numbers.firstOrNull()?.firstOrNull() ?: ""
            // take last char of last group
            val lastDigit = numbers.lastOrNull()?.lastOrNull() ?: ""
            val totalString = "$firstDigit$lastDigit"
            val total = if (totalString.isNotEmpty()) {
                totalString.toLong()
            } else {
                0
            }

            value += total
            true
        }

        assert(value == 54450L)
    }

    @Test
    fun day1_part2() {
        val numbersAsStrings = mapOf(
            "one" to 1L,
            "two" to 2L,
            "three" to 3L,
            "four" to 4L,
            "five" to 5L,
            "six" to 6L,
            "seven" to 7L,
            "eight" to 8L,
            "nine" to 9L
        )
        var value = 0L
        readLinesFromTextInput("day1input_pt2.txt") {

            val normalRegex = "\\d+|one|two|three|four|five|six|seven|eight|nine".toRegex()
            val reversed = "\\d+|eno|owt|eerht|ruof|evif|xis|neves|thgie|enin".toRegex()
            // Groupings of numbers ["123"],["1"],['6345']
            val numbers = normalRegex.findAll(it)
                .map { it.value }
                .toList()


            val numbersReversed = reversed.findAll(it.reversed())
                .map { it.value }
                .toList()

            println("input $it \nnumbers forward: $numbers\nnumbers backwards $numbersReversed")

            val firstDigit = numbers.firstOrNull()?.let {
                if (it.allDigits) {
                    it.firstOrNull()
                } else {
                    numbersAsStrings[it]
                }
            } ?: ""
            val lastDigit = numbersReversed.firstOrNull()?.let {
                if (it.allDigits) {
                    it.firstOrNull()
                } else {
                    numbersAsStrings[it.reversed()]
                }
            } ?: ""

            println("combined: $firstDigit$lastDigit")
            println("-----")
            val totalString = "$firstDigit$lastDigit"

            val total = if (totalString.isNotEmpty()) {
                totalString.toLong()
            } else {
                0
            }

            value += total
            true
        }

        println("Total value: $value")

        assert(value == 54265L)
    }

    private fun readLinesFromTextInput(
        fileName: String,
        // return true to continue streaming
        readNewLine: (String) -> Boolean
    ) {
        val inputStream = this.javaClass.classLoader?.getResourceAsStream(fileName)
        inputStream?.use { stream ->
            InputStreamReader(stream).use { isr ->
                BufferedReader(isr).use { reader ->
                    var line: String? = null
                    var shouldContinue = true
                    while (shouldContinue && reader.readLine().also { line = it } != null) {
                        // Process each line character by character
                        shouldContinue = readNewLine(line!!)
                        // Here you know a new line was just finished
                    }
                }
            }
        }
    }

    private val String.allDigits: Boolean
        get() = this.all { listOf('1', '2', '3', '4', '5', '6', '7', '8', '9').contains(it) }
}