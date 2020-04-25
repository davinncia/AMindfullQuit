package com.example.amindfullquit.principles

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.amindfullquit.R
import com.example.amindfullquit.utils.bind

class PrinciplesActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principles)

        pager = findViewById(R.id.view_pager_principles)
        pager.adapter = PrinciplesPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, PrinciplesActivity::class.java)
    }
}
