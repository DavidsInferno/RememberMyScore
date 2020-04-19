package com.minisalt.remembermyscore.recyclerView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.GameRules
import com.minisalt.remembermyscore.recyclerView.clickListener.RecyclerViewClickInterface

class RulesAdapter(
    private val rulesList: ArrayList<GameRules>,
    val recyclerViewClickInterface: RecyclerViewClickInterface
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class GameRulesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTitle: TextView = itemView.findViewById(R.id.Title)
        var mPoints: TextView = itemView.findViewById(R.id.PointsToWin)
        var mGameCount: TextView = itemView.findViewById(R.id.GameCount)
        var mButtons: TextView = itemView.findViewById(R.id.Buttons)
        var mLastPlayed: TextView = itemView.findViewById(R.id.LastPlayed)

        //https://www.youtube.com/watch?v=AkiltTv0CjA
        init {
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    recyclerViewClickInterface.onItemClick(adapterPosition)
                }
            })

            itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(view: View): Boolean {
                    recyclerViewClickInterface.onLongItemClick(adapterPosition)
                    return true
                }
            })
        }
        //-------------------------------------------
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