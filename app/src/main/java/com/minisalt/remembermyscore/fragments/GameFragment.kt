package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.*
import com.minisalt.remembermyscore.recyclerView.adapter.GameAdapter
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*


class GameFragment(val homeData: HomeData? = null) : Fragment(R.layout.fragment_game) {

    private var displayedGame: FinishedMatch = FinishedMatch()

    private val dataMover = DataMover()

    private var saveOccured = false //prevents the game from being saved to cache

    lateinit var gameRules: ArrayList<GameRules>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val existingGame = dataMover.loadCurrentGame(requireContext())

        gameRules = dataMover.loadGameRules(requireContext())


        btnScoreboard.setOnClickListener {
            openScoreboard()
        }

        when {
            homeData != null -> {
                val indexOfRule = dataMover.getIndexOfRule(requireContext(), homeData.gameName)

                if (indexOfRule != -1) {
                    freshGame(gameRules, indexOfRule, homeData.players)

                    displayedGame = FinishedMatch(displayedGame.players, homeData.gameName)
                    initToolbar(displayedGame.gameTitle, gameRules[indexOfRule].pointsToWin)
                    initFAB()
                } else
                    errorMessage("Problem loading home data at GameFragment")
            }
            existingGame != null -> {
                val indexOfRule = dataMover.getIndexOfRule(requireContext(), existingGame.gameTitle)

                if (indexOfRule != -1) {
                    initRecyclerView(existingGame.players, gameRules[indexOfRule])


                    displayedGame = FinishedMatch(existingGame.players, existingGame.gameTitle)
                    initToolbar(existingGame.gameTitle, gameRules[indexOfRule].pointsToWin)
                    initFAB()
                } else
                    errorMessage("Problem loading existing data at GameFragment")


            }
            else -> {
                txtNoGameActive.visibility = View.VISIBLE
                materialToolbar.visibility = View.GONE
                btnScoreboard.visibility = View.GONE
            }
        }
    }

    private fun initFAB() {
        recyclerViewPlayers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerViewPlayers, dx, dy)
                if (dy > 0 && btnScoreboard.visibility == View.VISIBLE) {
                    btnScoreboard.hide()
                } else if (dy < 0 && btnScoreboard.visibility != View.VISIBLE) {
                    btnScoreboard.show()
                }
            }
        })
    }

    private fun initToolbar(title: String, points: Int?) {
        if (points != null)
            materialToolbar.title = "$title -> $points"
        else
            materialToolbar.title = title

        toolBarButtons()
    }


    private fun freshGame(gameRules: ArrayList<GameRules>, indexOfGame: Int, amountOfPlayers: Int) {
        displayedGame.players = arrayListOf()

        for (i in 0 until amountOfPlayers)
            displayedGame.players.add(PlayerData(playerName = "Player ${i + 1}"))

        println(displayedGame.players)

        initRecyclerView(displayedGame.players, gameRules[indexOfGame])
    }

    private fun initRecyclerView(playerList: ArrayList<PlayerData>, gameRule: GameRules) {
        recyclerViewPlayers.layoutManager = LinearLayoutManager(context)
        recyclerViewPlayers.setHasFixedSize(true)
        val gameAdapter = GameAdapter(playerList, requireContext(), gameRule)
        recyclerViewPlayers.adapter = gameAdapter
    }


    private fun toolBarButtons() {
        materialToolbar.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.gameReset -> {
                    val indexOfRule = dataMover.getIndexOfRule(requireContext(), displayedGame.gameTitle)
                    if (indexOfRule != -1) {
                        freshGame(gameRules, indexOfRule, displayedGame.players.size)
                        true
                    } else {
                        errorMessage("Problem resetting game at GameFragment")
                        false
                    }

                }
                R.id.gameDelete -> {
                    dataMover.deleteCurrentGame(requireContext())
                    saveOccured = true
                    returnHome(savedGame = false, deletedGame = true)
                    true
                }
                R.id.gameSave -> {
                    displayedGame.datePlayed = Date()
                    dataMover.appendToFinishedMatches(requireContext(), displayedGame)

                    saveOccured = true

                    dataMover.deleteCurrentGame(requireContext())

                    returnHome(savedGame = true, deletedGame = false)
                    true
                }
                else -> false

            }
        }
    }

    override fun onPause() {
        super.onPause()
        println("This is the current game. BWAH!")
        println(displayedGame)

        if (displayedGame != FinishedMatch() && !saveOccured)
            dataMover.saveCurrentGame(requireContext(), displayedGame)
    }

    private fun returnHome(savedGame: Boolean, deletedGame: Boolean) {
        fragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right)?.replace(
            R.id.container, HomeFragment(savedGame, deletedGame)
        )?.commit()
        val navigationView = requireActivity().findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(0).isChecked = true
    }

    private fun errorMessage(reason: String) {
        Toast.makeText(
            requireContext(), reason, Toast
                .LENGTH_SHORT
        )
            .show()
    }

    fun openScoreboard() {
        fragmentManager?.beginTransaction()?.add(
            R.id.containerScoreboard, ScoreboardFragment(displayedGame.players)
        )?.commit()
    }
}
