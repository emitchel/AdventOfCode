package com.erm.adventofcode.twenty_three

import com.erm.adventofcode.twenty_three.Util.readLinesFromTextInput
import java.lang.Long.max
import kotlinx.coroutines.test.runTest
import org.junit.Test

class Day5Part2 {

    data class Map(
        val sourceName: String = "",
        val destinationName: String = "",
        val mappings: List<String> = emptyList()
    ) {

        private val knownMappingSourceToRange = mutableMapOf<Long, Long>()

        data class DestinationToSourceRange(
            val destinationStart: Long,
            val sourceStart: Long,
            val range: Long
        )

        val destinationToSourceRanges = mutableListOf<DestinationToSourceRange>()

        fun getUpperBoundOfRange(): Long {
            val maxSource = destinationToSourceRanges.maxBy { it.range + it.sourceStart }.run {
                range + sourceStart
            }
            val maxDestination =
                destinationToSourceRanges.maxBy { it.range + it.destinationStart }.run {
                    range + destinationStart
                }
            return max(maxSource, maxDestination)
        }

        fun buildKnownRanges() {
            if (destinationToSourceRanges.isNotEmpty()) return
            mappings.forEach { mapping ->
                // destinationLocation, sourceLocation, range
                // 50 98 2
                val destinationSourceRange = mapping.split(" ").map {
                    it.trim().toLong()
                }
                val destinationStart = destinationSourceRange[0]
                val sourceStart = destinationSourceRange[1]
                val range = destinationSourceRange[2]

                destinationToSourceRanges.add(
                    DestinationToSourceRange(
                        destinationStart = destinationStart,
                        sourceStart = sourceStart,
                        range = range
                    )
                )
            }
        }


        fun destination(incomingSource: String, sourceLocation: Long): Long {

            if (incomingSource != sourceName) {
                throw IllegalStateException("Can't map $incomingSource to this $sourceName-to-$destinationName map")
            }

            buildKnownRanges()

            destinationToSourceRanges.firstOrNull {
                sourceLocation >= it.sourceStart && sourceLocation < it.sourceStart + it.range
            }?.let {
                val difference = sourceLocation - it.sourceStart
                return (it.destinationStart + difference).also {
                    println("$incomingSource $sourceLocation maps to $destinationName $it")
                }
            } ?: run {
                return sourceLocation.also {
                    println("$incomingSource $sourceLocation maps to $destinationName $it")
                }
            }
        }
    }

    data class StartLength(
        val start: Long,
        val length: Long
    )

    @Test
    fun day5_pt2() = runTest {
        val seedRanges = mutableListOf<StartLength>()
        val maps = mutableListOf<Map>()
        var inputIndex = 0
        var latestMap = Map()
        readLinesFromTextInput("day5input.txt") { input ->
            if (inputIndex == 0) {
                // build get seed ids
                // seeds: 79 14 55 13
                val seedsAndIds = input.split(":").mapTrimmed()

                val ids = seedsAndIds[1].split(" ").map {
                    it.trim().toLong()
                }
                seedRanges.addAll(ids.chunked(2).map { StartLength(it[0], it[1]) })
            } else if (input.isNotEmpty()) {
                // seed-to-soil map:
                // 50 98 2
                // 52 50 48
                if (input.contains(":")) {
                    // seed-to-soil map:
                    val sourceToDestination = input.split("-").mapTrimmed()
                    latestMap = latestMap.copy(
                        sourceName = sourceToDestination[0],
                        //soil map:
                        destinationName = sourceToDestination[2].split(" ")[0]
                    )
                } else {
                    // create mappings
                    // 50 98 2
                    latestMap = latestMap.copy(
                        mappings = latestMap.mappings.toMutableList().apply {
                            add(input)
                        }
                    )
                }
            } else {
                if (latestMap.sourceName.isNotEmpty()) {
                    maps.add(latestMap)
                }

                // finish by resetting the reference
                latestMap = Map()
            }

            inputIndex++
            true
        }

        // add the last one generated
        if (latestMap.sourceName.isNotEmpty()) {
            maps.add(latestMap)
        }

        var lowestLocation = Long.MAX_VALUE


        maps.last().buildKnownRanges()
        val upper = maps.last().getUpperBoundOfRange()

        // Giving up for now.... I'm guessing we have to
        // reverse the tree to find a range of acceptable inputs
        // instead of iterating through the seeds
        // since that will take too long

        println("lowestLocation $lowestLocation")

        assert(lowestLocation == 46L)
    }
}