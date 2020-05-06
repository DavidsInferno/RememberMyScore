package com.minisalt.remembermyscore.data

import java.util.*

data class FinishedMatch(var players: ArrayList<PlayerData> = arrayListOf(), val gamePlayed: GameRules = GameRules()) {

    var expanded: Boolean = false

    var diceProperties: ArrayList<Int> = arrayListOf(-1, -1)


    var datePlayed: Date? = null


    fun addPlayerPositionsAndNames() {
        if (gamePlayed.lowestPointsWin)
            ascending()
        else
            descending()
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