package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialFadeThrough
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.*
import com.minisalt.remembermyscore.recyclerView.adapter.GameAdapter
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*


class GameFragment(val homeData: HomeData? = null) : Fragment(R.layout.fragment_game) {

    private lateinit var displayedGame: FinishedMatch

    private val dataMover = DataMover()

    private var saveOccurred = false //prevents the game from being saved to cache

    lateinit var gameRules: ArrayList<GameRules>

    var diceClickable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough.create()
        exitTransition = MaterialFadeThrough.create()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayedGame = FinishedMatch()

        val existingGame = dataMover.loadCurrentGame(requireContext())

        gameRules = dataMover.loadGameRules(requireContext())


        btnScoreboard.setOnClickListener {
            openScoreboard()
        }



        scoreboardOverlay.setOnClickListener {
            childFragmentManager.popBackStack()
            onCloseGameAdditions()
        }

        when {
            homeData != null -> {
                val indexOfRule = dataMover.getIndexOfRule(requireContext(), homeData.gameName)

                if (indexOfRule != -1) {
                    freshGame(gameRules[indexOfRule], homeData.players)

                    displayedGame = FinishedMatch(displayedGame.players, gameRules[indexOfRule])
                    initToolbar(displayedGame.gamePlayed.name, gameRules[indexOfRule].pointsToWin)
                    initFAB()
                } else
                    errorMessage("Problem loading home data at GameFragment")
            }
            existingGame != null -> {
                val indexOfRule = dataMover.getIndexOfRule(requireContext(), existingGame.gamePlayed.name)

                if (indexOfRule != -1) {
                    initRecyclerView(existingGame.players, gameRules[indexOfRule])

                    displayedGame = existingGame

                    initToolbar(displayedGame.gamePlayed.name, gameRules[indexOfRule].pointsToWin)
                    initFAB()
                } else {
                    txtNoGameActive.visibility = View.VISIBLE
                    materialToolbar.visibility = View.GONE
                    btnScoreboard.visibility = View.GONE
                    errorMessage("Corrupted save data at GameFragment")
                }
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

        if (displayedGame.gamePlayed.diceRequired)
            materialToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_casino_24px)

        if (points != null)
            materialToolbar.title = "$title ➞ $points"
        else
            materialToolbar.title = title

        toolBarButtons()
    }


    private fun freshGame(gameRule: GameRules, amountOfPlayers: Int) {
        displayedGame.players = arrayListOf()

        for (i in 0 until amountOfPlayers)
            displayedGame.players.add(PlayerData())

        displayedGame.addPlayerPositionsAndNames()
        initRecyclerView(displayedGame.players, gameRule)
    }

    private fun initRecyclerView(playerList: ArrayList<PlayerData>, gameRule: GameRules) {
        recyclerViewPlayers.setHasFixedSize(true)
        val gameAdapter = GameAdapter(playerList, requireContext(), gameRule)
        recyclerViewPlayers.adapter = gameAdapter
    }

    private fun toolBarButtons() {

        materialToolbar.setNavigationOnClickListener {
            if (diceClickable) {
                diceContainer.visibility = View.VISIBLE
                scoreboardOverlay.visibility = View.VISIBLE
                scoreboardOverlay.isClickable = true

                childFragmentManager.beginTransaction().add(R.id.diceContainer, DiceFragment(displayedGame.diceProperties))
                    .addToBackStack(null)
                    .commit()
                diceClickable = false
            } else {
                childFragmentManager.popBackStack()
                onCloseGameAdditions()
            }
        }

        materialToolbar.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.gameReset -> {
                    val indexOfRule = dataMover.getIndexOfRule(requireContext(), displayedGame.gamePlayed.name)
                    if (indexOfRule != -1) {
                        freshGame(gameRules[indexOfRule], displayedGame.players.size)
                        true
                    } else {
                        errorMessage("Problem resetting game at GameFragment")
                        false
                    }

                }
                R.id.gameDelete -> {
                    dataMover.deleteCurrentGame(requireContext())
                    saveOccurred = true
                    returnHome(savedGame = false)
                    true
                }
                R.id.gameSave -> {
                    displayedGame.datePlayed = Date()
                    displayedGame.addPlayerPositionsAndNames()

                    dataMover.appendToFinishedMatches(requireContext(), displayedGame)
                    saveOccurred = true

                    dataMover.deleteCurrentGame(requireContext())

                    returnHome(savedGame = true)
                    true
                }
                else -> false
            }
        }
    }

    override fun onPause() {
        super.onPause()

        if (displayedGame != FinishedMatch() && !saveOccurred)
            dataMover.saveCurrentGame(requireContext(), displayedGame)
    }

    private fun returnHome(savedGame: Boolean) {
        parentFragmentManager.beginTransaction().replace(R.id.container, HomeFragment(savedGame)).commit()
        val navigationView = requireActivity().findViewById(R.id.bottom_nav_view) as BottomNavigationView
        navigationView.menu.getItem(0).isChecked = true
    }

    private fun errorMessage(reason: String) {
        Toast.makeText(requireContext(), reason, Toast.LENGTH_SHORT).show()
    }

    private fun openScoreboard() {
        containerScoreboard.isClickable = true
        btnScoreboard.hide()
        scoreboardOverlay.isClickable = true
        scoreboardOverlay.visibility = View.VISIBLE

        overlayMargin()

        displayedGame.addPlayerPositionsAndNames()

        childFragmentManager.beginTransaction()
            .add(R.id.containerScoreboard, ScoreboardFragment(displayedGame.players, this), "Scoreboard Fragment")
            .addToBackStack(null)
            .commit()
    }

    fun onCloseGameAdditions() {
        diceContainer.isClickable = false
        diceClickable = true

        containerScoreboard.isClickable = false

        scoreboardOverlay.isClickable = false
        scoreboardOverlay.visibility = View.INVISIBLE

        Handler().postDelayed({ btnScoreboard.show() }, 300)

    }

    private fun overlayMargin() {
        val newLayoutParams = scoreboardOverlay.layoutParams as ConstraintLayout.LayoutParams

        if (l_materialToolbar.height - l_materialToolbar.bottom == 0)
            newLayoutParams.topMargin = l_materialToolbar.height
        else
            newLayoutParams.topMargin = 0

        scoreboardOverlay.layoutParams = newLayoutParams
    }
}
