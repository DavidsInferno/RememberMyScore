package com.minisalt.remembermyscore.recyclerView.adapter

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.data.PlayerData
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


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
            mName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus)
                    nameChecking()
            }

            mName.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == IME_ACTION_DONE) {
                    nameChecking()
                    UIUtil.hideKeyboard(context, mName)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }


            mPoints.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus)
                    pointChecking()
            }

            mPoints.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == IME_ACTION_DONE) {
                    pointChecking()
                    UIUtil.hideKeyboard(context, mPoints)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            KeyboardVisibilityEvent.setEventListener(getActivity(context)!!, object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (!isOpen) {
                        if (adapterPosition != -1) {
                            nameChecking()
                            pointChecking()
                        } else
                            Toast.makeText(context, "The adapter position was just -1", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        private fun getActivity(context: Context): Activity? {
            if (context is ContextWrapper) {
                return if (context is Activity) {
                    context
                } else {
                    getActivity((context).baseContext)
                }
            }
            return null
        }
        //------------------------------------------------


        private fun nameChecking() {
            if (mName.text.toString() != "")
                playerList[adapterPosition].playerName = mName.text.toString()
        }

        private fun pointChecking() {
            if (mPoints.text.toString() != "") {
                if (mPoints.text.length < 10) {
                    playerList[adapterPosition].playerPoints = Integer.parseInt(mPoints.text.toString())
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




                initRecyclerView(holder.nestedRecyclerView, holder.mPoints, position)
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
        mPoints: EditText,
        position: Int
    ) {
        val nestedButtonRecyclerViewAdapter =
            NestedButtonRecyclerViewAdapter(context, gameRule, mPoints, playerList[position])
        nestedRecyclerView.setHasFixedSize(true);
        nestedRecyclerView.adapter = nestedButtonRecyclerViewAdapter
    }
}