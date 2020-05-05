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
import java.util.*


class ScoresFragment : Fragment(R.layout.fragment_scores) {

    val dataMover = DataMover()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough.create()
        exitTransition = MaterialFadeThrough.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finishedMatches = dataMover.loadAllMatches(requireContext())

        initRecyclerView(finishedMatches)


    }

    private fun initRecyclerView(allMatches: ArrayList<FinishedMatch>) {

        recyclerViewScores.setHasFixedSize(true)
        val scoresAdapter = ScoresRecyclerviewAdapter(allMatches, requireContext())
        recyclerViewScores.adapter = scoresAdapter
    }
}
