package com.minisalt.remembermyscore.preferences

import java.util.*

data class FinishedMatch(
    val players: ArrayList<PlayerData> = arrayListOf(), val gameTitle: String = "", var datePlayed:
    Date = Date()
) {}