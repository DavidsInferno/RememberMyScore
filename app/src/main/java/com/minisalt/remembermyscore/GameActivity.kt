package com.minisalt.remembermyscore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.bottom_navbar_template.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //Changing the icons
        changingIcons()
        //-------------------------------------------









        homeBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        gameBtn.setOnClickListener {
        }
        rulesBtn.setOnClickListener {
            startActivity(Intent(this,RulesActivity::class.java))
        }
        scoresBtn.setOnClickListener {
            startActivity(Intent(this,ScoresActivity::class.java))
        }
    }

    private fun changingIcons(){
        homeBtn.setImageResource(R.drawable.ic_home_black)
        gameBtn.setImageResource(R.drawable.ic_controller_pink)
        rulesBtn.setImageResource(R.drawable.ic_receipt_black)
        scoresBtn.setImageResource(R.drawable.ic_storage_black)
    }
}
