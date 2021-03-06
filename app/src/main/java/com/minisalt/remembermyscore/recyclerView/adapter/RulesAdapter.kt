package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.GameRules
import java.text.SimpleDateFormat
import java.util.*

class RulesAdapter(
    private val rulesList: ArrayList<GameRules>, val context:
    Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataMover = DataMover()


    inner class GameRulesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTitle: TextView = itemView.findViewById(R.id.Title)
        var mPoints: TextView = itemView.findViewById(R.id.PointsToWin)
        var mGameCount: TextView = itemView.findViewById(R.id.GameCount)
        var mButtons: TextView = itemView.findViewById(R.id.Buttons)
        var mLastPlayed: TextView = itemView.findViewById(R.id.LastPlayed)
        var mDiceRequired: TextView = itemView.findViewById(R.id.DiceNeeded)
        var mExtraFields: TextView = itemView.findViewById(R.id.extraField_1_winPoints)
        var mRoundCounter: TextView = itemView.findViewById(R.id.roundCounter)

        val mCard: CardView = itemView.findViewById(R.id.RuleCard)

        init {
            mCard.setOnClickListener {
                Toast.makeText(context, "Swipe right to edit, swipe left to delete!", Toast.LENGTH_LONG).show()
            }
        }
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
                holder.mPoints.text = "Playing to " + rulesList[position].pointsToWin.toString()
                holder.mGameCount.text = "Games played: " + dataMover.numberOfGamesPlayed(context, rulesList[position].name).toString()

                if (rulesList[position].lowestPointsWin)
                    holder.mPoints.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0)

                if (rulesList[position].roundCounter)
                    holder.mRoundCounter.visibility = View.VISIBLE
                else
                    holder.mRoundCounter.visibility = View.GONE




                if (!rulesList[position].diceRequired)
                    holder.mDiceRequired.visibility = View.GONE


                checkingForExtraFields(holder, rulesList[position])


                if (rulesList[position].buttons.size != 0)
                    holder.mButtons.text = "Buttons: " + rulesList[position].buttons.toString()
                else
                    holder.mButtons.text = "Buttons: none"


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

    private fun checkingForExtraFields(holder: GameRulesViewHolder, gameRule: GameRules) {
        if (gameRule.extraField_1_enabled) {

            if (gameRule.extraField_2_enabled)
                holder.mExtraFields.text = "Extra field x2 "
            else
                holder.mExtraFields.text = "Extra field "

        } else
            holder.mExtraFields.visibility = View.GONE

    }
}