package com.minisalt.remembermyscore.recyclerView.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.IntroSlide

class IntroSlideAdapter(private val introSlide: List<IntroSlide>) : RecyclerView.Adapter<IntroSlideAdapter.IntroSlideViewHolder>() {

    inner class IntroSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textTitle: TextView = view.findViewById(R.id.textTitle)
        private val textDescription: TextView = view.findViewById(R.id.textDescription)
        private val ImageIcon: ImageView = view.findViewById(R.id.imageSlideIcon)

        fun bind(introSlide: IntroSlide) {
            textTitle.text = introSlide.title
            textDescription.text = introSlide.description
            ImageIcon.setImageResource(introSlide.icon)
        }
    }

    override fun getItemCount(): Int {
        return introSlide.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder((LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container, parent, false)))
    }


    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
        holder.bind(introSlide[position])
    }
}