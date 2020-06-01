package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.data.PlayerData


class GameAdapter(
    val playerList: ArrayList<PlayerData>, val context: Context, val gameRule: GameRules
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val hasButtons = gameRule.buttons.size != 0

    inner class PlayDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mName: TextInputEditText = itemView.findViewById(R.id.nameInput)
        var mPoints: EditText = itemView.findViewById(R.id.inputScore)
        var nestedRecyclerView: RecyclerView = itemView.findViewById(R.id.rvNestedButtons)


        init {
            mName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    nameChecking()
                }
            })

            mPoints.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    pointChecking()
                }
            })
        }

        private fun nameChecking() {
            if (mName.text.toString() != "")
                playerList[adapterPosition].playerName = mName.text.toString()
        }

        private fun pointChecking() {
            if (mPoints.text.toString() != "") {
                if (mPoints.text.length < 10) {
                    playerList[adapterPosition].playerPoints = Integer.parseInt(mPoints.text.toString())
                    if (!gameRule.lowestPointsWin)
                        checkWinner(playerList[adapterPosition].playerPoints, gameRule.pointsToWin, adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlayDataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.game_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayDataViewHolder -> {
                holder.mName.text =
                    Editable.Factory.getInstance().newEditable(playerList[position].playerName)
                holder.mPoints.text = Editable.Factory.getInstance()
                    .newEditable(playerList[position].playerPoints.toString())

                if (hasButtons)
                    initRecyclerView(holder.nestedRecyclerView, holder.mPoints, position)
                else
                    holder.nestedRecyclerView.visibility = View.GONE
            }
        }
    }

    fun checkWinner(playerPoints: Int, pointsToWin: Int, adapterPosition: Int) {
        //if it feels bad just place the materialDialogBuilder in each of the inner if-s and remove the negation
        if (!gameRule.lowestPointsWin) {
            if (!(playerPoints >= pointsToWin))
                return
        } else
            if (!(playerPoints <= pointsToWin))
                return


        MaterialAlertDialogBuilder(context)
            .setTitle("${playerList[adapterPosition].playerName} has won the game!")
            .setMessage("On dialog close, you can resume the game, or start another")
            .setPositiveButton("Awesome!") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun initRecyclerView(
        nestedRecyclerView: RecyclerView,
        mPoints: EditText,
        position: Int
    ) {
        val nestedButtonRecyclerViewAdapter =
            NestedButtonRecyclerViewAdapter(context, gameRule, mPoints, playerList[position])
        nestedRecyclerView.setHasFixedSize(true);
        nestedRecyclerView.adapter = nestedButtonRecyclerViewAdapter
    }
}