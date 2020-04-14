package com.minisalt.remembermyscore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minisalt.bottomnavigationview.utils.findLocationOfCenterOnTheScreen
import com.realpacific.circularrevealexitdemo.utils.open
import kotlinx.android.synthetic.main.fragment_rules.*

/**
 * A simple [Fragment] subclass.
 */
class RulesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_rules, container, false)

        val popupAddRulesFragment = AddRulesFragment()



        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fab.setOnClickListener {
            val positions = it.findLocationOfCenterOnTheScreen()
            println(positions)
            fragmentManager?.open {
                // Pass center as the end position of the circular reveal
                add(R.id.container, AddRulesFragment.newInstance(positions)).addToBackStack(null)
            }
        }
    }

}
