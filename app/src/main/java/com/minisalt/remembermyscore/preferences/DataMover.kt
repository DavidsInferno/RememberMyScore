package com.minisalt.remembermyscore.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DataMover {
    fun loadGameRules(context: Context): ArrayList<GameRules> {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val json: String? = sharedPreferences.getString("Game Rules", null)
        val type: Type = object : TypeToken<ArrayList<GameRules>>() {}.type

        val existingRules: ArrayList<GameRules>? = Gson().fromJson<ArrayList<GameRules>>(json, type)


        if (existingRules == null) {
            return arrayListOf()
        } else {
            return existingRules
        }
    }

    fun appendToGameRules(context: Context, list: GameRules) {
        val gameRulesList: ArrayList<GameRules> = arrayListOf(list)

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        //this appends to the end
        val allGameRules: ArrayList<GameRules>? = loadGameRules(context)
        if (allGameRules != null) {

            allGameRules.add(list)

            //sorts the list by name (So it is easier later to populate the recyclerview for all the rules)
            val sortedList = allGameRules.sortedWith((compareBy { it.name }))

            editor.putString("Game Rules", Gson().toJson(sortedList))
            editor.apply()

        } else {
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
}