package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.data.PlayerData

class ScoreboardRecyclerViewAdapter(
    val context: Context,
    val players: List<PlayerData>,
    val gamePlayed: GameRules
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPosition: TextView = itemView.findViewById(R.id.playerPosition)
        val mPlayerName: TextView = itemView.findViewById(R.id.playerName)
        val mPlayerScore: TextView = itemView.findViewById(R.id.playerPoints)


        val mPlayerScore2: TextView = itemView.findViewById(R.id.playerPoints2)
        val divider2: View = itemView.findViewById(R.id.divider2)
        val mPlayerScore3: TextView = itemView.findViewById(R.id.playerPoints3)
        val divider3: View = itemView.findViewById(R.id.divider3)
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

                if (gamePlayed.extraField_1_enabled) {
                    holder.mPlayerScore2.text = players[position].playerPoints2.toString()
                    holder.mPlayerScore2.visibility = View.VISIBLE
                    holder.divider2.visibility = View.VISIBLE


                    if (gamePlayed.extraField_2_enabled) {
                        holder.mPlayerScore3.text = players[position].playerPoints3.toString()
                        holder.mPlayerScore3.visibility = View.VISIBLE
                        holder.divider3.visibility = View.VISIBLE
                    } else {
                        holder.mPlayerScore3.visibility = View.GONE
                        holder.divider3.visibility = View.GONE
                    }

                } else {
                    holder.mPlayerScore2.visibility = View.GONE
                    holder.divider2.visibility = View.GONE

                    holder.mPlayerScore3.visibility = View.GONE
                    holder.divider3.visibility = View.GONE
                }

            }
        }
    }
}
