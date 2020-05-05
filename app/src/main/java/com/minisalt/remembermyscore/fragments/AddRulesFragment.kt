package com.minisalt.remembermyscore.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.MainActivity
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.recyclerView.adapter.ButtonAdapter
import com.minisalt.remembermyscore.recyclerView.clickListener.RecyclerViewClickInterface
import com.minisalt.remembermyscore.utils.ExitWithAnimation
import com.minisalt.remembermyscore.utils.startCircularReveal
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_add_rules.*

class AddRulesFragment : Fragment(R.layout.fragment_add_rules), ExitWithAnimation,
    RecyclerViewClickInterface {

    private val dataMover = DataMover()
    var editGameRules: GameRules? = null
    var updatePosition: Int? = null
    var rulesFragment: RulesFragment? = null

    //THIS IS FRAGMENT STUFF
    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true


    companion object {
        @JvmStatic
        fun newInstance(
            exit: IntArray? = null,
            editGameRules: GameRules? = null,
            updatePosition: Int? = null,
            rulesFragment: RulesFragment
        ): AddRulesFragment =
            AddRulesFragment().apply {
                this.editGameRules = editGameRules
                this.updatePosition = updatePosition
                this.rulesFragment = rulesFragment
                if (exit != null && exit.size == 2) {
                    posX = exit[0]
                    posY = exit[1]
                }
            }
    }
    //-----------------------

    lateinit var buttonAdapter: ButtonAdapter
    private var gameRule: GameRules = GameRules()

    //When a user updated a listing and didn't save, the changes would be displayed in RulesFragment. Not saved, just
    // would look ugly
    private var buttonBackup: ArrayList<Int> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(false)

        if (editGameRules != null)
            updatingListing()



        initRecyclerView(gameRule.buttons)

        numberInput.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                addItem()
                return@OnKeyListener true
            }
            false
        })

        buttonAdd.setOnClickListener {
            addItem()
        }

        addExtraField.setOnClickListener {
            cbLowestScoreWins.isChecked = false

            if (!gameRule.extraField_1_enabled)
                extraField_1_Visibility(visible = true)
            else
                extraField_2_Visibility(visible = true)
        }

        addExtraField.setOnLongClickListener {
            if (gameRule.extraField_2_enabled)
                extraField_2_Visibility(visible = false)
            else
                extraField_1_Visibility(visible = false)

            true
        }

        btnSave.setOnClickListener {
            val checkClose: Boolean = saveSettings()
            if (checkClose) {
                (activity as MainActivity?)?.onBackPressed()
            }
        }

    }

    fun extraField_1_Visibility(visible: Boolean) {

        if (visible) {
            gameRule.extraField_1_enabled = true
            l_extraField1.visibility = View.VISIBLE
            cBextraField1.visibility = View.VISIBLE
        } else {
            gameRule.extraField_1_enabled = false
            l_extraField1.visibility = View.GONE
            cBextraField1.visibility = View.GONE
        }

    }

    fun extraField_2_Visibility(visible: Boolean) {

        if (visible) {
            gameRule.extraField_2_enabled = true
            l_extraField2.visibility = View.VISIBLE
            cBextraField2.visibility = View.VISIBLE
        } else {
            gameRule.extraField_2_enabled = false
            l_extraField2.visibility = View.GONE
            cBextraField2.visibility = View.GONE
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
        recyclerViewButton.setHasFixedSize(true)
        buttonAdapter = ButtonAdapter(buttonArray)
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

        l_titleText.error = null
        return true
    }

    private fun pointCheck(): Boolean {
        val pointsAsString = editPointsToWin.text.toString()

        when {
            pointsAsString == "" -> {
                l_pointsToWin.error = "Field can't be empty"
                return false
            }
            pointsAsString.length > 10 -> {
                l_pointsToWin.error = "Number is too big"
                return false
            }
            Integer.parseInt(pointsAsString) == 0 -> {
                l_pointsToWin.error = "Input valid points to win"
                return false
            }
            else -> {
                gameRule.pointsToWin = Integer.parseInt(pointsAsString)
                l_pointsToWin.error = null
                return true
            }
        }

    }

    private fun saveSettings(): Boolean {
        val allRules: ArrayList<GameRules> = dataMover.loadGameRules(requireContext())

        gameRule.diceRequired = diceRequirement.isChecked

        if (gameRule.extraField_1_enabled) {

            if (!fieldCheck1())
                return false

            if (gameRule.extraField_2_enabled)
                if (!fieldCheck2())
                    return false

            //check if lowest points to win option is disabled when adding more fields
            if (cbLowestScoreWins.isChecked && (gameRule.extraField_1condition || gameRule.extraField_2condition)) {
                cbLowestScoreWins.isChecked = false
                cbLowestScoreWins.error = "Can not have extra field condition with this option"

                Toast.makeText(context, "Can not have extra field condition with lowest score wins", Toast.LENGTH_SHORT).show()
                return false
            }
        }


        gameRule.lowestPointsWin = cbLowestScoreWins.isChecked






        return if (updatePosition == null && titleCheck(allRules) && pointCheck()) {
            dataMover.appendToGameRules(requireContext(), gameRule)
            rulesFragment?.gettingLatestRuleList(null)
            true
        } else if (updatePosition != null && checkForChanges()) {
            dataMover.replaceGameRule(requireContext(), gameRule, updatePosition!!)
            rulesFragment?.gettingLatestRuleList(updatePosition)
            true
        } else
            false
    }

    private fun fieldCheck1(): Boolean {
        val pointsAsString = extraField1.text.toString()

        when {
            pointsAsString == "" -> {
                l_extraField1.error = "Field can't be empty"
                return false
            }
            pointsAsString.length > 10 -> {
                l_extraField1.error = "Number is too big"
                return false
            }
            Integer.parseInt(pointsAsString) == 0 -> {
                l_extraField1.error = "Input valid points"
                return false
            }
            else -> {
                gameRule.extraField_1text = Integer.parseInt(pointsAsString)
                gameRule.extraField_1condition = cBextraField1.isChecked
                l_extraField1.error = null
                return true
            }
        }
    }

    private fun fieldCheck2(): Boolean {
        val pointsAsString = extraField2.text.toString()

        when {
            pointsAsString == "" -> {
                l_extraField2.error = "Field can't be empty"
                return false
            }
            pointsAsString.length > 10 -> {
                l_extraField2.error = "Number is too big"
                return false
            }
            Integer.parseInt(pointsAsString) == 0 -> {
                l_extraField2.error = "Input valid points"
                return false
            }
            else -> {
                gameRule.extraField_2text = Integer.parseInt(pointsAsString)
                gameRule.extraField_2condition = cBextraField2.isChecked

                l_extraField2.error = null
                return true
            }
        }
    }

    private fun repeatingName(allRules: ArrayList<GameRules>): Boolean {
        for (topItem in allRules)
            if (topItem.name.decapitalize() == titleText.text.toString().decapitalize())
                return true

        return false
    }

    private fun updatingListing() {
        titleText.text = Editable.Factory.getInstance().newEditable(editGameRules!!.name)
        editPointsToWin.text = Editable.Factory.getInstance().newEditable(editGameRules!!.pointsToWin.toString())

        if (editGameRules!!.extraField_1_enabled) {
            l_extraField1.visibility = View.VISIBLE
            cBextraField1.visibility = View.VISIBLE
            extraField1.text = Editable.Factory.getInstance().newEditable(editGameRules!!.extraField_1text.toString())
            cBextraField1.isChecked = editGameRules!!.extraField_1condition
            gameRule.extraField_1_enabled = true

            if (editGameRules!!.extraField_2_enabled) {
                l_extraField2.visibility = View.VISIBLE
                cBextraField2.visibility = View.VISIBLE
                extraField2.text = Editable.Factory.getInstance().newEditable(editGameRules!!.extraField_2text.toString())
                cBextraField2.isChecked = editGameRules!!.extraField_2condition
                gameRule.extraField_2_enabled = true

            } else {
                l_extraField2.visibility = View.GONE
                cBextraField2.visibility = View.GONE
            }
        } else {
            l_extraField1.visibility = View.GONE
            cBextraField1.visibility = View.GONE
        }

        diceRequirement.isChecked = editGameRules!!.diceRequired
        cbLowestScoreWins.isChecked = editGameRules!!.lowestPointsWin

        gameRule.buttons = editGameRules!!.buttons
        btnSave.text = "Update"
        buttonBackup.addAll(gameRule.buttons)
    }

    private fun checkForChanges(): Boolean {
        return updatedName() && pointCheck() && !checkGameRuleExistence()
    }

    fun checkGameRuleExistence(): Boolean {
        if (dataMover.gameRuleExistence(requireContext(), gameRule)) {
            Toast.makeText(context, "No changes have been made", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun updatedName(): Boolean {
        when {
            titleText.text.toString() == "" -> {     //Checking if there is a name
                l_titleText.error = "Input valid title name"
                return false
            }
            titleText.length() > 20 -> {
                l_titleText.error = "Title length must be under 20 characters"
                return false
            }
            else -> gameRule.name = titleText.text.toString().capitalize()
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        if (updatePosition != null) {
            gameRule.buttons.clear()
            gameRule.buttons.addAll(buttonBackup)
        }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onLongItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}
