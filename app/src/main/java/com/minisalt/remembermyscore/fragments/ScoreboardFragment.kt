package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.PlayerData
import com.minisalt.remembermyscore.recyclerView.adapter.ScoreboardRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_scoreboard.*


class ScoreboardFragment(var players: ArrayList<PlayerData>, val gameFragment: GameFragment) : Fragment(R.layout.fragment_scoreboard) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis.create(MaterialSharedAxis.Y, true)
        exitTransition = MaterialSharedAxis.create(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initRecyclerView(players)

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
        parentFragmentManager.beginTransaction().remove(this).commit()
        gameFragment.onCloseGameAdditions()
    }

}
