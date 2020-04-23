package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.GameRules
import com.minisalt.remembermyscore.preferences.PlayerData

class GameAdapter(
    val playerList: ArrayList<PlayerData>, val context: Context, val gameRule: GameRules
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class PlayDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mName: TextInputEditText = itemView.findViewById(R.id.nameInput)
        var mPoints: EditText = itemView.findViewById(R.id.inputScore)
        var nestedRecyclerView: RecyclerView = itemView.findViewById(R.id.rvNestedButtons)
        var mScore: EditText = itemView.findViewById(R.id.inputScore)

        // To allow the use od EditText changing in recyclerview
        init {

            mName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
                ) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (mName.text.toString() != "")
                        playerList[adapterPosition].playerName = mName.text.toString()
                }

                override fun afterTextChanged(editable: Editable) {
                }
            })

            mPoints.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence, i: Int, i1: Int, i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (mPoints.text.toString() != "") {
                        if (mPoints.text.length < 10)
                            playerList[adapterPosition].playerPoints =
                                Integer.parseInt(mPoints.text.toString())
                        else
                            playerList[adapterPosition].playerPoints = 0
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    println("TEXT HAS BEEN CHANGED")

                }
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
                    context,
                    gameRule.buttons, holder.mScore
                )

                initRecyclerView(holder.nestedRecyclerView, nestedButtonRecyclerViewAdapter)


            }
        }
    }

    fun initRecyclerView(
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