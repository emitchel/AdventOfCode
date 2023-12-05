package com.erm.adventofcode.twenty_three

import com.erm.adventofcode.twenty_three.Util.readLinesFromTextInput
import kotlin.math.pow
import org.junit.Test

class Day4 {

    data class Card(
        val id: Int,
        val winningNumbers: List<Long>,
        val chosenNumbers: List<Long>
    ) {

        var matchingNumbers: List<Long> = emptyList()
            private set
            get() {
                if (field.isNotEmpty()) return field

                field = winningNumbers.intersect(chosenNumbers).toList()
                return field
            }

        var points: Long = 0L
            private set
            get() {
                field = 2.0.pow(matchingNumbers.size.toDouble()).toLong()
                return field
            }

        var idsOfWinningCopies: List<Int> = emptyList()
            private set
            get() {
                field = (id + 1..id + matchingNumbers.size).toList()
                return field
            }

        companion object {
            fun fromInput(input: String): Card {
                //Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
                val cardIdColonNumbers = input.split(":").mapTrimmed()
                //Card 1
                val cardId =
                    cardIdColonNumbers[0].split(" ").filter { it.trim().isNotEmpty() }[1].toInt()
                //41 48 83 86 17 |  83 86  6 31 17  9 48 53
                val winningAndChosenNumbers = cardIdColonNumbers[1].split("|").mapTrimmed()

                // TODO to make faster, simply build one list that contains numbers that exist in both lists

                val winningNumbersSorted = winningAndChosenNumbers[0].split(" ")
                    .filter { it.trim().isNotEmpty() }
                    .map {
                        it.trim().toLong()
                    }.sorted()

                val chosenNumbersSorted = winningAndChosenNumbers[1].split(" ")
                    .filter { it.trim().isNotEmpty() }
                    .map {
                        it.trim().toLong()
                    }.sorted()

                return Card(cardId, winningNumbersSorted, chosenNumbersSorted)
            }
        }
    }

    @Test
    fun day4_pt1() {
        var aggregatedPoints = 0L
        readLinesFromTextInput("day4input.txt") {
            val card = Card.fromInput(it)
            aggregatedPoints += card.points
            true
        }
        println("aggregated Points =$aggregatedPoints")
        assert(aggregatedPoints == 21138L)
    }

    @Test
    fun day4_pt2() {
        val cardsAndCopies = mutableMapOf<Card, Int>()
        val finishCardsAndCopies = mutableMapOf<Card, Int>()

        readLinesFromTextInput("day4input.txt") { cardInput ->
            //set initial copy count to 1
            val newCard = Card.fromInput(cardInput).also {
                println("Card ${it.id} wins card ids ${it.idsOfWinningCopies}")
            }
            cardsAndCopies[newCard] = 1
            finishCardsAndCopies[newCard] = 1
            true
        }

        cardsAndCopies.forEach { card, copies ->
            card.idsOfWinningCopies.forEach { copyId ->
                val updatedCopiesOfCard = finishCardsAndCopies[card]!!
                val foundCard = cardsAndCopies.keys.first { it.id == copyId }
                finishCardsAndCopies[foundCard] =
                    finishCardsAndCopies[foundCard]!! + updatedCopiesOfCard
            }
        }


        assert(finishCardsAndCopies.values.sum() == 30)
    }
}