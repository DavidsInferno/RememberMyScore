package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.DataMover
import com.minisalt.remembermyscore.preferences.HomeData
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    val dataMover = DataMover()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameOfListRules = ArrayList<String>()

        val gameRuleList = DataMover().loadGameRules(context!!)

        if (gameRuleList.size != 0)
            for (rule in gameRuleList)
                nameOfListRules.add(rule.name)
        else
            nameOfListRules.add("You have no games saved")

        numberPickerSetup()


        val myAdapter: ArrayAdapter<String> =
            ArrayAdapter(context!!, android.R.layout.simple_list_item_1, nameOfListRules)

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = myAdapter

        btnLaunchGame.setOnClickListener {
            if (nameOfListRules[0] != "You have no games saved") {
                val homeData = HomeData(
                    numberPicker.value, dataMover.getStringFromIndex
                        (context!!, spinner.selectedItemPosition)
                )
                val currentGame = dataMover.loadCurrentGame(context!!)


                goToPlayScreen(homeData)

                /*OVO JE ZA PROVJERIT AKO IMA UPALJENA IGRA
                if (currentGame == null) {
                    goToPlayScreen(homeData)
                } else {
                    //prozor ce se otvorit
                }*/
            }
        }
    }

    private fun numberPickerSetup() {
        numberPicker.minValue = 1
        numberPicker.maxValue = 99

        numberPicker.wrapSelectorWheel = true
    }

    private fun goToPlayScreen(newGame: HomeData) {
        fragmentManager?.beginTransaction()?.replace(
            R.id.container, GameFragment
                (homeData = newGame)
        )?.commit()
        val navigationView = activity!!.findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(1).isChecked = true
    }
}
