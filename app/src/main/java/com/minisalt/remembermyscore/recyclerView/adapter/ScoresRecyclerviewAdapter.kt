package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.FinishedMatch
import java.text.SimpleDateFormat
import java.util.*

class ScoresRecyclerviewAdapter(val finishedMatch: ArrayList<FinishedMatch>, val context: Context) : RecyclerView
.Adapter<RecyclerView
.ViewHolder>
    () {

    inner class ScoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mGameTitle = itemView.findViewById<TextView>(R.id.gameTitle)
        val mDatePlayed = itemView.findViewById<TextView>(R.id.datePlayed)
        val mPosition = itemView.findViewById<TextView>(R.id.position)
        val mUsername = itemView.findViewById<TextView>(R.id.username)
        val mPointsToWin = itemView.findViewById<TextView>(R.id.pointsToWin)
        val mCardView = itemView.findViewById<CardView>(R.id.ScoresRow)

        val mRecyclerView = itemView.findViewById<RecyclerView>(R.id.scoreboardRecyclerView)

        val expandableLayout: ConstraintLayout = itemView.findViewById(R.id.expandable_layout)


        init {
            mCardView.setOnClickListener {
                val match = finishedMatch[adapterPosition]
                match.expanded = !match.expanded

                notifyItemChanged(adapterPosition)

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ScoresViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.scores_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return finishedMatch.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScoresViewHolder -> {

                val gameTitleText = finishedMatch[position].gamePlayed.name + " -> " + finishedMatch[position]
                    .gamePlayed.pointsToWin.toString()

                holder.mGameTitle.text = gameTitleText
                holder.mDatePlayed.text = "Played: " + dateParser(finishedMatch[position].datePlayed)
                holder.mPosition.text = "Player Position"
                holder.mUsername.text = "User name"
                holder.mPointsToWin.text = "Points to win"


                if (finishedMatch[position].expanded) {
                    holder.expandableLayout.visibility = View.VISIBLE
                } else
                    holder.expandableLayout.visibility = View.GONE


                val nestedPlayerScoreboard = ScoreboardRecyclerViewAdapter(
                    context, finishedMatch[position].players
                )

                initRecyclerView(holder.mRecyclerView, nestedPlayerScoreboard)
            }
        }
    }

    private fun dateParser(date: Date): String {
        return SimpleDateFormat("dd-MM-yy HH:mm").format(date)
    }

    private fun initRecyclerView(
        nestedRecyclerView: RecyclerView,
        nestedPlayerScoreboard: ScoreboardRecyclerViewAdapter
    ) {
        nestedRecyclerView.setHasFixedSize(true)
        nestedRecyclerView.adapter = nestedPlayerScoreboard
    }

}