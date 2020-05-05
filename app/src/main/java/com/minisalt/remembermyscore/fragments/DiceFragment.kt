package com.minisalt.remembermyscore.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import com.minisalt.remembermyscore.R
import kotlinx.android.synthetic.main.fragment_dice.*


class DiceFragment(val diceProperties: ArrayList<Int>) : Fragment(R.layout.fragment_dice) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis.create(MaterialSharedAxis.Y, false)
        exitTransition = MaterialSharedAxis.create(MaterialSharedAxis.Y, true)

    }


    val diceTypes: Array<String> = arrayOf("4", "6", "8", "10", "12", "20", "100")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDiceAmount()
        initDiceType()

        btnRoll.setOnClickListener {
            val ammountOfDices = numberOfDices.value
            val numberOfSides = Integer.parseInt(diceTypes[typeOfDice.value])
            var rollesNumbers: String = ""

            for (x in 1..ammountOfDices) {
                if (x == ammountOfDices) {
                    rollesNumbers += randomNumber(numberOfSides)
                    continue
                }

                rollesNumbers += randomNumber(numberOfSides) + "   "
            }
            rollValue.text = rollesNumbers
        }
    }

    fun randomNumber(end: Int): String {
        return (1..end).random().toString()
    }

    private fun initDiceAmount() {
        numberOfDices.minValue = 1
        numberOfDices.maxValue = 6

        if (diceProperties[0] != -1)
            numberOfDices.value = diceProperties[0]

        numberOfDices.wrapSelectorWheel = false
    }

    private fun initDiceType() {

        typeOfDice.minValue = 0
        typeOfDice.maxValue = diceTypes.size - 1

        typeOfDice.displayedValues = diceTypes
        typeOfDice.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        if (diceProperties[1] != -1)
            typeOfDice.value = diceProperties[1]

        typeOfDice.wrapSelectorWheel = false
    }

    override fun onPause() {
        super.onPause()
        diceProperties[0] = numberOfDices.value
        diceProperties[1] = typeOfDice.value
    }
}
