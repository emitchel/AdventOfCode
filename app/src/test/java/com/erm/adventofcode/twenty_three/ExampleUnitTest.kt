package com.erm.adventofcode.twenty_three

import androidx.core.text.isDigitsOnly
import java.io.BufferedReader
import java.io.InputStreamReader
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AdventOfCode23 {
    @Test // 33ms
    fun day1() {
        var value = 0L
        readLinesFromTextInput("day1input.txt") {

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

        assert(value == 0L) // no spoilersr!
    }

    private fun readLinesFromTextInput(
        fileName: String,
        // return true to continue streaming
        readNewLine: (String) -> Boolean
    ) {
        val inputStream = this.javaClass.classLoader.getResourceAsStream(fileName)
        inputStream.use { stream ->
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
}