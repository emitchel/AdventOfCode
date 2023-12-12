package com.erm.adventofcode.twenty_three

import org.junit.Test

class Day6 {

    data class Race(
        val time: Long,
        val recordDistance: Long
    ) {
        fun waysToWin(): Long {
            var waysToWin = 0
            for (holdTime in 1..time) {
                val leftOverTime = time - holdTime
                if (leftOverTime * holdTime > recordDistance) {
                    waysToWin += 1
                }
            }
            return waysToWin.toLong()
        }
    }

    @Test
    fun day6_p1() {
        var aggregatedPoints = 1L
        var times = listOf<Long>()
        var distances = listOf<Long>()
        Util.readLinesFromTextInput("day6input.txt") {
            if (it.contains("Time")) {
                times = it.split(":").toMutableList()
                    .apply {
                        removeAt(0)
                    }[0]
                    .split(" ")
                    .mapNotNull { it.ifBlank { null }?.trim()?.toLong() }

            } else if (it.contains("Distance")) {
                distances = it.split(":").toMutableList()
                    .apply {
                        removeAt(0)
                    }[0]
                    .split(" ")
                    .mapNotNull { it.ifBlank { null }?.trim()?.toLong() }
            }
            true
        }
        times.forEachIndexed { index, time ->
            val race = Race(time, distances[index])

            aggregatedPoints *= race.waysToWin()
        }


        println("aggregated ways =$aggregatedPoints")
        assert(aggregatedPoints == 288L)
    }
}