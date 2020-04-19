package com.minisalt.remembermyscore

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minisalt.bottomnavigationview.utils.ExitWithAnimation
import com.minisalt.bottomnavigationview.utils.exitCircularReveal
import com.minisalt.remembermyscore.fragments.GameFragment
import com.minisalt.remembermyscore.fragments.HomeFragment
import com.minisalt.remembermyscore.fragments.RulesFragment
import com.minisalt.remembermyscore.fragments.ScoresFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    lateinit var toast: Toast
    private var doubleBackExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav_view.setOnNavigationItemSelectedListener(this)
        bottom_nav_view.selectedItemId = R.id.navigation_home
    }

    val homeFragment: HomeFragment =
        HomeFragment()
    val gameFragment: GameFragment =
        GameFragment()
    val rulesFragment: RulesFragment =
        RulesFragment()
    val scoresFragment: ScoresFragment =
        ScoresFragment()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, homeFragment, "Home Fragment").commit()
                return true
            }

            R.id.navigation_game -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, gameFragment, "Game Fragment").commit()
                return true
            }

            R.id.navigation_rules -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, rulesFragment, "Rule Fragment").commit()
                return true
            }

            R.id.navigation_scores -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, scoresFragment, "Scores Fragment").commit()
                return true
            }

        }


        return true
    }

    override fun onBackPressed() {

        toast = Toast.makeText(this, "Press back again to exit app", Toast.LENGTH_SHORT)

        if (supportFragmentManager.backStackEntryCount != 0) {
            with(supportFragmentManager.findFragmentById(R.id.container)) {
                // Check if the current fragment implements the [ExitWithAnimation] interface or not
                // Also check if the [ExitWithAnimation.isToBeExitedWithAnimation] is `true` or not
                if ((this as? ExitWithAnimation)?.isToBeExitedWithAnimation() == true) {
                    if (this.posX == null || this.posY == null) {
                        super.onBackPressed()
                    } else {
                        this.view?.exitCircularReveal(this.posX!!, this.posY!!) {
                            super.onBackPressed()
                        } ?: super.onBackPressed()
                    }
                } else {
                    super.onBackPressed()
                }
            }
        } else {
            if (doubleBackExitPressedOnce) {
                toast.cancel()
                super.onBackPressed()
            } else {
                this.doubleBackExitPressedOnce = true
                toast.show()
                Handler().postDelayed({ doubleBackExitPressedOnce = false }, 2000)
            }
        }

    }
}
