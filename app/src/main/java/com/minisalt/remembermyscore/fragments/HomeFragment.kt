package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.HomeData
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment(private val savedGame: Boolean = false) :
    Fragment(R.layout.fragment_home) {

    private val dataMover = DataMover()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough.create()
        exitTransition = MaterialFadeThrough.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val allRules = arrayListOf<String>()


        numberPickerSetup()
        initSpinner(allRules)



        if (savedGame) {
            Snackbar.make(snackbar_pos, "Game saved successfully", Snackbar.LENGTH_LONG)
                .setAction("Dismiss") {}
                .show()
        }

        btnLaunchGame.setOnClickListener {
            if (allRules[0] != resources.getString(R.string.Lenny)) {
                val homeData = HomeData(numberPicker.value, dataMover.getStringFromIndex(requireContext(), spinner.selectedItemPosition))
                val currentGame = dataMover.loadCurrentGame(requireContext())

                if (currentGame == null)
                    goToGameFragment(homeData)
                else
                    dialog(homeData)

            } else
                goToRuleFragment()

        }
    }

    private fun initSpinner(nameOfListRules: ArrayList<String>) {
        val gameRuleList = dataMover.loadGameRules(requireContext())



        if (gameRuleList.size != 0)
            for (rule in gameRuleList)
                nameOfListRules.add(rule.name)
        else
            nameOfListRules.add(resources.getString(R.string.Lenny))

        val myAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, nameOfListRules)

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = myAdapter
    }

    fun dialog(homeData: HomeData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Start a new game?")
            .setMessage("You currently have a game active. Continuing will start a fresh game.")
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Start fresh") { dialog, which ->
                goToGameFragment(homeData)
                dialog.dismiss()
            }
            .show()
    }


    private fun numberPickerSetup() {
        numberPicker.minValue = 1
        numberPicker.maxValue = 99

        numberPicker.wrapSelectorWheel = true
    }

    private fun goToGameFragment(newGame: HomeData) {
        fragmentManager?.beginTransaction()?.replace(
            R.id.container, GameFragment
                (homeData = newGame)
        )?.commit()
        val navigationView = requireActivity().findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(1).isChecked = true
    }

    private fun goToRuleFragment() {
        fragmentManager?.beginTransaction()?.replace(
            R.id.container, RulesFragment()
        )?.commit()
        val navigationView = requireActivity().findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(2).isChecked = true
    }
}
