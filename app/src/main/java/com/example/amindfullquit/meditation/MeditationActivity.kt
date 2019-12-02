package com.example.amindfullquit.meditation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.amindfullquit.R
import com.google.android.material.tabs.TabLayout

class MeditationActivity : AppCompatActivity() {

    private lateinit var mPager: CustomSwipeViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        mPager = findViewById(R.id.view_pager_meditation)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout_indicator_meditation)

        val pagerAdapter = MeditationPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        mPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(mPager)
    }

}
