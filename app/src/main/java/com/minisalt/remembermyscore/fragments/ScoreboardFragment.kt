package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.PlayerData
import com.minisalt.remembermyscore.recyclerView.adapter.ScoreboardRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_scoreboard.*

class ScoreboardFragment(var players: ArrayList<PlayerData>) : Fragment(R.layout.fragment_scoreboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sortedList: List<PlayerData> = players.sortedWith((compareBy { it.playerPoints }))

        for (i in sortedList.indices)
            sortedList[i].playerPosition = i + 1

        println(sortedList)

        initRecyclerView(sortedList)
    }


    private fun initRecyclerView(players: List<PlayerData>) {
        recyclerScoreboard.layoutManager = LinearLayoutManager(context)
        recyclerScoreboard.setHasFixedSize(true)
        val scoreBoardAdapter = ScoreboardRecyclerViewAdapter(requireContext(), players)
        recyclerScoreboard.adapter = scoreBoardAdapter
    }
}
