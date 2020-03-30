package com.minisalt.remembermyscore

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minisalt.remembermyscore.adapter.RecycleButtonAdapter
import com.minisalt.remembermyscore.preferences.GameRules
import kotlinx.android.synthetic.main.layout_addrules.*
import kotlin.math.roundToInt

//, RecycleButtonAdapter.OnNoteListener
class PopupAddRules : AppCompatActivity() {

    lateinit var buttonAdapter: RecycleButtonAdapter
    var gameRule: GameRules = GameRules(buttons = arrayListOf())

    //swipe away assets
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#D41414"))
    private lateinit var deleteIcon: Drawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_addrules)

        initRecyclerView(gameRule.buttons)


        //Making the window
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout(((width * 0.8).roundToInt()), (height * 0.6).roundToInt())
        //---------------------------------------------



        btnAddButton.setOnClickListener {
            gameRule.buttons.add(Integer.parseInt(numberInput.text.toString()))
            buttonAdapter.notifyItemInserted(gameRule.buttons.size)
        }

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
}
