package com.minisalt.remembermyscore.recyclerView.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.FinishedMatch
import java.text.SimpleDateFormat
import java.util.*


class ScoresRecyclerviewAdapter(val finishedMatch: ArrayList<FinishedMatch>, val context: Context) : RecyclerView
.Adapter<RecyclerView.ViewHolder>() {

    val collapsedColor = ContextCompat.getColor(context, R.color.list_item_bg_collapsed)
    val expandedColor = ContextCompat.getColor(context, R.color.list_item_bg_expanded)

    var previouslyExpanded: Int = -1

    inner class ScoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mGameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val mDatePlayed: TextView = itemView.findViewById(R.id.datePlayed)
        val mPosition: TextView = itemView.findViewById(R.id.position)
        val mUsername: TextView = itemView.findViewById(R.id.username)
        val mPointsToWin: TextView = itemView.findViewById(R.id.pointsToWin)
        val mCardView: CardView = itemView.findViewById(R.id.ScoresRow)
        val mArrow: ImageView = itemView.findViewById(R.id.chevron)


        val mRecyclerView = itemView.findViewById<RecyclerView>(R.id.scoreboardRecyclerView)

        val expandableLayout: ConstraintLayout = itemView.findViewById(R.id.expandable_layout)


        init {
            mCardView.setOnClickListener {
                val match = finishedMatch[adapterPosition]
                match.expanded = !match.expanded

                if (previouslyExpanded != -1 && previouslyExpanded != adapterPosition)
                    closeOtherScore(previouslyExpanded)

                notifyItemChanged(adapterPosition)
                previouslyExpanded = adapterPosition
            }
        }
    }

    fun closeOtherScore(previouslyExpanded: Int) {
        if (finishedMatch[previouslyExpanded].expanded) {
            finishedMatch[previouslyExpanded].expanded = !finishedMatch[previouslyExpanded].expanded
            notifyItemChanged(previouslyExpanded)
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
                holder.mPosition.text = "Rank"
                holder.mUsername.text = "User name"
                holder.mPointsToWin.text = "Score"


                if (finishedMatch[position].expanded) {
                    expandItem(holder)
                } else
                    collapseItem(holder)


                val nestedPlayerScoreboard = ScoreboardRecyclerViewAdapter(context, finishedMatch[position].players)
                initRecyclerView(holder.mRecyclerView, nestedPlayerScoreboard)
            }
        }
    }

    private fun expandItem(holder: ScoresViewHolder) {
        holder.expandableLayout.visibility = View.VISIBLE
        holder.mCardView.backgroundTintList = ColorStateList.valueOf(expandedColor)

        val newLayoutParams = holder.mCardView.layoutParams as RecyclerView.LayoutParams

        newLayoutParams.setMargins(30, 15, 30, 15)
        holder.mCardView.layoutParams = newLayoutParams

        ObjectAnimator.ofFloat(holder.mArrow, View.ROTATION, 0f, 90f).setDuration(300).start()
    }

    private fun collapseItem(holder: ScoresViewHolder) {
        holder.expandableLayout.visibility = View.GONE
        holder.mCardView.backgroundTintList = ColorStateList.valueOf(collapsedColor)

        val newLayoutParams = holder.mCardView.layoutParams as RecyclerView.LayoutParams


        //I know in the layout it says 16 but when checking what newLayoutParams spat it out says its 56
        newLayoutParams.setMargins(56, 35, 56, 35)
        holder.mCardView.layoutParams = newLayoutParams




        ObjectAnimator.ofFloat(holder.mArrow, View.ROTATION, 0f, -90f).setDuration(300).start()
    }

    private fun dateParser(date: Date): String {
        return SimpleDateFormat("dd-MM-yy HH:mm").format(date)
    }

    private fun initRecyclerView(nestedRecyclerView: RecyclerView, nestedPlayerScoreboard: ScoreboardRecyclerViewAdapter) {
        nestedRecyclerView.setHasFixedSize(true);
        nestedRecyclerView.adapter = nestedPlayerScoreboard
    }
}