package com.minisalt.remembermyscore.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.bottomnavigationview.utils.ExitWithAnimation
import com.minisalt.bottomnavigationview.utils.exitCircularReveal
import com.minisalt.bottomnavigationview.utils.startCircularReveal
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.DataMover
import com.minisalt.remembermyscore.preferences.GameRules
import com.minisalt.remembermyscore.recyclerView.adapter.ButtonAdapter
import com.minisalt.remembermyscore.recyclerView.clickListener.RecyclerViewClickInterface
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_add_rules.*


class AddRulesFragment : Fragment(R.layout.fragment_add_rules), ExitWithAnimation,
    RecyclerViewClickInterface {

    private val dataMover = DataMover()
    var editGameRules: GameRules? = null
    var updatePosition: Int? = null

    //THIS IS FRAGMENT STUFF
    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true


    companion object {
        @JvmStatic
        fun newInstance(
            exit: IntArray? = null,
            editGameRules: GameRules? = null,
            updatePosition: Int? = null
        ): AddRulesFragment = AddRulesFragment()
            .apply {
                this.editGameRules = editGameRules
                this.updatePosition = updatePosition
                if (exit != null && exit.size == 2) {
                    posX = exit[0]
                    posY = exit[1]
                }
            }
    }
    //-----------------------

    lateinit var buttonAdapter: ButtonAdapter
    private var gameRule: GameRules = GameRules()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(false)

        if (editGameRules != null) {
            updatingListing()
        }


        initRecyclerView(gameRule.buttons)

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
                view.exitCircularReveal(btnSave.x.toInt() + 200, btnSave.y.toInt()) {
                    fragmentManager!!.popBackStack()
                }
            }
        }

    }

    private val simpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //THIS METHOD IS JUST IF YOU WANT TO REARRANGE ITEMS IN THE RECYCLERVIEW
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
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(ContextCompat.getColor(context!!, R.color.DeleteRed))
                    .addActionIcon(R.drawable.ic_delete_white)
                    .setIconHorizontalMargin(R.drawable.ic_delete_white, 8)
                    .create()
                    .decorate()



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

    private fun initRecyclerView(buttonArray: ArrayList<Int>) {
        recyclerViewButton.layoutManager = LinearLayoutManager(context)
        recyclerViewButton.setHasFixedSize(true)
        buttonAdapter =
            ButtonAdapter(
                buttonArray
            )
        recyclerViewButton.adapter = buttonAdapter
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewButton)
    }

    private fun addItem() {
        if (numberInput.text.toString() == "")
            return

        val num = Integer.parseInt(numberInput.text.toString())
        when {
            gameRule.buttons.contains(num) -> {
                l_numberInput.error = "Button already exists"
            }
            num == 0 -> {
                l_numberInput.error = "The number 0 isn't a valid number to add"
            }
            else -> {
                l_numberInput.error = null
                buttonAdapter.notifyItemInserted(addSort())
            }
        }

        numberInput.text?.clear()
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

    private fun titleCheck(list: ArrayList<GameRules>): Boolean {
        if (titleText.text.toString() == "") {     //Checking if there is a name
            l_titleText.error = "Input valid title name"
            return false

        } else if (list.size != 0 && repeatingName(list)) { //Checking if the name is already in use

            l_titleText.error = "You already have rules for a game named '${titleText.text}'"
            return false

        } else if (titleText.length() > 20) {
            l_titleText.error = "Title length must be under 20 characters"
            return false
        } else
            gameRule.name = titleText.text.toString().capitalize()

        return true
    }

    private fun pointCheck(): Boolean {
        return if (editPointsToWin.text.toString() != "" && Integer.parseInt(editPointsToWin.text.toString()) != 0) {
            gameRule.pointsToWin = Integer.parseInt(editPointsToWin.text.toString())
            true
        } else {
            l_pointsToWin.error = "Input valid points to win"
            false
        }
    }

    private fun saveSettings(): Boolean {
        val allRules: ArrayList<GameRules> = dataMover.loadGameRules(context!!)

        val fragm: RulesFragment? =
            fragmentManager!!.findFragmentByTag("Rule Fragment") as RulesFragment?

        if (updatePosition == null && titleCheck(allRules) && pointCheck()) {
            dataMover.appendToGameRules(context!!, gameRule)
            fragm!!.changesApplied(updatePosition)
        } else {
            if (!checkForChanges(allRules))
                return false

            dataMover.replaceGameRule(context!!, gameRule, updatePosition!!)
            fragm!!.changesApplied(updatePosition!!)
        }

        return true
    }

    private fun repeatingName(allRules: ArrayList<GameRules>): Boolean {
        for (topItem in allRules)
            if (topItem.name.decapitalize() == titleText.text.toString().decapitalize())
                return true

        return false
    }

    private fun updatingListing() {
        titleText.text = Editable.Factory.getInstance().newEditable(editGameRules!!.name)
        editPointsToWin.text =
            Editable.Factory.getInstance().newEditable(editGameRules!!.pointsToWin.toString())
        gameRule.buttons = editGameRules!!.buttons
        btnSave.text = "Update"
    }

    private fun checkForChanges(allRules: ArrayList<GameRules>): Boolean {
        return if (updatedName(allRules) && pointCheck() && dataMover.gameRuleExistence(context!!, gameRule)) {
            true
        } else true
    }

    private fun updatedName(list: ArrayList<GameRules>): Boolean {
        if (titleText.text.toString() == "") {     //Checking if there is a name
            l_titleText.error = "Input valid title name"
            return false
        } else if (titleText.length() > 20) {
            l_titleText.error = "Title length must be under 20 characters"
            return false
        } else
            gameRule.name = titleText.text.toString().capitalize()

        return true
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}
