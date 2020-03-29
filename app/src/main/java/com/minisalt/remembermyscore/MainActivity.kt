package com.minisalt.remembermyscore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bottom_navbar_template.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Changing the icons
        changingIcons()
        //-------------------------------------------










        homeBtn.setOnClickListener {
        }
        gameBtn.setOnClickListener{
            startActivity(Intent(this,GameActivity::class.java))
        }
        rulesBtn.setOnClickListener {
            startActivity(Intent(this,RulesActivity::class.java))
        }
        scoresBtn.setOnClickListener {
            startActivity(Intent(this,ScoresActivity::class.java))
        }



    }
    private fun changingIcons(){
        homeBtn.setImageResource(R.drawable.ic_home_pink)
        gameBtn.setImageResource(R.drawable.ic_controller_black)
        rulesBtn.setImageResource(R.drawable.ic_receipt_black)
        scoresBtn.setImageResource(R.drawable.ic_storage_black)
    }
}
