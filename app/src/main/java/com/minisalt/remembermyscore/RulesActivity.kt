package com.minisalt.remembermyscore

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_rules.*
import kotlinx.android.synthetic.main.bottom_navbar_template.*

class RulesActivity : AppCompatActivity() {


    val hrpaBrojeva: ArrayList<Int> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)


        //Changing the icons
        changingIcons()
        //-------------------------------------------


        hrpaBrojeva.add(2)
        hrpaBrojeva.add(4)
        hrpaBrojeva.add(5)
        hrpaBrojeva.add(72)
        hrpaBrojeva.add(28)
        hrpaBrojeva.add(29)
        hrpaBrojeva.add(23)
        hrpaBrojeva.add(355)





        btnAddRules.setOnClickListener {
            //showDialog()
            startActivity(Intent(this, PopupAddRules::class.java))

        }


        homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        gameBtn.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))

        }
        scoresBtn.setOnClickListener {
            startActivity(Intent(this, ScoresActivity::class.java))
        }


    }

    private fun changingIcons() {
        homeBtn.setImageResource(R.drawable.ic_home_black)
        gameBtn.setImageResource(R.drawable.ic_controller_black)
        rulesBtn.setImageResource(R.drawable.ic_receipt_pink)
        scoresBtn.setImageResource(R.drawable.ic_storage_black)
    }


    private fun showDialog() {


        val dialog: Dialog = Dialog(this)

        val view: View = layoutInflater.inflate(R.layout.layout_addrules, null)

        val lv: ListView = view.findViewById(R.id.listView) as ListView

        // Change MyActivity.this and myListOfItems to your own values
        val clad: CustomListAdapterDialog = CustomListAdapterDialog(this@RulesActivity, hrpaBrojeva)


        lv.adapter = clad


        lv.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(
                    this@RulesActivity,
                    hrpaBrojeva.get(position).toString() + "",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        dialog.setContentView(view)

        dialog.show()
    }
}
