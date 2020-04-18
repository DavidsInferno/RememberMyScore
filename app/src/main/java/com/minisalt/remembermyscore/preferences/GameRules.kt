package com.minisalt.remembermyscore.preferences

import java.util.*


data class GameRules(var name: String = "", var pointsToWin: Int = 0, var buttons: ArrayList<Int>) {
    var datePlayed: Date = Date()
    var gamesPlayed = 0
}