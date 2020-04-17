package com.minisalt.remembermyscore

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.minisalt.bottomnavigationview.utils.ExitWithAnimation
import com.minisalt.bottomnavigationview.utils.exitCircularReveal
import com.minisalt.bottomnavigationview.utils.startCircularReveal
import com.minisalt.remembermyscore.adapter.RecycleButtonAdapter
import com.minisalt.remembermyscore.preferences.DataMover
import com.minisalt.remembermyscore.preferences.GameRules
import kotlinx.android.synthetic.main.fragment_add_rules.*

/**
 * A simple [Fragment] subclass.
 */
class AddRulesFragment : Fragment(), ExitWithAnimation {

    val dataMover = DataMover()

    //THIS IS FRAGMENT STUFF
    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true


    companion object {
        @JvmStatic
        fun newInstance(exit: IntArray? = null): AddRulesFragment = AddRulesFragment().apply {
            if (exit != null && exit.size == 2) {
                posX = exit[0]
                posY = exit[1]
            }
        }
    }
    //-----------------------

    lateinit var buttonAdapter: RecycleButtonAdapter
    var gameRule: GameRules = GameRules(buttons = arrayListOf(-1, 1))


    //swipe away assets
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#D41414"))
    private lateinit var deleteIcon: Drawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_rules, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(false)


        initRecyclerView(gameRule.buttons)
        swipeToRemove()

        numberInput.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                addItem()
                return@OnKeyListener true
            }
            false
        })

        buttonAdd.setOnClickListener {
            addItem()
        }

        btnSave.setOnClickListener {
            val checkClose: Boolean = saveSettings()
            if (checkClose) {
                view.exitCircularReveal(btnSave.x.toInt() + 130, btnSave.y.toInt() + 100) {
                    fragmentManager!!.popBackStack()
                }
            }
        }

    }

    private fun swipeToRemove() {
        deleteIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_delete)!!

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
        recyclerViewButton.layoutManager = LinearLayoutManager(context)
        recyclerViewButton.setHasFixedSize(false)
        buttonAdapter = RecycleButtonAdapter(buttonArray)
        recyclerViewButton.adapter = buttonAdapter
    }

    private fun addItem() {
        if (numberInput.text.toString() == "")
            return

        val num = Integer.parseInt(numberInput.text.toString())
        if (gameRule.buttons.contains(num)) {
            l_numberInput.error = "Button already exists"
        } else {
            l_numberInput.error = null
            buttonAdapter.notifyItemInserted(addSort())
        }
    }

    private fun addSort(): Int {
        val numberToAdd = Integer.parseInt(numberInput.text.toString())
        var counter = 0
        if (gameRule.buttons.size != 0) {
            for (i in 0 until gameRule.buttons.size) {
                counter = i
                if (numberToAdd < gameRule.buttons[i]) {
                    gameRule.buttons.add(i, numberToAdd)
                    return i
                } else if (numberToAdd > gameRule.buttons[gameRule.buttons.size - 1]) {
                    gameRule.buttons.add(numberToAdd)
                    return gameRule.buttons.size - 1
                }
            }
        } else
            gameRule.buttons.add(counter, numberToAdd)

        return counter
    }

    private fun saveSettings(): Boolean {
        val existingList: ArrayList<GameRules>? = dataMover.loadGameRules(context!!)

        //Check the title
        if (titleText.text.toString() == "") {     //Checking if there is a name

            l_titleText.error = "Input valid title name"
            return false

        } else if (existingList != null && repeatingName(existingList)) { //Checking if the name is already in use

            l_titleText.error = "You already have rules for a game named '${titleText.text}'"
            return false

        } else if (titleText.length() > 20) {
            l_titleText.error = "Title length must be under 20 characters"
            return false
        } else
            gameRule.name = titleText.text.toString().capitalize()

        //Save for points
        if (editPointsToWin.text.toString() != "" && Integer.parseInt(editPointsToWin.text.toString()) != 0)
            gameRule.pointsToWin = Integer.parseInt(editPointsToWin.text.toString())
        else {
            l_numberInput.error = "Input valid points to win"
            return false
        }

        //check if any buttons have been added
        if (gameRule.buttons.size == 0) {
            l_numberInput.error = "Add at least one button"
            return false
        }


        dataMover.saveGameRules(context!!, gameRule)
        return true
    }

    private fun repeatingName(existingList: ArrayList<GameRules>): Boolean {
        for (topItem in existingList)
            if (topItem.name.decapitalize() == titleText.text.toString().decapitalize())
                return true

        return false
    }

}
