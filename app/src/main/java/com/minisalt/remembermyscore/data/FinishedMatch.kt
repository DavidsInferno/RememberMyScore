package com.minisalt.remembermyscore.data

import java.util.*

data class FinishedMatch(
    var players: ArrayList<PlayerData> = arrayListOf(), val gamePlayed: GameRules = GameRules(), var datePlayed:
    Date? = null, var expanded: Boolean = false
) {
    fun addPlayerPositionsAndNames() {
        val sortedList: List<PlayerData> = players.sortedWith((compareByDescending { it.playerPoints }))

        for (i in sortedList.indices) {
            sortedList[i].playerPosition = i + 1
            if (sortedList[i].playerName == "")
                sortedList[i].playerName = "Player ${i + 1}"
            players[i] = sortedList[i]
        }
    }
}