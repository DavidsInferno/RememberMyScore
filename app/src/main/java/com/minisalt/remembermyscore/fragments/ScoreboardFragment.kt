package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.PlayerData
import com.minisalt.remembermyscore.recyclerView.adapter.ScoreboardRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_scoreboard.*


class ScoreboardFragment(var players: ArrayList<PlayerData>) : Fragment(R.layout.fragment_scoreboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragm: ScoreboardFragment? =
            requireFragmentManager().findFragmentByTag("Scoreboard Fragment") as ScoreboardFragment?
        fragm?.exitTransition = Slide()

        val sortedList: List<PlayerData> = players.sortedWith((compareByDescending { it.playerPoints }))

        for (i in sortedList.indices)
            sortedList[i].playerPosition = i + 1

        initRecyclerView(sortedList)

        btnCloseScoreboard.setOnClickListener {
            closeScoreboard()
        }
    }


    private fun initRecyclerView(players: List<PlayerData>) {
        recyclerScoreboard.setHasFixedSize(true)
        val scoreBoardAdapter = ScoreboardRecyclerViewAdapter(requireContext(), players)
        recyclerScoreboard.adapter = scoreBoardAdapter
    }

    fun closeScoreboard() {
        fragmentManager?.beginTransaction()?.remove(this)?.commit()

        val fragm: GameFragment? =
            requireFragmentManager().findFragmentByTag("Game Fragment") as GameFragment?

        fragm?.onCloseScoreboard()
    }


    override fun onPause() {
        super.onPause()
        val fragm: GameFragment? =
            requireFragmentManager().findFragmentByTag("Game Fragment") as GameFragment?

        fragm?.onCloseScoreboard()
    }
}
