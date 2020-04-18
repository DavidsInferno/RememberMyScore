package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.DataMover
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private val dataMover = DataMover()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameOfListRules = ArrayList<String>()

        val gameRuleList = dataMover.loadGameRules(context!!)

        if (gameRuleList.size != 0)
            for (rule in gameRuleList)
                nameOfListRules.add(rule.name)
        else
            nameOfListRules.add("You have no games saved")



        numberPicker.minValue = 1
        numberPicker.maxValue = 99

        numberPicker.wrapSelectorWheel = true


        val myAdapter: ArrayAdapter<String> =
            ArrayAdapter(context!!, android.R.layout.simple_list_item_1, nameOfListRules)

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = myAdapter

        btnLaunchGame.setOnClickListener {
            println("YOU LEFT THE BUTTON TO START THE GAME")
        }

    }
}
