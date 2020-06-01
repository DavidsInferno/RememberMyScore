package com.minisalt.remembermyscore

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.minisalt.remembermyscore.data.DataMover
import com.minisalt.remembermyscore.data.IntroSlide
import com.minisalt.remembermyscore.recyclerView.adapter.IntroSlideAdapter
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {

    private val introSlideAdapter = IntroSlideAdapter(
        listOf(
            IntroSlide(
                "Welcome to RememberMyScore", "This is an app that lets you keep track of any game you want. Let me walk you through " +
                        "some of the features", R.drawable.redo_main
            ),
            IntroSlide(
                "Home Screen", "From this screen you can select how many players are playing and what game you want to play.", R.drawable
                    .home_screen
            ),

            IntroSlide(
                "Game screen", "This is where you keep track of all your scores. Don't worry about losing your progress, it gets saved " +
                        "automatically when you leave this screen.", R.drawable.game_screen
            ),
            IntroSlide(
                "Rules screen", "This is where you will keep a collection of all the games you have created. From here you can make " +
                        "your own!", R.drawable.rules_screen
            ),
            IntroSlide(
                "Scores screen",
                "All the games you have completed will be saved here",
                R.drawable.scores_screen
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        introSliderViewPager.adapter = introSlideAdapter
        setupIndicators()
        setCurrentIndicator(0)

        buttonNext.setOnClickListener {
            if (introSliderViewPager.currentItem + 1 < introSlideAdapter.itemCount)
                introSliderViewPager.currentItem++
            else {
                DataMover().firstTimeWrite(this, false)

                val myIntent = Intent(this, MainActivity::class.java)
                startActivity(myIntent)
            }
        }

        introSliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == introSlideAdapter.itemCount - 1)
                    buttonNext.text = "Finish"
                else
                    buttonNext.text = "Next"
            }
        })
    }


    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSlideAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive)
                )
                this?.layoutParams = layoutParams
            }
            indicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive)
                )
            }
        }
    }

}
