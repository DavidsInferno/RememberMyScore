package com.minisalt.remembermyscore.data

import java.util.*


data class GameRules(
    var name: String = "",
    var pointsToWin: Int = 0,
    var buttons: ArrayList<Int> = arrayListOf(-1, 1),
    var diceRequired: Boolean = false
)