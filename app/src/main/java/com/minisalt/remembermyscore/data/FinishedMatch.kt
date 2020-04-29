package com.minisalt.remembermyscore.data

import java.util.*

data class FinishedMatch(
    var players: ArrayList<PlayerData> = arrayListOf(), val gamePlayed: GameRules = GameRules(), var datePlayed:
    Date = Date(), var expanded: Boolean = false
) {}