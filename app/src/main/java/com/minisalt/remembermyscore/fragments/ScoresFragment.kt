package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.FinishedMatch
import com.minisalt.remembermyscore.recyclerView.adapter.ScoresRecyclerviewAdapter
import kotlinx.android.synthetic.main.fragment_scores.*


class ScoresFragment : Fragment(R.layout.fragment_scores) {

    private val dataMover = DataMover()
    private var amountOfGames = 0
    lateinit var finishedMatches: ArrayList<FinishedMatch>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough.create()
        exitTransition = MaterialFadeThrough.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finishedMatches = dataMover.loadAllMatches(requireContext())
        amountOfGames = finishedMatches.size


        initRecyclerView(finishedMatches)

        if (finishedMatches.size == 0)
            txtNoGameActive.visibility = View.VISIBLE
        else
            txtNoGameActive.visibility = View.GONE

    }

    private fun initRecyclerView(allMatches: ArrayList<FinishedMatch>) {

        recyclerViewScores.setHasFixedSize(true)
        val scoresAdapter = ScoresRecyclerviewAdapter(allMatches, requireContext())
        recyclerViewScores.adapter = scoresAdapter
    }
    override fun onPause() {
        super.onPause()
        for (i in finishedMatches) {
            if (i.expanded)
                i.expanded = false
        }
        if (amountOfGames != finishedMatches.size)
            dataMover.saveMatches(requireContext(), finishedMatches)
    }
}
