package com.minisalt.remembermyscore.data

import java.util.*

data class FinishedMatch(
    var players: ArrayList<PlayerData> = arrayListOf(),
    val gamePlayed: GameRules = GameRules(),
    var expanded: Boolean = false,
    var diceProperties: ArrayList<Int> = arrayListOf(-1, -1),
    var datePlayed: Date? = null,
    var round: Int = 0
) {



    fun addPlayerPositionsAndNames() {
        if (gamePlayed.lowestPointsWin)
            ascending()
        else
            descending()
    }

    fun addPlayerPoints() {
        if (gamePlayed.extraField_1_enabled && !gamePlayed.extraField_2_enabled) {
            for (i in players) {
                i.playerPoints = gamePlayed.startingPoints
                i.playerPoints2 = gamePlayed.extraField_StartPoint_1
            }
        } else if (gamePlayed.extraField_1_enabled && gamePlayed.extraField_2_enabled) {
            for (i in players) {
                i.playerPoints = gamePlayed.startingPoints
                i.playerPoints2 = gamePlayed.extraField_StartPoint_1
                i.playerPoints3 = gamePlayed.extraField_StartPoint_2
            }
        } else {
            for (i in players)
                i.playerPoints = gamePlayed.startingPoints
        }
    }


    private fun ascending() {
        val sortedList: List<PlayerData> = players.sortedWith((compareBy { it.playerPoints }))

        for (i in sortedList.indices) {
            sortedList[i].playerPosition = i + 1
            if (sortedList[i].playerName == "")
                sortedList[i].playerName = "Player ${i + 1}"
            players[i] = sortedList[i]
        }
    }

    private fun descending() {
        val sortedList: List<PlayerData> = players.sortedWith((compareByDescending { it.playerPoints }))


        for (i in sortedList.indices) {
            sortedList[i].playerPosition = i + 1
            if (sortedList[i].playerName == "")
                sortedList[i].playerName = "Player ${i + 1}"
            players[i] = sortedList[i]
        }
    }
}