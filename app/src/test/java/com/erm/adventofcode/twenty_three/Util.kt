package com.erm.adventofcode.twenty_three

import java.io.BufferedReader
import java.io.InputStreamReader

object Util {

    fun readLinesFromTextInput(
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
}

val String.allDigits: Boolean
    get() = this.all { listOf('1', '2', '3', '4', '5', '6', '7', '8', '9').contains(it) }

val Char.isDigit: Boolean
    get() = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').contains(this)