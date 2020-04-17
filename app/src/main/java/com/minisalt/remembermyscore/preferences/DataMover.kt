package com.minisalt.remembermyscore.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DataMover {
    fun loadGameRules(context: Context): ArrayList<GameRules>? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = sharedPreferences.getString("Game Rules", null)
        val type: Type = object : TypeToken<ArrayList<GameRules>>() {}.type

        val existingRules: ArrayList<GameRules>? = gson.fromJson<ArrayList<GameRules>>(json, type)


        if (existingRules == null) {
            return null
        } else {
            return existingRules
        }
    }

    fun saveGameRules(context: Context, list: GameRules) {
        val gameRulesList: ArrayList<GameRules> = arrayListOf(list)

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()

        //this appends to the end
        val allGameRules: ArrayList<GameRules>? = loadGameRules(context)
        if (allGameRules != null) {

            allGameRules.add(list)

            //sorts the list by name (So it is easier later to populate the recyclerview for all the rules)
            val sortedList = allGameRules.sortedWith((compareBy({ it.name })))

            val json: String = gson.toJson(sortedList)
            editor.putString("Game Rules", json)
            editor.apply()

        } else {
            val json: String = gson.toJson(gameRulesList)
            editor.putString("Game Rules", json)
            editor.apply()
        }
        //----------------------

    }
}