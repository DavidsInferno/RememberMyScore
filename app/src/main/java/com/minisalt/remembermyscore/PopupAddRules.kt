package com.minisalt.remembermyscore

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minisalt.remembermyscore.adapter.RecycleButtonAdapter
import com.minisalt.remembermyscore.preferences.GameRules
import kotlinx.android.synthetic.main.layout_addrules.*
import java.lang.reflect.Type
import kotlin.math.roundToInt

//, RecycleButtonAdapter.OnNoteListener
class PopupAddRules : AppCompatActivity() {

    lateinit var buttonAdapter: RecycleButtonAdapter
    var gameRule: GameRules = GameRules(buttons = arrayListOf())

    //lateinit var gameRules:GameRules
    //swipe away assets
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#D41414"))
    private lateinit var deleteIcon: Drawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_addrules)

        initRecyclerView(gameRule.buttons)
        displayWindow()
        swipeToRemove()



        numberInput.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                addItem()
                return@OnKeyListener true
            }
            false
        })

        btnSave.setOnClickListener {
            val checkClose: Boolean = saveSettings()
            if (checkClose) {
                startActivity(Intent(this, RulesActivity::class.java))
            }
        }
    }

    private fun swipeToRemove() {
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!

        val itemTouchHelperCallBack = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                buttonAdapter.removeItem(viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    swipeBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else {
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }

                swipeBackground.draw(c)

                c.save()


                if (dX > 0)
                    c.clipRect(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                else
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerViewButton)
    }

    private fun initRecyclerView(buttonArray: ArrayList<Int>) {
        recyclerViewButton.layoutManager = LinearLayoutManager(this@PopupAddRules)
        recyclerViewButton.setHasFixedSize(false)
        buttonAdapter = RecycleButtonAdapter(buttonArray)
        recyclerViewButton.adapter = buttonAdapter
    }

    private fun addItem() {
        if (numberInput.text.toString() == "")
            return

        val num = Integer.parseInt(numberInput.text.toString())
        if (gameRule.buttons.contains(num)) {
            Toast.makeText(this, "Button '$num' already exists", Toast.LENGTH_SHORT).show()
        } else {
            gameRule.buttons.add(Integer.parseInt(numberInput.text.toString()))
            buttonAdapter.notifyItemInserted(gameRule.buttons.size)
        }
    }

    private fun saveSettings(): Boolean {
        val existingList: ArrayList<GameRules>? = loadData()

        //Check the title
        if (titleText.text.toString() == "") {     //Checking if there is a name

            Toast.makeText(this, "Input valid title name", Toast.LENGTH_SHORT).show()
            return false

        } else if (existingList != null && repeatingName(existingList)) { //Checking if the name is already in use

            Toast.makeText(
                this,
                "You already have rules for a game named '${titleText.text}'",
                Toast.LENGTH_SHORT
            ).show()
            return false

        } else
            gameRule.name = titleText.text.toString()

        //Save for points
        if (editPointsToWin.text.toString() != "" && Integer.parseInt(editPointsToWin.text.toString()) != 0)
            gameRule.pointsToWin = Integer.parseInt(editPointsToWin.text.toString())
        else {
            Toast.makeText(this, "Input valid points to win", Toast.LENGTH_SHORT).show()
            return false
        }

        //check if any buttons have been added
        if (gameRule.buttons.size == 0) {
            Toast.makeText(this, "Add at least one button", Toast.LENGTH_SHORT).show()
            return false
        }


        saveDataToPreferences(gameRule)
        return true
    }

    private fun repeatingName(existingList: ArrayList<GameRules>): Boolean {
        for (topItem in existingList)
            if (topItem.name == titleText.text.toString())
                return true

        return false
    }

    private fun displayWindow() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout(((width * 0.8).roundToInt()), (height * 0.6).roundToInt())
    }

    private fun saveDataToPreferences(list: GameRules) {
        val gameRulesList: ArrayList<GameRules> = arrayListOf(list)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()

        //this appends to the end
        val allGameRules: ArrayList<GameRules>? = loadData()
        if (allGameRules != null) {
            println("UŠLO JE U IF")
            allGameRules.add(list)

            val json: String = gson.toJson(allGameRules)
            editor.putString("Game Rules", json)
            editor.apply()

            println(allGameRules)
        } else {
            println("UŠLO JE U ELSE")
            println(gameRulesList)

            val json: String = gson.toJson(gameRulesList)
            editor.putString("Game Rules", json)
            editor.apply()
        }
        //----------------------

    }

    private fun loadData(): ArrayList<GameRules>? {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = sharedPreferences.getString("Game Rules", null)
        val type: Type = object : TypeToken<ArrayList<GameRules>>() {}.type

        val existingRules: ArrayList<GameRules>? = gson.fromJson<ArrayList<GameRules>>(json, type)


        if (existingRules == null) {
            println("LOADDATA NISTA NE VRACA")
            return null
        } else {
            println("LOADDATA VRACA ARRAYLIST!")
            return existingRules
        }

    }
}
