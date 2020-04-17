package com.minisalt.remembermyscore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.GameRules

class RulesAdapter(val rulesList: ArrayList<GameRules>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class GameRulesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTitle: TextView = itemView.findViewById(R.id.Title)
        var mPoints: TextView = itemView.findViewById(R.id.PointsToWin)
        var mGameCount: TextView = itemView.findViewById(R.id.GameCount)
        var mButtons: TextView = itemView.findViewById(R.id.Buttons)
        var mLastPlayed: TextView = itemView.findViewById(R.id.LastPlayed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GameRulesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rule_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return rulesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GameRulesViewHolder -> {
                holder.mTitle.text = rulesList[position].name
                holder.mPoints.text = rulesList[position].pointsToWin.toString()
                holder.mGameCount.text = rulesList[position].gamesPlayed.toString()
                holder.mButtons.text = rulesList[position].buttons.toString()
                holder.mLastPlayed.text = "Datum"
            }
        }
    }

}