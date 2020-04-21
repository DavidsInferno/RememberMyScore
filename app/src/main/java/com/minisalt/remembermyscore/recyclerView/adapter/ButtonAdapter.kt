package com.minisalt.remembermyscore.recyclerView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R

class ButtonAdapter(val itemList: ArrayList<Int>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ExampleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExampleViewHolder ->
                holder.mTextView.text = itemList.get(position).toString()
        }
    }

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextView: TextView = itemView.findViewById(R.id.list_item)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        itemList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
    }
}