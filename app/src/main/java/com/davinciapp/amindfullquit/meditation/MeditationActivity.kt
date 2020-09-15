package com.davinciapp.amindfullquit.meditation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentStatePagerAdapter
import com.davinciapp.amindfullquit.R
import com.google.android.material.tabs.TabLayout

class MeditationActivity : AppCompatActivity() {

    private lateinit var mPager: CustomSwipeViewPager
    private lateinit var mTabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)

        //this.supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.myBlue)))
        //this.supportActionBar?.hide()
        setSupportActionBar(findViewById(R.id.toolbar_meditation))
        supportActionBar?.title = ""

        mPager = findViewById(R.id.view_pager_meditation)
        mTabLayout = findViewById(R.id.tab_layout_indicator_meditation)

        val pagerAdapter = MeditationPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        mPager.adapter = pagerAdapter
        //TODO anim

        mTabLayout.setupWithViewPager(mPager)

    }


}
