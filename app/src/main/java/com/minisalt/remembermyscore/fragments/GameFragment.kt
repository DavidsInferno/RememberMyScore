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
import com.minisalt.remembermyscore.recyclerView.adapter.gameAdapters.GameAdapter
import com.minisalt.remembermyscore.recyclerView.adapter.gameAdapters.GameAdapter2
import com.minisalt.remembermyscore.recyclerView.adapter.gameAdapters.GameAdapter3
import kotlinx.android.synthetic.main.fragment_game.*
import java.util.*


class GameFragment(val homeData: HomeData? = null) : Fragment(R.layout.fragment_game) {

    private lateinit var displayedGame: FinishedMatch

    private val dataMover = DataMover()

    private var saveOccurred = false //prevents the game from being saved to cache

    lateinit var gameRules: ArrayList<GameRules>

    var diceClickable: Boolean = true

    var playingTo: String = ""

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
                    initToolbar(displayedGame.gamePlayed.name)
                    initFAB()
                } else
                    errorMessage("Problem loading home data at GameFragment")
            }
            existingGame != null -> {
                val indexOfRule = dataMover.getIndexOfRule(requireContext(), existingGame.gamePlayed.name)

                if (indexOfRule != -1) {
                    initRecyclerView(existingGame.players, gameRules[indexOfRule])

                    displayedGame = existingGame

                    initToolbar(displayedGame.gamePlayed.name)
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

    private fun initToolbar(title: String) {
        if (displayedGame.gamePlayed.diceRequired)
            toolbarNavIcon.visibility = View.VISIBLE

        materialToolbar.findViewById<View>(R.id.gameRound).visibility = View.GONE

        toolbarTitle.text = title

        toolBarButtons()
    }


    private fun freshGame(gameRule: GameRules, amountOfPlayers: Int) {
        displayedGame.players = arrayListOf()

        for (i in 0 until amountOfPlayers)
            displayedGame.players.add(PlayerData())

        displayedGame.round = 1
        displayedGame.addPlayerPositionsAndNames()
        displayedGame.addPlayerPoints()
        initRecyclerView(displayedGame.players, gameRule)
    }


    private fun initRecyclerView(playerList: ArrayList<PlayerData>, gameRule: GameRules) {

        if (gameRule.extraField_1_enabled && !gameRule.extraField_2_enabled) {

            recyclerViewPlayers.setHasFixedSize(true)
            val gameAdapter = GameAdapter2(
                playerList,
                requireContext(),
                gameRule
            )
            recyclerViewPlayers.adapter = gameAdapter

        } else if (gameRule.extraField_1_enabled && gameRule.extraField_2_enabled) {

            recyclerViewPlayers.setHasFixedSize(true)
            val gameAdapter = GameAdapter3(
                playerList,
                requireContext(),
                gameRule
            )
            recyclerViewPlayers.adapter = gameAdapter

        } else {

            recyclerViewPlayers.setHasFixedSize(true)
            val gameAdapter = GameAdapter(
                playerList,
                requireContext(),
                gameRule
            )
            recyclerViewPlayers.adapter = gameAdapter

        }
    }

    private fun toolBarButtons() {

        toolbarNavIcon.setOnClickListener {
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
                R.id.gameRound -> {
                    displayedGame.round++
                    toolbarTitle.text = playingTo + "   Round ${displayedGame.round}"
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

        toolbarForScoreboard(displayedGame.gamePlayed)
        overlayMargin()

        displayedGame.addPlayerPositionsAndNames()

        childFragmentManager.beginTransaction()
            .add(R.id.containerScoreboard, ScoreboardFragment(displayedGame.players, this, displayedGame.gamePlayed), "Scoreboard Fragment")
            .addToBackStack(null)
            .commit()
    }

    private fun toolbarForScoreboard(gamePlayed: GameRules) {
        materialToolbar.findViewById<View>(R.id.gameReset).visibility = View.GONE
        materialToolbar.findViewById<View>(R.id.gameDelete).visibility = View.GONE
        materialToolbar.findViewById<View>(R.id.gameSave).visibility = View.GONE
        toolbarNavIcon.visibility = View.GONE

        if (displayedGame.gamePlayed.roundCounter)
            materialToolbar.findViewById<View>(R.id.gameRound).visibility = View.VISIBLE



        playingTo = if (gamePlayed.extraField_1condition && !gamePlayed.extraField_2condition)
            "Playing to ${gamePlayed.pointsToWin}/${gamePlayed.extraField_1_pointsToWin}"
        else if (!gamePlayed.extraField_1condition && gamePlayed.extraField_2condition)
            "Playing to ${gamePlayed.pointsToWin}/_/${gamePlayed.extraField_2_pointsToWin}"
        else if (gamePlayed.extraField_1condition && gamePlayed.extraField_2condition)
            "Playing to ${gamePlayed.pointsToWin}/${gamePlayed.extraField_1_pointsToWin}/${gamePlayed.extraField_2_pointsToWin}"
        else
            "Playing to ${gamePlayed.pointsToWin}"

        if (gamePlayed.roundCounter)
            toolbarTitle.text = playingTo + "   Round ${displayedGame.round}"
        else
            toolbarTitle.text = playingTo
    }

    fun onCloseGameAdditions() {
        diceContainer.isClickable = false
        diceClickable = true

        containerScoreboard.isClickable = false

        scoreboardOverlay.isClickable = false
        scoreboardOverlay.visibility = View.INVISIBLE

        initToolbar(displayedGame.gamePlayed.name)

        materialToolbar.findViewById<View>(R.id.gameReset).visibility = View.VISIBLE
        materialToolbar.findViewById<View>(R.id.gameDelete).visibility = View.VISIBLE
        materialToolbar.findViewById<View>(R.id.gameSave).visibility = View.VISIBLE
        materialToolbar.findViewById<View>(R.id.gameRound).visibility = View.GONE

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
