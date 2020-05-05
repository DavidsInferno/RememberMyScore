package com.minisalt.remembermyscore.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class DataMover {
    fun loadGameRules(context: Context): ArrayList<GameRules> {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString("Game Rules", null)
        val type: Type = object : TypeToken<ArrayList<GameRules>>() {}.type

        return Gson().fromJson(json, type) ?: arrayListOf()
    }

    fun appendToGameRules(context: Context, rule: GameRules) {

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        //this appends to the end
        val allGameRules: ArrayList<GameRules> = loadGameRules(context)
        if (allGameRules.size != 0) {

            allGameRules.add(rule)

            //sorts the list by name (So it is easier later to populate the recyclerview for all the rules)
            val sortedList = allGameRules.sortedWith((compareBy { it.name }))

            editor.putString("Game Rules", Gson().toJson(sortedList))
            editor.apply()

        } else {
            val gameRulesList: ArrayList<GameRules> = arrayListOf()
            gameRulesList.add(rule)
            println(gameRulesList)
            editor.putString("Game Rules", Gson().toJson(gameRulesList))
            editor.apply()
        }
        //----------------------
    }

    fun saveGameRules(context: Context, list: ArrayList<GameRules>) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("Game Rules", Gson().toJson(list))
        editor.apply()
    }

    fun replaceGameRule(context: Context, updatedListing: GameRules, position: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val allGameRules = loadGameRules(context)

        allGameRules[position] = updatedListing

        editor.putString("Game Rules", Gson().toJson(allGameRules))
        editor.apply()

    }

    fun gameRuleExistence(context: Context, rule: GameRules): Boolean {
        val allGameRules: ArrayList<GameRules> = loadGameRules(context)

        return allGameRules.contains(rule)
    }


    fun saveCurrentGame(context: Context, match: FinishedMatch) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("Current Game", Gson().toJson(match))
        editor.apply()
    }

    fun loadCurrentGame(context: Context): FinishedMatch? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString("Current Game", null)
        val type: Type = object : TypeToken<FinishedMatch>() {}.type

        return Gson().fromJson<FinishedMatch>(json, type)
    }

    fun loadAllMatches(context: Context): ArrayList<FinishedMatch> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString("Finished Matches", null)
        val type: Type = object : TypeToken<ArrayList<FinishedMatch>>() {}.type

        return Gson().fromJson(json, type) ?: arrayListOf()
    }

    fun appendToFinishedMatches(context: Context, match: FinishedMatch) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        //this appends to the end
        val allFinishedMatches: ArrayList<FinishedMatch>? = loadAllMatches(context)
        if (allFinishedMatches != null) {

            allFinishedMatches.add(match)

            //Sorts the list by date
            val sortedList = allFinishedMatches.sortedWith((compareByDescending { it.datePlayed }))

            editor.putString("Finished Matches", Gson().toJson(sortedList))
            editor.apply()

        } else {
            val finishedMatches: ArrayList<FinishedMatch> = arrayListOf()
            finishedMatches.add(match)
            editor.putString("Finished Matches", Gson().toJson(finishedMatches))
            editor.apply()
        }
        //----------------------
    }

    fun deleteCurrentGame(context: Context) {
        val settings = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        settings.edit().remove("Current Game").apply()
    }


    fun getIndexOfRule(context: Context, gameName: String): Int {
        val allRules = loadGameRules(context)

        var counter = 0

        if (allRules.size != 0) {
            for (i in allRules)
                if (i.name == gameName)
                    return counter
                else
                    counter++
        }
        return -1
    }

    fun getStringFromIndex(context: Context, index: Int): String {
        val allRules = loadGameRules(context)

        return allRules[index].name
    }


    fun dateOfLastGame(context: Context, gameTitle: String): Date? {
        val allMatches = loadAllMatches(context)
        for (i in allMatches)
            if (i.gamePlayed.name == gameTitle)
                return i.datePlayed
        return null
    }

    fun numberOfGamesPlayed(context: Context, gameTitle: String): Int {
        val allMatches = loadAllMatches(context)
        var counter = 0
        for (i in allMatches)
            if (i.gamePlayed.name == gameTitle)
                counter++

        return counter
    }

}