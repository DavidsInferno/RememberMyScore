package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.recyclerView.clickListener.RecyclerViewClickInterface
import java.text.SimpleDateFormat
import java.util.*

class RulesAdapter(
    private val rulesList: ArrayList<GameRules>, val recyclerViewClickInterface: RecyclerViewClickInterface, val context:
    Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataMover = DataMover()


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

        val date = dataMover.dateOfLastGame(context, rulesList[position].name)


        when (holder) {
            is GameRulesViewHolder -> {
                holder.mTitle.text = rulesList[position].name
                holder.mPoints.text = rulesList[position].pointsToWin.toString()
                holder.mGameCount.text = dataMover.numberOfGamesPlayed(context, rulesList[position].name).toString()
                holder.mButtons.text = rulesList[position].buttons.toString()
                if (date != null) {
                    holder.mLastPlayed.text = dateParser(date)
                } else
                    holder.mLastPlayed.text = "Not played yet"
            }
        }
    }

    private fun dateParser(date: Date): String {
        return SimpleDateFormat("dd-MM-yy HH:mm").format(date)
    }

}