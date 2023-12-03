package com.erm.adventofcode.twenty_three

import com.erm.adventofcode.twenty_three.Util.readLinesFromTextInput
import org.junit.Test

class AdventOfCode23_Day3 {

    data class Range(val from: Int, val to: Int) {
        fun toList() = (from..to).toList()
    }

    data class PartNumber(
        val value: Long,
        val xRange: Range,
        val y: Int,
    ) {
        fun touchesAnySymbols(symbols: List<Symbol>): Boolean {
            val validYPositions = listOf(y, y - 1, y + 1)
            val validXPositions = listOf(xRange.from - 1, xRange.to + 1) + xRange.toList()

            var touches = false
            symbols.forEach { symbol ->
                if (validXPositions.contains(symbol.x) && validYPositions.contains(symbol.y)) {
                    println("$this touches $symbol")
                    touches = true
                }
            }
            return touches
        }
    }

    data class Symbol(
        val char: Char,
        val x: Int,
        val y: Int
    )

    @Test
    fun day3_pt1() {
        val schematic = mutableListOf<List<Char>>()
        val partNumbers = mutableListOf<PartNumber>()
        val symbols = mutableListOf<Symbol>()

        readLinesFromTextInput("day3input.txt") { schematicLine ->

            if(schematicLine == ".241....966...........%.........804...............589....554...307.+....#.505..........&....332...449.190......780........*322..........-540") {
                println("")
            }

            var numberInMotion: String? = null
            val row = schematicLine.mapIndexed { index, char ->
                if (char.isDigit) {
                    // current number!
                    if (numberInMotion == null) {
                        numberInMotion = char.toString()
                    } else {
                        numberInMotion += char.toString()
                    }

                    if (index == schematicLine.length-1) {
                        val number = numberInMotion!!.toLong()
                        partNumbers.add(
                            PartNumber(
                                number,
                                Range(index - numberInMotion!!.length+1, index),
                                schematic.size
                            ).also {
                                println("Created PartNumber $it")
                            }
                        )
                    }
                } else {
                    if (numberInMotion != null) {
                        // create new part number
                        val number = numberInMotion!!.toLong()
                        partNumbers.add(
                            PartNumber(
                                number,
                                Range(index - numberInMotion!!.length, index - 1),
                                schematic.size
                            ).also {
                                println("Created PartNumber $it")
                            }
                        )
                    }
                    // Symbol!

                    if (char != '.') {
                        symbols.add(
                            Symbol(
                                char,
                                index,
                                schematic.size
                            ).also {
                                println("Created Symbol $it")
                            })
                    }

                    numberInMotion = null
                }

                // still create the schematic
                char
            }
            schematic.add(row)
            true
        }

        var sumOfValidPartNumbers = 0L

        partNumbers.forEach { partNumber ->
            if (partNumber.touchesAnySymbols(symbols)) {
                sumOfValidPartNumbers += partNumber.value
            }
        }

        println("sumbofvalidpartnumbers = $sumOfValidPartNumbers")

        assert(sumOfValidPartNumbers == 4361L)
    }
}