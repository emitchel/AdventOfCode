package com.erm.adventofcode.twenty_three

import com.erm.adventofcode.twenty_three.Util.readLinesFromTextInput
import org.junit.Test

class Day5Part2 {

    data class Map(
        val sourceName: String = "",
        val destinationName: String = "",
        val inputMappings: List<String> = emptyList()
    ) {

        data class Mapping(val destination: LongRange, val source: LongRange)

        class MappingTreeNode(
            var mapping: Mapping,
            var left: MappingTreeNode? = null,
            var right: MappingTreeNode? = null
        )

        class IntervalTree {
            var root: MappingTreeNode? = null

            fun insert(mapping: Mapping) {
                root = insert(root, mapping)
            }

            private fun insert(node: MappingTreeNode?, mapping: Mapping): MappingTreeNode {
                if (node == null) return MappingTreeNode(mapping)

                if (mapping.source.first < node.mapping.source.first) {
                    node.left = insert(node.left, mapping)
                } else {
                    node.right = insert(node.right, mapping)
                }

                return node
            }

            fun query(number: Long): Mapping? {
                return query(root, number)
            }

            private fun query(node: MappingTreeNode?, number: Long): Mapping? {
                if (node == null) return null

                if (node.mapping.source.contains(number)) {
                    return node.mapping
                }

                if (number < node.mapping.source.first) {
                    return query(node.left, number)
                }

                return query(node.right, number)
            }
        }

        private var tree = IntervalTree()

        fun buildIntervalTree() {
            tree = IntervalTree()
            inputMappings.forEach { mapping ->
                val (destinationStart, sourceStart, range) = mapping.split(" ")
                    .map { it.trim().toLong() }
                tree.insert(
                    Mapping(
                        LongRange(destinationStart, destinationStart + range - 1),
                        LongRange(sourceStart, sourceStart + range - 1)
                    )
                )
            }
        }


        fun destination(incomingSource: String, sourceLocation: Long): Long {

            if (incomingSource != sourceName) {
                throw IllegalStateException("Can't map $incomingSource to this $sourceName-to-$destinationName map")
            }

            return tree.query(sourceLocation)?.let {
                val difference = sourceLocation - it.source.first
                return (it.destination.first + difference).also {
//                    println("$incomingSource $sourceLocation maps to $destinationName $it")
                }
            } ?: run {
                return sourceLocation.also {
//                    println("$incomingSource $sourceLocation maps to $destinationName $it")
                }
            }
        }
    }

    @Test
    fun day5_pt2() {
        val seedRanges = mutableListOf<LongRange>()
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
                seedRanges.addAll(ids.chunked(2).map {
                    val start = it[0]
                    val end = it[1]
                    LongRange(start, start + end - 1)
                })
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
                        inputMappings = latestMap.inputMappings.toMutableList().apply {
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


        maps.forEach {
            it.buildIntervalTree()
        }

        var lowestLocation = Long.MAX_VALUE

        seedRanges.forEach { seedRange ->
            seedRange.forEach { seedId ->
                var currentId = seedId
                var currentSource = "seed"
                maps.forEach { map ->
//                    println("${map.sourceName} $currentId")
                    currentSource = map.sourceName
                    currentId = map.destination(currentSource, currentId)
//                    println("maps to ${map.destinationName} $currentId")
                }
                // we're at the last humidity-to-location map
                lowestLocation = Math.min(currentId, lowestLocation)
            }
        }

        println("lowest location: $lowestLocation")
        assert(lowestLocation == 46L)
    }
}