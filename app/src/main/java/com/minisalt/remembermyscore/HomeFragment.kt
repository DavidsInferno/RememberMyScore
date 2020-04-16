package com.minisalt.remembermyscore

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minisalt.remembermyscore.preferences.GameRules
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.reflect.Type

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ble = ArrayList<String>()

        val gameRuleList = loadData()

        if (gameRuleList != null)
            for (rule in gameRuleList)
                ble.add(rule.name)



        numberPicker.minValue = 1
        numberPicker.maxValue = 99

        numberPicker.wrapSelectorWheel = true

        val myAdapter: ArrayAdapter<String> =
            ArrayAdapter(context!!, android.R.layout.simple_list_item_1, ble)

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = myAdapter

        btnLaunchGame.setOnClickListener {
            println("YOU LEFT THE BUTTON TO START THE GAME")
        }

    }

    private fun loadData(): ArrayList<GameRules>? {
        val sharedPreferences: SharedPreferences =
            context!!.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
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

}
