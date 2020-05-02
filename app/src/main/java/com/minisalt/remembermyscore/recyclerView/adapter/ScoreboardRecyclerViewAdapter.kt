package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.PlayerData

class ScoreboardRecyclerViewAdapter(val context: Context, val players: List<PlayerData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPosition: TextView = itemView.findViewById(R.id.playerPosition)
        val mPlayerName: TextView = itemView.findViewById(R.id.playerName)
        val mPlayerScore: TextView = itemView.findViewById(R.id.playerPoints)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.scoreboard_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayerViewHolder -> {
                holder.mPosition.text = players[position].playerPosition.toString()
                holder.mPlayerName.text = players[position].playerName
                holder.mPlayerScore.text = players[position].playerPoints.toString()
            }
        }
    }
}
