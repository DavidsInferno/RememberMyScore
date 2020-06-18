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
import com.google.android.material.textfield.TextInputLayout
import com.minisalt.remembermyscore.MainActivity
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.recyclerView.adapter.ButtonAdapter
import com.minisalt.remembermyscore.utils.ExitWithAnimation
import com.minisalt.remembermyscore.utils.startCircularReveal
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_add_rules.*

enum class InputFields {
    Main,
    Second,
    Third
}

class AddRulesFragment : Fragment(R.layout.fragment_add_rules), ExitWithAnimation {

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


        simpleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                advancedMode()
            else
                simpleMode()

        }


        addExtraField.setOnClickListener {

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

        btnSave.setOnClickListener {
            val checkClose: Boolean = saveSettings()
            if (checkClose) {
                (activity as MainActivity?)?.onBackPressed()
            } else
                Toast.makeText(context, "Errors made", Toast.LENGTH_SHORT).show()

        }

    }

    private fun simpleMode() {
        simpleSwitch.text = "Simple"


        extraField_1_Visibility(false)
        extraField_2_Visibility(false)

        l_defaultPoints_1.visibility = View.GONE
        cbLowestScoreWins.visibility = View.GONE
        addExtraField.visibility = View.GONE
        txtExtraField.visibility = View.GONE
        diceRequirement.visibility = View.GONE
        roundCounter.visibility = View.GONE
    }

    private fun advancedMode() {
        simpleSwitch.text = "Advanced"

        cbLowestScoreWins.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cBwinningCondition1.isChecked = false
                cBwinningCondition2.isChecked = false
            }
        }

        l_defaultPoints_1.visibility = View.VISIBLE
        cbLowestScoreWins.visibility = View.VISIBLE
        addExtraField.visibility = View.VISIBLE
        txtExtraField.visibility = View.VISIBLE
        diceRequirement.visibility = View.VISIBLE
        roundCounter.visibility = View.VISIBLE
    }


    private fun extraField_1_Visibility(visible: Boolean) {

        if (visible) {
            gameRule.extraField_1_enabled = true
            pointDivider2.visibility = View.VISIBLE
            l_extraField_1_default.visibility = View.VISIBLE
            cBwinningCondition1.visibility = View.VISIBLE

            cBwinningCondition1.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    l_extraField_1_winPoints.visibility = View.VISIBLE
                    cbLowestScoreWins.isChecked = false
                } else
                    l_extraField_1_winPoints.visibility = View.GONE
            }
        } else {
            gameRule.extraField_1_enabled = false

            l_extraField_1_default.error = null
            l_extraField_1_winPoints.error = null

            pointDivider2.visibility = View.GONE
            l_extraField_1_default.visibility = View.GONE
            l_extraField_1_winPoints.visibility = View.GONE
            cBwinningCondition1.visibility = View.GONE
        }

    }

    private fun extraField_2_Visibility(visible: Boolean) {

        if (visible) {
            gameRule.extraField_2_enabled = true

            txtExtraField.text = "Long press to remove"

            pointDivider3.visibility = View.VISIBLE
            l_extraField_2_default.visibility = View.VISIBLE
            cBwinningCondition2.visibility = View.VISIBLE

            cBwinningCondition2.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    l_extraField_2_winPoints.visibility = View.VISIBLE
                    cbLowestScoreWins.isChecked = false
                } else
                    l_extraField_2_winPoints.visibility = View.GONE

            }
        } else {
            gameRule.extraField_2_enabled = false
            l_extraField_2_default.error = null
            l_extraField_2_winPoints.error = null

            txtExtraField.text = "Add another field \n( long press to remove )"

            pointDivider3.visibility = View.GONE
            l_extraField_2_default.visibility = View.GONE
            l_extraField_2_winPoints.visibility = View.GONE
            cBwinningCondition2.visibility = View.GONE
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
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context!!, R.color.DeleteRed))
                    .addActionIcon(R.drawable.ic_delete_white)
                    .setIconHorizontalMargin(R.drawable.ic_delete_white, 8)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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

    private fun saveSettings(): Boolean {
        val allRules: ArrayList<GameRules> = dataMover.loadGameRules(requireContext())

        gameRule.advancedMode = simpleSwitch.isChecked

        if (gameRule.advancedMode) {
            gameRule.diceRequired = diceRequirement.isChecked
            gameRule.lowestPointsWin = cbLowestScoreWins.isChecked
            gameRule.extraField_1condition = cBwinningCondition1.isChecked
            gameRule.extraField_2condition = cBwinningCondition2.isChecked
            gameRule.roundCounter = roundCounter.isChecked
        }


        if (!titleCheck(allRules))
            return false

        if (!checkingPoints())
            return false

        //updatePosition indicates if the rule is being updated or not
        return if (updatePosition == null) {
            dataMover.appendToGameRules(requireContext(), gameRule)
            rulesFragment?.gettingLatestRuleList(null)
            true
        } else if (updatePosition != null && !checkGameRuleExistence()) {
            dataMover.replaceGameRule(requireContext(), gameRule, updatePosition!!)
            rulesFragment?.gettingLatestRuleList(updatePosition)
            true
        } else
            false
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

    private fun checkingPoints(): Boolean {
        //Checking the main field
        if (winningPointCheck(pointsToWin.text.toString(), l_pointsToWin, InputFields.Main)) {
            if (gameRule.advancedMode) {
                if (startingPointCheck(defaultPoints_1.text.toString(), l_defaultPoints_1, InputFields.Main)) {
                    if (!gameRule.lowestPointsWin && (gameRule.startingPoints >= gameRule.pointsToWin)) {
                        l_defaultPoints_1.error = "Starting points higher than winning points"
                        return false
                    } else if (gameRule.lowestPointsWin && (gameRule.pointsToWin >= gameRule.startingPoints)) {
                        l_defaultPoints_1.error = "Starting points must be higher than winning points"
                        return false
                    }
                } else
                    return false
            }
        } else
            return false
        //---------------------


        //Checking the extra fields
        if (gameRule.advancedMode) {
            if (startingPointCheck(extraField_1_default.text.toString(), l_extraField_1_default, InputFields.Second)) {
                if (gameRule.extraField_1condition) {
                    if (winningPointCheck(extraField_1_winPoints.text.toString(), l_extraField_1_winPoints, InputFields.Second)) {
                        if (gameRule.extraField_StartPoint_1 >= gameRule.extraField_1_pointsToWin) {
                            l_defaultPoints_1.error = "Starting points higher than winning points"
                            return false
                        }
                    } else
                        return false

                }
            } else
                return false

            if (startingPointCheck(extraField_2_default.text.toString(), l_extraField_2_default, InputFields.Third)) {
                if (gameRule.extraField_2condition) {
                    if (winningPointCheck(extraField_2_winPoints.text.toString(), l_extraField_2_winPoints, InputFields.Third)) {
                        if (gameRule.extraField_StartPoint_2 >= gameRule.extraField_2_pointsToWin) {
                            l_extraField_2_default.error = "Starting points higher than winning points"
                            return false
                        }
                    } else
                        return false

                }
            } else
                return false
        }
        //-------------------------

        return true
    }

    private fun startingPointCheck(pointsToCheck: String, layout: TextInputLayout, inputField: InputFields): Boolean {
        return when {
            pointsToCheck == "" -> {
                layout.error = "Field can't be empty"
                false
            }
            pointsToCheck.length > 6 -> {
                layout.error = "Number is too big"
                false
            }
            else -> {
                when (inputField) {
                    InputFields.Main -> gameRule.startingPoints = Integer.parseInt(pointsToCheck)

                    InputFields.Second -> gameRule.extraField_StartPoint_1 = Integer.parseInt(pointsToCheck)

                    InputFields.Third -> gameRule.extraField_StartPoint_2 = Integer.parseInt(pointsToCheck)
                }

                layout.error = null
                true
            }
        }
    }

    private fun winningPointCheck(pointsToCheck: String, layout: TextInputLayout, inputField: InputFields): Boolean {
        return when {
            pointsToCheck == "" || pointsToCheck == "-" -> {
                layout.error = "Field can't be empty"
                false
            }
            pointsToCheck.length > 6 -> {
                layout.error = "Number is too big"
                false
            }
            else -> {
                when (inputField) {
                    InputFields.Main -> gameRule.pointsToWin = Integer.parseInt(pointsToCheck)

                    InputFields.Second -> {
                        gameRule.extraField_1_pointsToWin = Integer.parseInt(pointsToCheck)
                        if (gameRule.extraField_1_pointsToWin <= gameRule.extraField_StartPoint_1) {
                            layout.error = "Can't be lower than starting points"
                            return false
                        }
                    }

                    InputFields.Third -> {
                        gameRule.extraField_2_pointsToWin = Integer.parseInt(pointsToCheck)
                        if (gameRule.extraField_2_pointsToWin <= gameRule.extraField_StartPoint_2) {
                            layout.error = "Can't be lower than starting points"
                            return false
                        }
                    }
                }

                layout.error = null
                true
            }
        }
    }


    private fun repeatingName(allRules: ArrayList<GameRules>): Boolean {
        if (updatePosition != null) // game is getting updated
        {
            if (editGameRules!!.name.decapitalize() == titleText.text.toString().decapitalize())
                return false
        }

        for (topItem in allRules)
            if (topItem.name.decapitalize() == titleText.text.toString().decapitalize())
                return true

        return false
    }

    private fun updatingListing() {
        addRulesTitle.text = "Updating rule"
        titleText.text = Editable.Factory.getInstance().newEditable(editGameRules!!.name)
        pointsToWin.text = Editable.Factory.getInstance().newEditable(editGameRules!!.pointsToWin.toString())


        if (editGameRules!!.advancedMode) {
            advancedMode()
            simpleSwitch.isChecked = true

            defaultPoints_1.text = Editable.Factory.getInstance().newEditable(editGameRules!!.startingPoints.toString())
            cbLowestScoreWins.isChecked = editGameRules!!.lowestPointsWin

            if (editGameRules!!.extraField_1_enabled) {
                extraField_1_Visibility(true)

                extraField_1_default.text = Editable.Factory.getInstance().newEditable(editGameRules!!.extraField_StartPoint_1.toString())
                cBwinningCondition1.isChecked = editGameRules!!.extraField_1condition
                if (cBwinningCondition1.isChecked)
                    extraField_1_winPoints.text = Editable.Factory.getInstance().newEditable(editGameRules!!.extraField_1_pointsToWin.toString())


                if (editGameRules!!.extraField_2_enabled) {
                    extraField_2_Visibility(true)

                    extraField_2_default.text = Editable.Factory.getInstance().newEditable(editGameRules!!.extraField_StartPoint_2.toString())
                    cBwinningCondition2.isChecked = editGameRules!!.extraField_1condition
                    if (cBwinningCondition2.isChecked)
                        extraField_2_winPoints.text =
                            Editable.Factory.getInstance().newEditable(editGameRules!!.extraField_2_pointsToWin.toString())

                }
            }
            diceRequirement.isChecked = editGameRules!!.diceRequired
        }

        roundCounter.isChecked = editGameRules!!.roundCounter

        gameRule.buttons = editGameRules!!.buttons
        btnSave.text = "Update"
        buttonBackup.addAll(gameRule.buttons)
    }

    private fun checkGameRuleExistence(): Boolean {
        val gameExist = dataMover.gameRuleExistence(requireContext(), gameRule)

        return if (gameExist) {
            Toast.makeText(context, "No changes have been made", Toast.LENGTH_SHORT).show()
            true
        } else
            false
    }

    override fun onPause() {
        super.onPause()
        if (updatePosition != null) {
            gameRule.buttons.clear()
            gameRule.buttons.addAll(buttonBackup)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentFragmentManager.popBackStack()
    }
}
