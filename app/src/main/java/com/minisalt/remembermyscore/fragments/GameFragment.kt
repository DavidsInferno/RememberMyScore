package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.minisalt.remembermyscore.R
import kotlinx.android.synthetic.main.fragment_game.*


class GameFragment : Fragment(R.layout.fragment_game) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabCustomization()
    }


    fun fabCustomization() {
        fabMain()
        fabSave()
        fabClose()
        fabReset()


        speedDial.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_save_game -> {

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
                R.id.fab_close_game -> {

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
                R.id.fab_reset -> {

                    speedDial.close()
                    return@OnActionSelectedListener true
                }
            }
            false
        })


    }

    fun fabMain() {
        speedDial.mainFabClosedBackgroundColor = resources.getColor(R.color.DarkPink)
    }

    fun fabSave() {
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_save_game, R.drawable.ic_save_black)
                .setFabBackgroundColor(resources.getColor(R.color.PacificCoast))
                .setLabelBackgroundColor(resources.getColor(R.color.LightPink))
                .setLabel("Save game")
                .create()
        )
    }

    fun fabClose() {
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_close_game, R.drawable.ic_delete_sweep)
                .setFabBackgroundColor(resources.getColor(R.color.PacificCoast))
                .setLabelBackgroundColor(resources.getColor(R.color.LightPink))
                .setLabel("Close game")
                .create()
        )
    }

    fun fabReset() {
        speedDial.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_reset, R.drawable.ic_reset_black)
                .setFabBackgroundColor(resources.getColor(R.color.PacificCoast))
                .setLabelBackgroundColor(resources.getColor(R.color.LightPink))
                .setLabel("Reset game")
                .create()
        )
    }
}
