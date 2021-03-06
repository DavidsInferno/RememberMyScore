package com.minisalt.remembermyscore.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.minisalt.remembermyscore.HelpActivity
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
        MobileAds.initialize(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        HomeFragmentAd.loadAd(adRequest)
        HomeFragmentAd.visibility = View.GONE

        HomeFragmentAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                HomeFragmentAd.visibility = View.VISIBLE
                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(p0: Int) {
                //Toast.makeText(context, "Ad failed to load: $p0", Toast.LENGTH_SHORT).show()
                super.onAdFailedToLoad(p0)
            }
        }

        val allRules = arrayListOf<String>()


        helpButton.setOnClickListener {
            val myIntent = Intent(context, HelpActivity::class.java)
            startActivity(myIntent)
        }


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
            ArrayAdapter(requireContext(), R.layout.spinner_item, nameOfListRules)

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = myAdapter
    }

    fun dialog(homeData: HomeData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Start a new game?")
            .setMessage("You currently have a game active. Continuing will start a fresh game.")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Start fresh") { dialog, _ ->
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
        parentFragmentManager.beginTransaction().replace(
            R.id.container, GameFragment
                (homeData = newGame)
        ).commit()
        val navigationView = requireActivity().findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(1).isChecked = true
    }

    private fun goToRuleFragment() {
        parentFragmentManager.beginTransaction().replace(
            R.id.container, RulesFragment()
        ).commit()
        val navigationView = requireActivity().findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(2).isChecked = true
    }

    override fun onPause() {
        HomeFragmentAd.destroy()
        super.onPause()
    }
}
