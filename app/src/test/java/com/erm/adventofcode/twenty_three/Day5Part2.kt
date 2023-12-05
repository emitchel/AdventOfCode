package com.erm.adventofcode.twenty_three

import com.erm.adventofcode.twenty_three.Util.readLinesFromTextInput
import org.junit.Test

class Day5Part2 {

    data class Map(
        val source: String = "",
        val destination: String = "",
        val mappings: List<String> = emptyList()
    ) {

        private val knownMappingSourceToRange = mutableMapOf<Long, Long>()

        data class DestinationToSourceRange(
            val destinationStart: Long,
            val sourceStart: Long,
            val range: Long
        )

        val destinationToSourceRanges = mutableListOf<DestinationToSourceRange>()
        fun buildKnownRanges() {
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

                // cant do this, numbers too large
                // for (step in 0 until range) {
                //     knownMappingSourceToRange[sourceStart + step] = destinationStart + step
                // }

                println("")

            }
        }

        fun destination(incomingSource: String, sourceLocation: Long): Long {

            if (incomingSource != source) {
                throw IllegalStateException("Can't map $incomingSource to this $source-to-$destination map")
            }

            buildKnownRanges()

            destinationToSourceRanges.firstOrNull {
                sourceLocation >= it.sourceStart && sourceLocation < it.sourceStart + it.range
            }?.let {
                val difference = sourceLocation - it.sourceStart
                return (it.destinationStart + difference).also {
                    println("$incomingSource $sourceLocation maps to $destination $it")
                }
            } ?: run {
                return sourceLocation.also {
                    println("$incomingSource $sourceLocation maps to $destination $it")
                }
            }
        }
    }

    @Test
    fun day5_pt1() {
        val seedIds = mutableListOf<Long>()
        val maps = mutableListOf<Map>()
        var inputIndex = 0
        var latestMap = Map()
        readLinesFromTextInput("day5input.txt") { input ->
            if (inputIndex == 0) {
                // build get seed ids
                // seeds: 79 14 55 13
                val seedsAndIds = input.split(":").mapTrimmed()

                seedIds.addAll(seedsAndIds[1].split(" ").map {
                    it.trim().toLong()
                })
            } else if (input.isNotEmpty()) {
                // seed-to-soil map:
                // 50 98 2
                // 52 50 48
                if (input.contains(":")) {
                    // seed-to-soil
                    val sourceToDestination = input.split("-").mapTrimmed()
                    latestMap = latestMap.copy(
                        source = sourceToDestination[0],
                        destination = sourceToDestination[2]
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
                if (latestMap.source.isNotEmpty()) {
                    maps.add(latestMap)
                }

                // finish by resetting the reference
                latestMap = Map()
            }

            inputIndex++
            true
        }

        // add the last one generated
        if (latestMap.source.isNotEmpty()) {
            maps.add(latestMap)
        }

        var lowestLocation = Long.MAX_VALUE
        seedIds.forEach { seedId ->
            var currentId = seedId
            var currentSource = "seed"
            maps.forEach { map ->
                currentSource = map.source
                currentId = map.destination(currentSource, currentId)
            }
            // we're at the last humidity-to-location map
            lowestLocation = Math.min(currentId, lowestLocation)
        }

        assert(lowestLocation == 510109797L)
    }
}