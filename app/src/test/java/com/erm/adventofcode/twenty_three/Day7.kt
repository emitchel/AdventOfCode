package com.erm.adventofcode.twenty_three

import org.junit.Test

class Day7 {

    sealed class Card(val card: Char, val ranking: Int) {
        companion object {
            fun fromInput(input: String): Card {
                return when (input) {
                    "A" -> Ace
                    "K" -> King
                    "Q" -> Queen
                    "J" -> Jack
                    "T" -> Ten
                    "9" -> Nine
                    "8" -> Eight
                    "7" -> Seven
                    "6" -> Six
                    "5" -> Five
                    "4" -> Four
                    "3" -> Three
                    "2" -> Two
                    else -> throw IllegalArgumentException("Invalid card input: $input")
                }
            }
        }

        data object Ace : Card('A', 12)
        data object King : Card('K', 11)
        data object Queen : Card('Q', 10)
        data object Jack : Card('J', 9)
        data object Ten : Card('T', 8)
        data object Nine : Card('9', 7)
        data object Eight : Card('8', 6)
        data object Seven : Card('7', 5)
        data object Six : Card('6', 4)
        data object Five : Card('5', 3)
        data object Four : Card('4', 2)
        data object Three : Card('3', 1)
        data object Two : Card('2', 0)

    }

    data class Hand(val cards: List<Card>) {
        val type by lazy {
            HandType.fromHand(this)
        }
    }

    object RoundComparator : Comparator<Round> {
        override fun compare(first: Round, second: Round): Int {
            return first.compare(second)
        }
    }

    data class Round(val hand: Hand, val bid: Long) {


        fun compare(to: Round): Int {
            return when {
                hand.type.ranking > to.hand.type.ranking -> 1
                hand.type.ranking < to.hand.type.ranking -> -1
                else -> {
//                    println("\nTiebreaker!!!\n${hand} =  ${hand.type}\nvs\n${to.hand} ${to.hand.type}")
                    var myCount = 0
                    var theirCount = 0
                    for (i in 0 until hand.cards.size) {
                        if (hand.cards[i].ranking > to.hand.cards[i].ranking) {
                            myCount++
                        } else if (hand.cards[i].ranking < to.hand.cards[i].ranking) {
                            theirCount++
                        }

                        if (myCount > theirCount) return 1.also{
//                            println("Winner: ${hand}")
                        }
                        if (myCount < theirCount) return (-1).also{
//                            println("Winner: ${to.hand}")
                        }
                    }
                    return 1
                }
            }
        }

        companion object {
            fun fromInput(input: String): Round {
                val split = input.split(" ")
                val cards = split[0].map { Card.fromInput(it.toString()) }
                val hand = Hand(cards)
                val bid = split[1].toLong()
                return Round(hand, bid)
            }
        }
    }

    sealed class HandType(val ranking: Int) {
        companion object {
            fun fromHand(hand: Hand): HandType {
                val cardCount = mutableMapOf<Card, Int>()
                hand.cards.forEach {
                    cardCount[it] = cardCount.getOrDefault(it, 0) + 1
                }

                if (cardCount.size == 1) return FiveOfAKind(hand.cards[0])
                if (cardCount.size == 2) {
                    // full house, or four of a kind
                    cardCount.entries.firstOrNull { it.value > 3 }?.let {
                        return FourOfAKind(it.key)
                    }
                    return FullHouse(
                        OnePair(cardCount.entries.first { it.value == 2 }.key),
                        ThreeOfAKind(cardCount.entries.first { it.value == 3 }.key)
                    )
                }
                if (cardCount.size == 3) {
                    // two pair, or three of a kind
                    cardCount.entries.firstOrNull { it.value == 3 }?.let {
                        return ThreeOfAKind(it.key)
                    }
                    val pairs = cardCount.filter { it.value == 2 }

                    return TwoPair(
                        OnePair(pairs.entries.first().key),
                        OnePair(pairs.entries.last().key),
                        cardCount.entries.first { it.value == 1 }.key
                    )
                }

                if (cardCount.size == 4) {
                    // one pair
                    return OnePair(cardCount.entries.first { it.value == 2 }.key)
                }

                return HighCard(hand.cards.maxBy { it.ranking })
            }
        }

        // A
        data class HighCard(
            val of: Card
        ) : HandType(0)


        // AA XYZ
        data class OnePair(
            val of: Card
        ) : HandType(1)

        // AA KK X
        data class TwoPair(
            val pair1: OnePair,
            val pair2: OnePair,
            val leftOver: Card
        ) : HandType(2)

        // AAA YZ
        data class ThreeOfAKind(
            val of: Card,
        ) : HandType(3)

        // AAAKK
        data class FullHouse(
            val onePair: OnePair,
            val threeOfAKind: ThreeOfAKind
        ) : HandType(4)

        // AAAAK
        data class FourOfAKind(
            val of: Card,
        ) : HandType(5)

        data class FiveOfAKind(val of: Card) :
            HandType(6)

    }

    @Test
    fun day7_pt1() {

        var rounds = mutableListOf<Round>()

        Util.readLinesFromTextInput("day7input.txt") {
            rounds.add(Round.fromInput(it))
            true
        }


        rounds = rounds.sortedWith(RoundComparator).toMutableList()
        var totalWinnings = 0L
        rounds.forEachIndexed { index, round ->
            println("Rank ${index + 1} is ${round.hand.type} with bid ${round.bid}, ${round.hand}")
            totalWinnings += round.bid * (index + 1)
        }
        println("Total Winnings! $totalWinnings")
        assert(totalWinnings == 251106089L)
    }
}