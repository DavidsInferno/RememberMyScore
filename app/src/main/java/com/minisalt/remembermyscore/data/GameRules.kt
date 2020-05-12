package com.minisalt.remembermyscore.data

import java.util.*


data class GameRules(
    var name: String = "",
    var pointsToWin: Int = 0,
    var buttons: ArrayList<Int> = arrayListOf(-1, 1),
    var startingPoints: Int = 0,


    var advancedMode: Boolean = true,


    var diceRequired: Boolean = false,
    var lowestPointsWin: Boolean = false,


    var extraField_1_enabled: Boolean = false,
    var extraField_2_enabled: Boolean = false,

    var extraField_1_pointsToWin: Int = 0,
    var extraField_2_pointsToWin: Int = 0,

    var extraField_1condition: Boolean = false,
    var extraField_2condition: Boolean = false,


    var extraField_StartPoint_1: Int = 0,
    var extraField_StartPoint_2: Int = 0,

    var roundCounter: Boolean = false,
    var roundMax: Int = 0
)