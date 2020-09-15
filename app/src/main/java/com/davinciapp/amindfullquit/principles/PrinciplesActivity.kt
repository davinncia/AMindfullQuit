package com.davinciapp.amindfullquit.principles

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.davinciapp.amindfullquit.R
import com.google.android.material.appbar.CollapsingToolbarLayout

class PrinciplesActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principles)

        pager = findViewById(R.id.view_pager_principles)
        pager.adapter = PrinciplesPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        pager.setPageTransformer(true, DepthPageTransformer())

    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, PrinciplesActivity::class.java)
    }
}
