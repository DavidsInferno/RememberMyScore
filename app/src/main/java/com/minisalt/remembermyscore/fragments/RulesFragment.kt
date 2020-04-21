package com.minisalt.remembermyscore.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.minisalt.bottomnavigationview.utils.findLocationOfCenterOnTheScreen
import com.minisalt.remembermyscore.R
import com.minisalt.remembermyscore.preferences.DataMover
import com.minisalt.remembermyscore.preferences.GameRules
import com.minisalt.remembermyscore.recyclerView.adapter.RulesAdapter
import com.minisalt.remembermyscore.recyclerView.clickListener.RecyclerViewClickInterface
import com.minisalt.remembermyscore.utils.open
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_rules.*

class RulesFragment : Fragment(R.layout.fragment_rules), RecyclerViewClickInterface {

    lateinit var ruleAdapter: RulesAdapter

    private var easterEgg = 0

    val dataMover = DataMover()

    lateinit var list: ArrayList<GameRules>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        easterEgg = 0

        list = dataMover.loadGameRules(context!!)




        if (list.isNotEmpty()) {
            initRecyclerView(list)
            txtNoRules.visibility = View.GONE
        } else
            txtNoRules.visibility = View.VISIBLE



        fab.setOnClickListener {
            val positions = it.findLocationOfCenterOnTheScreen()
            fragmentManager?.open {
                // Pass center as the end position of the circular reveal
                add(
                    R.id.container, AddRulesFragment.newInstance(
                        positions
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
                                swipedGameRule.name,
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

                                val positions: IntArray = intArrayOf(
                                    recyclerViewRules.width / 2,
                                    viewHolder.itemView.y.toInt() + viewHolder.itemView.height / 2
                                )
                                add(
                                    R.id.container,
                                    AddRulesFragment.newInstance(
                                        positions,
                                        swipedGameRule,
                                        position
                                    )
                                ).addToBackStack(null)
                                Handler().postDelayed(
                                    { ruleAdapter.notifyItemChanged(position) },
                                    400
                                )
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
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorAccent
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.colorAccent
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_edit_black)
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


    private fun initRecyclerView(rules: ArrayList<GameRules>) {
        recyclerViewRules.layoutManager = LinearLayoutManager(context)
        recyclerViewRules.setHasFixedSize(true)
        ruleAdapter =
            RulesAdapter(rules, this)
        recyclerViewRules.adapter = ruleAdapter
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewRules)
    }

    override fun onItemClick(position: Int) {
        easterEgg++
        if (easterEgg == 10)
            Toast.makeText(context!!, "Press me harder", Toast.LENGTH_LONG).show()
        else if (easterEgg == 20)
            Toast.makeText(context!!, "Just the way I like it", Toast.LENGTH_LONG).show()


    }

    fun changesApplied(position: Int?) {
        initRecyclerView(dataMover.loadGameRules(context!!))

        if (position != null)
            Handler().postDelayed({ ruleAdapter.notifyItemChanged(position) }, 700)
    }

    override fun onLongItemClick(position: Int) {
    }

    fun checkIfGameIsInProgress(gameName: String): Boolean {
        val currentGame = dataMover.loadCurrentGame(context!!)
        return if (currentGame != null && currentGame.gameTitle == gameName) {
            val snackBar = Snackbar.make(
                recyclerViewRules, "You are currently playing this game",
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction("Dismiss") {
                snackBar.dismiss()
            }
            snackBar.show()
            true
        } else
            false
    }
}
