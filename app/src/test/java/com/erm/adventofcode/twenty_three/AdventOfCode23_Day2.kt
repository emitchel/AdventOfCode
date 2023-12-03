package com.erm.adventofcode.twenty_three

import com.erm.adventofcode.twenty_three.Util.readLinesFromTextInput
import java.lang.Long.max
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AdventOfCode23_Day2 {
    enum class Color(val value: String, val max: Int) {
        RED("red", 12),
        GREEN("green", 13),
        BLUE("blue", 14);

        companion object {
            fun from(inputString: String): Color {
                return when (inputString) {
                    "red" -> RED
                    "green" -> GREEN
                    "blue" -> BLUE
                    else -> throw IllegalStateException("unrecognized color $inputString")
                }
            }
        }
    }

    data class GameCube(
        val count: Long,
        val color: Color
    )

    data class GameSet(
        val gameCubes: List<GameCube>
    )

    data class Game(
        val id: Long,
        val sets: List<GameSet>
    ) {

        fun powerOfFewestNumberOfColors(): Long {
            var minRed = 0L
            var minBlue = 0L
            var minGreen = 0L

            // TODO do this ahead of time while generating the game
            sets.forEach { gameSet ->
                gameSet.gameCubes.forEach { gameCube ->
                    val count = gameCube.count
                    when (gameCube.color) {
                        Color.RED -> {
                            minRed = max(minRed, count)
                        }

                        Color.GREEN -> {
                            minGreen = max(minGreen, count)
                        }

                        Color.BLUE -> {
                            minBlue = max(minBlue, count)
                        }
                    }
                }
            }

            return (minRed * minBlue * minGreen)
        }

        companion object {
            fun fromInput(gameInput: String, allowInvalid: Boolean = false): Game? {
                //  gameInput ~ Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green

                // ["Game X"], ["3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"]
                val gameIdAndSets = gameInput.split(":")
                // ["Game X"] -> ["Game"],["X"] -> "X".toLong()
                val gameId = gameIdAndSets[0].split(" ")[1].trim().toLong()

                //["3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"] -> ["3 blue, 4 red"],[1 red, 2 green, 6 blue"],[" 2 green"]
                val gameSetsString = gameIdAndSets[1].split(";").map { it.trim() }

                val gameSets = mutableListOf<GameSet>()
                gameSetsString.forEach { gameSet ->
                    //["3 blue, 4 red"], -> GameCube(3, "blue"), GameCube(4,"red")
                    val gameCubesForSet = mutableListOf<GameCube>()
                    gameSet.split(",").forEach { gameCubeString ->
                        val countColor = gameCubeString.trim().split(" ").map { it.trim() }

                        val count = countColor[0].toLong()
                        val color = Color.from(countColor[1])

                        if (!allowInvalid && count > color.max) {
                            println("Invalid game found! gameId $gameId, $count exist of ${color.value}")
                            return null
                        }

                        gameCubesForSet.add(
                            GameCube(count, color)
                        )
                    }

                    gameSets.add(GameSet(gameCubesForSet))
                }
                val validGame = Game(
                    id = gameId,
                    sets = gameSets
                )
                println("New valid game created $gameId $validGame")

                return validGame
            }
        }
    }

    @Test
    fun day2_part1() {
        // find the sum of the IDs of "possible" games knowing
        // 12 red cubes, 13 green cubes, and 14 blue cubes are all that is available in the bag

        var sumOfPossibleIds = 0L

        readLinesFromTextInput("day2input_pt1.txt") { gameInput ->
            Game.fromInput(gameInput)?.let {
                sumOfPossibleIds += it.id
            }
            true
        }

        println("Total sum of possible IDs $sumOfPossibleIds")

        assert(sumOfPossibleIds == 2476L)
    }

    @Test
    fun day2_part2() {
        // find the sum of the IDs of "possible" games knowing
        // 12 red cubes, 13 green cubes, and 14 blue cubes are all that is available in the bag

        var sumOfAllPowers = 0L

        readLinesFromTextInput("day2input_pt1.txt") { gameInput ->
            val game = Game.fromInput(gameInput, true)!!
            sumOfAllPowers += game.powerOfFewestNumberOfColors()
            true
        }

        println("Total sum of all powers $sumOfAllPowers")

        assert(sumOfAllPowers == 54911L)
    }
}