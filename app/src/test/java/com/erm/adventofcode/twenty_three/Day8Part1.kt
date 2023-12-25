package com.erm.adventofcode.twenty_three

import org.junit.Test

class Day8Part1 {

    sealed interface Direction {
        data object Left : Direction
        data object Right : Direction
    }

    class Instruction(val operation: List<Direction>) {
        private var step = 0
        fun getNextDirection(): Direction {
            return operation[step++].also {
                if (step > operation.size - 1) step = 0
            }
        }

//        fun getDirectionsShifted(by: Int): List<Direction> {
//            return operation.drop(by) + operation.take(by)
//        }

        companion object {
            fun fromInput(inputString: String): Instruction {
                val input = inputString.split("").mapNotNull {
                    it.ifEmpty { null }
                }
                return Instruction(input.map {
                    when (it) {
                        "L" -> Direction.Left
                        "R" -> Direction.Right
                        else -> throw IllegalArgumentException("Invalid direction: $it")
                    }
                })
            }
        }
    }

    class ElementCache(
        val startingElement: Element,
        var finalElement: Element? = null,
        var zzzFoundAfterStepCount: Int? = null
    )

    data class Element(val name: String, val leftName: String, val rightName: String) {

        fun get(direction: Direction): String {
            return when (direction) {
                is Direction.Left -> leftName
                is Direction.Right -> rightName
            }
        }

        companion object {
            fun fromInput(inputString: String): Element {
                // input is as follows: AAA = (BBB, CCC)
                val nameAndLR = inputString.split("=").mapTrimmed()
                val name = nameAndLR[0].trim()
                val lr = nameAndLR[1].split(",").map {
                    it.replace("(", "").replace(")", "").trim()
                }
                val leftName = lr[0]
                val rightName = lr[1]
                return Element(name, leftName, rightName)
            }
        }
    }

    @Test
    fun day8part1() {
        var instruction: Instruction? = null
        val rawLookupInstructions = mutableMapOf<String, Element>()
        val allElements = mutableListOf<Element>()

        Util.readLinesFromTextInput("day8input.txt") {
            if (it.isEmpty()) return@readLinesFromTextInput true
            if (!it.contains("=")) {
                // instruction
                instruction = Instruction.fromInput(it)
            } else {
                // build pre element
                Element.fromInput(it).also { preElement ->
                    rawLookupInstructions[preElement.name] = preElement
                    allElements.add(preElement)
                }
            }
            true
        }

        val elementCaches = mutableListOf<ElementCache>()

        var steps = 0L
        allElements.forEach { currentElement ->
            // for each element, build a cache of all instructions shifted incrementally
            val elementCache = ElementCache(currentElement)
            var finalElement = currentElement
            instruction!!.operation.forEachIndexed { index, direction ->
                finalElement = allElements.first { it.name == finalElement.get(direction) }

                if (finalElement.name == "ZZZ") {
                    elementCache.zzzFoundAfterStepCount = index + 1
                }
            }

            elementCache.finalElement = finalElement
            elementCaches.add(elementCache)
        }

        val elementCacheMap = elementCaches.map { it.startingElement to it }.toMap()

        val elementsThatContainsZZZ =
            elementCaches.filter { it.zzzFoundAfterStepCount != null }

        println("\n!!!Element Caches built! Found n=${elementsThatContainsZZZ.size} elements that contain zzz\n")

        var targetedElementCache =
            elementCaches.first { it.startingElement.name == "AAA" }
        var foundZZZ = false

        val uniqueElementsStarted = mutableMapOf<Element, Int>()

        var sizeOfUniqueElements = 0
        var repeating = false

        while (!foundZZZ) {
//            if (repeating) {
//                println("!!${targetedElementCache.startingElement.name} is to ${targetedElementCache.finalElement?.name}")
//            }
            uniqueElementsStarted[targetedElementCache.startingElement] =
                uniqueElementsStarted.getOrDefault(targetedElementCache.startingElement, 1)

            if (targetedElementCache.zzzFoundAfterStepCount != null) {
                steps += targetedElementCache.zzzFoundAfterStepCount!!
                foundZZZ = true
            } else {
                targetedElementCache = elementCacheMap[targetedElementCache.finalElement]!!
                steps += instruction!!.operation.size
            }

//            if (uniqueElementsStarted.size == sizeOfUniqueElements && !repeating) {
//                println("No new elements started!")
//                repeating = true
//            }

            if(uniqueElementsStarted.keys.contains(targetedElementCache.startingElement)) {
                println("Repeating element ${targetedElementCache.startingElement.name}!")
            }

            println("Evaluated ${uniqueElementsStarted.size} of ${allElements.size} unique elements")
            sizeOfUniqueElements = uniqueElementsStarted.size
        }

        println("Steps taken! $steps")
        assert(steps == 0L)


    }
}