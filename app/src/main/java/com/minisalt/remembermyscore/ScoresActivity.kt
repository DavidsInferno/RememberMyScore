package com.minisalt.remembermyscore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_navbar_template.*

class ScoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        //Changing the icons
        changingIcons()
        //-------------------------------------------













        homeBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))

        }
        gameBtn.setOnClickListener {
            startActivity(Intent(this,GameActivity::class.java))

        }
        rulesBtn.setOnClickListener {
            startActivity(Intent(this,RulesActivity::class.java))

        }
        scoresBtn.setOnClickListener {

        }
    }

    private fun changingIcons(){
        homeBtn.setImageResource(R.drawable.ic_home_black)
        gameBtn.setImageResource(R.drawable.ic_controller_black)
        rulesBtn.setImageResource(R.drawable.ic_receipt_black)
        scoresBtn.setImageResource(R.drawable.ic_storage_pink)
    }
}
