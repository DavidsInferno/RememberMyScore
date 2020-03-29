package com.minisalt.remembermyscore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.adapter.RecycleButtonAdapter
import com.minisalt.remembermyscore.preferences.GameRules
import kotlinx.android.synthetic.main.layout_addrules.*
import java.lang.Exception
import kotlin.math.roundToInt

class PopupAddRules : AppCompatActivity() {

    lateinit var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    lateinit var buttonAdapter: RecycleButtonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_addrules)

        val gameRule: GameRules = GameRules(buttons = arrayListOf<Int>())

        gameRule.buttons.add(3)
        gameRule.buttons.add(6)
        gameRule.buttons.add(0)
        gameRule.buttons.add(12)

        initRecyclerView(gameRule.buttons)


        //Making the window

        val dm : DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout(((width*0.8).roundToInt()),(height*0.6).roundToInt())
        //---------------------------------------------


        btnAddButton.setOnClickListener {
            gameRule.buttons.add(Integer.parseInt(numberInput.text.toString()))
        }

    }

    private fun initRecyclerView(buttonArray: ArrayList<Int>){
        recyclerViewButton.layoutManager=LinearLayoutManager(this@PopupAddRules)
        buttonAdapter = RecycleButtonAdapter(buttonArray)
        recyclerViewButton.adapter = buttonAdapter
    }


}
