package com.example.amindfullquit.meditation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MeditationPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {

    private val NBR_PAGES = 2

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> TimerFragment.newInstance()
            1 -> LogFragment.newInstance()
            else -> return TimerFragment.newInstance()
        }
    }

    override fun getCount(): Int = NBR_PAGES


}