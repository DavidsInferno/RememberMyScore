package com.minisalt.remembermyscore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.minisalt.remembermyscore.preferences.DataMover
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
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

        val ble = ArrayList<String>()

        val gameRuleList = dataMover.loadGameRules(context!!)

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
}
