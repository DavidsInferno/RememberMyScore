package com.minisalt.remembermyscore.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.GameRules
import com.minisalt.remembermyscore.recyclerView.adapter.RulesAdapter
import com.minisalt.remembermyscore.recyclerView.clickListener.RecyclerViewClickInterface
import com.minisalt.remembermyscore.utils.findLocationOfCenterOnTheScreen
import com.minisalt.remembermyscore.utils.open
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_rules.*

class RulesFragment : Fragment(R.layout.fragment_rules), RecyclerViewClickInterface {

    lateinit var ruleAdapter: RulesAdapter

    val dataMover = DataMover()

    lateinit var list: ArrayList<GameRules>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough.create()
        exitTransition = MaterialFadeThrough.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gettingLatestRuleList(null)

        fabSetup()
    }

    fun fabSetup() {
        fab.setOnClickListener {
            val positions = it.findLocationOfCenterOnTheScreen()
            fragmentManager?.open {
                // Pass center as the end position of the circular reveal
                add(
                    R.id.container, AddRulesFragment.newInstance(
                        positions,
                        rulesFragment = this@RulesFragment
                    )
                ).addToBackStack(null)
            }
        }

        recyclerViewRules.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerViewRules, dx, dy)
                if (dy > 0 && fab.visibility == View.VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                    fab.show()
                }
            }
        })
    }

    fun gettingLatestRuleList(position: Int?) {
        list = dataMover.loadGameRules(requireContext())

        if (list.size != 0) {
            initRecyclerView(list)
            txtNoRules.visibility = View.GONE

            if (position != null)
                Handler().postDelayed({ ruleAdapter.notifyItemChanged(position) }, 700)
        } else
            txtNoRules.visibility = View.VISIBLE
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

                val position = viewHolder.adapterPosition

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val swipedGameRule = list[position]

                        if (!(checkIfGameIsInProgress(swipedGameRule.name))) {

                            list.removeAt(position)
                            ruleAdapter.notifyItemRemoved(position)
                            //set anchor view to show it above the navigation buttons
                            Snackbar.make(
                                recyclerViewRules,
                                swipedGameRule.name + " has been deleted!",
                                Snackbar.LENGTH_LONG
                            ).setAction("Undo") {
                                list.add(position, swipedGameRule)
                                ruleAdapter.notifyItemInserted(position)
                                dataMover.saveGameRules(context!!, list)
                            }.show()

                            dataMover.saveGameRules(context!!, list)
                        } else
                            ruleAdapter.notifyItemChanged(position)
                    }

                    ItemTouchHelper.RIGHT -> {
                        fragmentManager?.open {
                            val swipedGameRule = list[position]

                            if (!(checkIfGameIsInProgress(swipedGameRule.name))) {

                                val positions: IntArray =
                                    intArrayOf(recyclerViewRules.width / 2, viewHolder.itemView.y.toInt() + viewHolder.itemView.height / 2)
                                add(
                                    R.id.container,
                                    AddRulesFragment.newInstance(positions, swipedGameRule, position, this@RulesFragment)
                                ).addToBackStack(null)
                                Handler().postDelayed({ ruleAdapter.notifyItemChanged(position) }, 400)
                            } else {
                                ruleAdapter.notifyItemChanged(position)
                            }

                        }
                    }
                }
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
                RecyclerViewSwipeDecorator
                    .Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black)
                    .addSwipeLeftLabel("Delete")
                    .addSwipeRightActionIcon(R.drawable.ic_edit_black)
                    .addSwipeRightLabel("Edit")
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }


    private fun initRecyclerView(rules: ArrayList<GameRules>) {
        recyclerViewRules.setHasFixedSize(true)
        ruleAdapter = RulesAdapter(rules, this, requireContext());
        recyclerViewRules.adapter = ruleAdapter

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewRules)
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), "You can swipe the rules!", Toast.LENGTH_LONG).show()
    }

    override fun onLongItemClick(position: Int) {
    }

    fun checkIfGameIsInProgress(gameName: String): Boolean {
        val currentGame = dataMover.loadCurrentGame(requireContext())
        return if (currentGame != null && currentGame.gamePlayed.name == gameName) {
            val snackBar = Snackbar.make(
                recyclerViewRules,
                "You are currently playing this game",
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction("Dismiss") {}.show()
            true
        } else
            false
    }
}
