package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.*
import com.minisalt.remembermyscore.recyclerView.adapter.GameAdapter
import kotlinx.android.synthetic.main.fragment_game.*


class GameFragment(val homeData: HomeData? = null) :
    Fragment(R.layout.fragment_game) {

    lateinit var gameAdapter: GameAdapter

    var displayedGame: FinishedMatch = FinishedMatch()


    val dataMover = DataMover()

    var playerList: ArrayList<PlayerData> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabCustomization()

        val gameRules = dataMover.loadGameRules(context!!)

        val existingGame = dataMover.loadCurrentGame(context!!)



        when {
            homeData != null -> {
                freshGame(gameRules)
                displayedGame = FinishedMatch(playerList, homeData.gameName)

            }
            existingGame != null -> {
                initRecyclerView(
                    existingGame.players, gameRules[dataMover.getIndexOfRule
                        (context!!, existingGame.gameTitle)].buttons
                )
                displayedGame = FinishedMatch(existingGame.players, existingGame.gameTitle)
            }
            else -> {
                txtNoGameActive.visibility = View.VISIBLE
            }
        }
    }


    private fun freshGame(gameRules: ArrayList<GameRules>) {
        for (i in 0 until homeData!!.players)
            playerList.add(PlayerData(playerName = "Player $i"))

        val indexOfGame = dataMover.getIndexOfRule(context!!, homeData.gameName)

        initRecyclerView(playerList, gameRules[indexOfGame].buttons)
    }

    private fun initRecyclerView(playerList: ArrayList<PlayerData>, arrayList: ArrayList<Int>) {
        recyclerViewPlayers.layoutManager = LinearLayoutManager(context)
        recyclerViewPlayers.setHasFixedSize(true)
        gameAdapter =
            GameAdapter(
                playerList, context!!, arrayList
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
                    dataMover

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
                R.id.fab_close_game -> {

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
                R.id.fab_reset -> {

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
                .setLabel("Save game")
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

        if (displayedGame != FinishedMatch())
            dataMover.saveCurrentGame(context!!, displayedGame)
    }
}
