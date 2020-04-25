package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.data.PlayerData

class GameAdapter(
    val playerList: ArrayList<PlayerData>, val context: Context, val gameRule: GameRules
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class PlayDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mName: TextInputEditText = itemView.findViewById(R.id.nameInput)
        var mPoints: EditText = itemView.findViewById(R.id.inputScore)
        var nestedRecyclerView: RecyclerView = itemView.findViewById(R.id.rvNestedButtons)

        // To allow the use od EditText changing in recyclerview
        init {

            mName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    if (mName.text.toString() != "")
                        playerList[adapterPosition].playerName = mName.text.toString()
                    return@OnKeyListener true
                }
                false
            })

            mPoints.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    if (mPoints.text.toString() != "") {
                        if (mPoints.text.length < 10) {
                            playerList[adapterPosition].playerPoints = Integer.parseInt(mPoints.text.toString())
                            checkWinner(playerList[adapterPosition].playerPoints, gameRule.pointsToWin)

                        }
                    }
                    return@OnKeyListener true
                }
                false
            })
        }
        //------------------------------------------------

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

                val nestedButtonRecyclerViewAdapter = NestedButtonRecyclerViewAdapter(
                    context, gameRule, holder.mPoints,
                    playerList[position]
                )

                initRecyclerView(holder.nestedRecyclerView, nestedButtonRecyclerViewAdapter)
            }
        }
    }

    fun checkWinner(playerPoints: Int, pointsToWin: Int) {
        if (playerPoints >= pointsToWin) {
            Toast.makeText(context, "We have a winner!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView(
        nestedRecyclerView: RecyclerView,
        nestedButtonRecyclerViewAdapter: NestedButtonRecyclerViewAdapter
    ) {

        nestedRecyclerView.setHasFixedSize(true)
        nestedRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        nestedRecyclerView.adapter = nestedButtonRecyclerViewAdapter
    }
}