package com.example.amindfullquit.principles

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PrinciplesPagerAdapter(fm: FragmentManager, behaviour: Int):
    FragmentStatePagerAdapter(fm, behaviour) {

    override fun getItem(position: Int): Fragment = PrinciplesPagerFragment.newInstance(position)

    override fun getCount(): Int = NUM_PAGES

    companion object {
        private const val NUM_PAGES = 4
    }
}