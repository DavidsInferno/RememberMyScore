package com.minisalt.remembermyscore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R

class RecycleButtonAdapter(val itemList: ArrayList<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mExample: ArrayList<Int> = itemList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ExampleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return mExample.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ExampleViewHolder->
                holder.mTextView.text=mExample.get(position).toString()
        }
    }

    public class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextView: TextView = itemView.findViewById(R.id.list_item)
    }


}