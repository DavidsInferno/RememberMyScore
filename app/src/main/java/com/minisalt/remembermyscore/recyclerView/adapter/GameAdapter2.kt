package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.data.PlayerData


class GameAdapter2(val playerList: ArrayList<PlayerData>, val context: Context, val gameRule: GameRules) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val hasButtons = gameRule.buttons.size != 0

    inner class PlayDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mName: TextInputEditText = itemView.findViewById(R.id.nameInput)
        var mPoints1: EditText = itemView.findViewById(R.id.inputScore)
        var mRadio1: RadioButton = itemView.findViewById(R.id.firstPoint)

        var mPoints2: EditText = itemView.findViewById(R.id.inputScore2)
        var mRadio2: RadioButton = itemView.findViewById(R.id.secondPoint)

        var nestedRecyclerView: RecyclerView = itemView.findViewById(R.id.rvNestedButtons)

        // To allow the use od EditText changing in recyclerview
        init {
            mRadio1.setOnClickListener {
                mRadio1.isChecked = true
                mRadio2.isChecked = false
            }

            mRadio2.setOnClickListener {
                mRadio1.isChecked = false
                mRadio2.isChecked = true
            }

            mName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    nameChecking()
                }
            })

            mPoints1.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    pointChecking(mPoints1, 1)
                }
            })

            mPoints2.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    pointChecking(mPoints2, 2)
                }
            })


        }

        private fun nameChecking() {
            if (mName.text.toString() != "")
                playerList[adapterPosition].playerName = mName.text.toString()
        }

        private fun pointChecking(mPoints: EditText, i: Int) {
            if (mPoints.text.toString() != "") {
                if (mPoints.text.length < 10) {
                    if (i == 1) {
                        playerList[adapterPosition].playerPoints = Integer.parseInt(mPoints.text.toString())
                        checkWinner(playerList[adapterPosition].playerPoints, gameRule.pointsToWin, adapterPosition)
                    } else {
                        playerList[adapterPosition].playerPoints2 = Integer.parseInt(mPoints.text.toString())
                        if (gameRule.extraField_1condition) {
                            checkWinner(playerList[adapterPosition].playerPoints2, gameRule.extraField_1text!!, adapterPosition)
                        }
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlayDataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.game_row_two, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayDataViewHolder -> {
                holder.mName.text = Editable.Factory.getInstance().newEditable(playerList[position].playerName)
                holder.mPoints1.text = Editable.Factory.getInstance().newEditable(playerList[position].playerPoints.toString())

                holder.mPoints2.text = Editable.Factory.getInstance().newEditable(playerList[position].playerPoints2.toString())



                if (hasButtons)
                    initRecyclerView(holder.nestedRecyclerView, holder.mPoints1, holder.mPoints2, holder.mRadio1, position)
                else
                    holder.nestedRecyclerView.visibility = View.GONE
            }
        }
    }

    fun checkWinner(playerPoints: Int, pointsToWin: Int, adapterPosition: Int) {
        if (playerPoints >= pointsToWin) {
            MaterialAlertDialogBuilder(context)
                .setTitle("${playerList[adapterPosition].playerName} has won the game!")
                .setMessage("On dialog close, you can resume the game, or start another")
                .setPositiveButton("Awesome!") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun initRecyclerView(
        nestedRecyclerView: RecyclerView,
        mPoints1: EditText,
        mPoints2: EditText,
        mRadio1: RadioButton,
        position: Int
    ) {
        val nestedButtonRecyclerViewAdapter =
            NestedButtonRecyclerViewAdapter2(context, gameRule, mPoints1, mPoints2, mRadio1, playerList[position])
        nestedRecyclerView.setHasFixedSize(true)
        nestedRecyclerView.adapter = nestedButtonRecyclerViewAdapter
    }
}