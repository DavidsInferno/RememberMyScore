package com.minisalt.remembermyscore.preferences

import java.util.*


data class GameRules(
    var name: String = "",
    var pointsToWin: Int = 0,
    var buttons: ArrayList<Int> = arrayListOf(-1, 1)
) {
    var datePlayed: Date = Date()
    var gamesPlayed = 0
}