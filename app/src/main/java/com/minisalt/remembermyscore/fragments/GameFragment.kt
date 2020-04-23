package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.minisalt.remembermyscore.MainActivity
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.*
import com.minisalt.remembermyscore.recyclerView.adapter.GameAdapter
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*


class GameFragment(val homeData: HomeData? = null) :
    Fragment(R.layout.fragment_game) {

    lateinit var gameAdapter: GameAdapter

    var displayedGame: FinishedMatch = FinishedMatch()


    val dataMover = DataMover()

    var playerList: ArrayList<PlayerData> = arrayListOf()

    var saveOccured = false //prevents the game from being saved to cache


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        fabCustomization()


        val existingGame = dataMover.loadCurrentGame(context!!)

        val gameRules = dataMover.loadGameRules(context!!)


        btnTest.setOnClickListener {

        }



        when {
            homeData != null -> {
                val indexOfRule = dataMover.getIndexOfRule(context!!, homeData.gameName)
                freshGame(gameRules, indexOfRule)

                displayedGame = FinishedMatch(playerList, homeData.gameName)

                changingTitle(displayedGame.gameTitle, gameRules[indexOfRule].pointsToWin)
            }
            existingGame != null && dataMover.getIndexOfRule(context!!, existingGame.gameTitle) != -1 -> {
                val indexOfRule = dataMover.getIndexOfRule(context!!, existingGame.gameTitle)
                initRecyclerView(existingGame.players, gameRules[indexOfRule])
                displayedGame = FinishedMatch(existingGame.players, existingGame.gameTitle)


                changingTitle(existingGame.gameTitle, gameRules[indexOfRule].pointsToWin)

            }
            else -> {
                txtNoGameActive.visibility = View.VISIBLE
            }
        }
    }

    fun changingTitle(title: String, points: Int?) {
        if (points != null)
            (activity as MainActivity?)?.setTitleName("$title -> $points")
        else
            (activity as MainActivity?)?.setTitleName(title)
    }


    private fun freshGame(
        gameRules: ArrayList<GameRules>,
        indexOfGame: Int
    ) {
        for (i in 0 until homeData!!.players)
            playerList.add(PlayerData(playerName = "Player ${i + 1}"))


        initRecyclerView(playerList, gameRules[indexOfGame])
    }

    private fun initRecyclerView(playerList: ArrayList<PlayerData>, gameRule: GameRules) {
        recyclerViewPlayers.layoutManager = LinearLayoutManager(context)
        recyclerViewPlayers.setHasFixedSize(true)
        gameAdapter =
            GameAdapter(
                playerList, context!!, gameRule
            )
        recyclerViewPlayers.adapter = gameAdapter
    }


    fun fabCustomization() {
        fabMain()
        fabSave()
        fabClose()
        fabReset()


        speedDial.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_save_game -> {
                    displayedGame.datePlayed = Date()
                    dataMover.appendToFinishedMatches(context!!, displayedGame)

                    val snackBar = Snackbar.make(
                        recyclerViewPlayers, "Game saved successfully",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("Dismiss") { snackBar.dismiss() }
                    snackBar.show()
                    saveOccured = true

                    dataMover.deleteCurrentGame(context!!)

                    returnHome(savedGame = true, deletedGame = false)

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
                R.id.fab_close_game -> {
                    dataMover.deleteCurrentGame(context!!)
                    saveOccured = true
                    returnHome(savedGame = false, deletedGame = true)

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
                R.id.fab_reset -> {
                    val ammountOfPlayers = displayedGame.players.size
                    val indexOfGame = dataMover.getIndexOfRule(context!!, displayedGame.gameTitle)
                    val gameRules = dataMover.loadGameRules(context!!)


                    val freshPlayers: ArrayList<PlayerData> = arrayListOf()

                    for (i in 0 until ammountOfPlayers)
                        freshPlayers.add(PlayerData(playerName = "Player ${i + 1}"))

                    initRecyclerView(freshPlayers, gameRules[indexOfGame])

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
            }
            false
        })
    }


    fun fabMain() {
        speedDial.mainFabClosedBackgroundColor = resources.getColor(R.color.DarkPink)
    }

    fun fabSave() {
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_save_game, R.drawable.ic_save_black)
                .setFabBackgroundColor(resources.getColor(R.color.PacificCoast))
                .setLabelBackgroundColor(resources.getColor(R.color.LightPink))
                .setLabel("Save & close game")
                .create()
        )
    }

    fun fabClose() {
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_close_game, R.drawable.ic_delete_sweep)
                .setFabBackgroundColor(resources.getColor(R.color.PacificCoast))
                .setLabelBackgroundColor(resources.getColor(R.color.LightPink))
                .setLabel("Close game")
                .create()
        )
    }

    fun fabReset() {
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_reset, R.drawable.ic_reset_black)
                .setFabBackgroundColor(resources.getColor(R.color.PacificCoast))
                .setLabelBackgroundColor(resources.getColor(R.color.LightPink))
                .setLabel("Reset game")
                .create()
        )
    }


    override fun onPause() {
        super.onPause()

        if (displayedGame != FinishedMatch() && !saveOccured)
            dataMover.saveCurrentGame(context!!, displayedGame)
    }

    fun returnHome(savedGame: Boolean, deletedGame: Boolean) {
        fragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right)?.replace(
            R.id.container, HomeFragment(savedGame, deletedGame)
        )?.commit()
        changingTitle(getString(R.string.app_name), null)
        val navigationView = activity!!.findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(0).isChecked = true
    }
}
