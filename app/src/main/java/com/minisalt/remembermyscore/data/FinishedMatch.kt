package com.minisalt.remembermyscore.data

import java.util.*

data class FinishedMatch(
    var players: ArrayList<PlayerData> = arrayListOf(), val gameTitle: String = "", var datePlayed:
    Date = Date()
) {}