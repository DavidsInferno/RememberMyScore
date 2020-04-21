package com.minisalt.remembermyscore.recyclerView.adapter

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R

class NestedButtonRecyclerViewAdapter(
    val context: Context, val buttonList: ArrayList<Int>, val
    mScore: EditText
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mButton: Button = itemView.findViewById(R.id.nestedButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ButtonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.button_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return buttonList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ButtonViewHolder -> {
                if (buttonList[position] > 0)
                    holder.mButton.text = "+${buttonList[position]}"
                else
                    holder.mButton.text = buttonList[position].toString()


                holder.mButton.setOnClickListener {
                    var score: Int = Integer.parseInt(mScore.text.toString())
                    score += Integer.parseInt(holder.mButton.text.toString())
                    mScore.text = Editable.Factory.getInstance().newEditable(score.toString())
                }
            }
        }
    }
}