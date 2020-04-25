package com.example.amindfullquit.meditation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.amindfullquit.meditation.log_fragment.LogFragment
import com.example.amindfullquit.meditation.timer_fragment.TimerFragment

class MeditationPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> TimerFragment.newInstance()
            1 -> LogFragment.newInstance()
            else -> return TimerFragment.newInstance()
        }
    }

    override fun getCount(): Int = NBR_PAGES

    companion object {
        private const val NBR_PAGES = 2
    }

}