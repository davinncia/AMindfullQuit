package com.example.amindfullquit

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.amindfullquit.meditation.MeditationActivity
import com.example.amindfullquit.smoking.SmokingActivity
import java.util.function.Function

class MainActivity : AppCompatActivity(), View.OnTouchListener {

    private lateinit var principlesView: LinearLayout
    private lateinit var meditationView: LinearLayout
    private lateinit var dataView: LinearLayout
    private lateinit var developerView: LinearLayout

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        principlesView = findViewById(R.id.linear_layout_principles_main)
        principlesView.tag = "PRINCIPLES"
        meditationView = findViewById(R.id.linear_layout_meditation_main)
        meditationView.tag = "MEDITATE"
        dataView = findViewById(R.id.linear_layout_data_main)
        dataView.tag = "DATA"
        developerView = findViewById(R.id.linear_layout_developer_main)
        developerView.tag = "CODER"

        principlesView.setOnTouchListener(this)
        meditationView.setOnTouchListener(this)
        dataView.setOnTouchListener(this)
        developerView.setOnTouchListener(this)
        //meditationView.setOnClickListener { view ->  openActivity(view.tag.toString())}
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (v == principlesView || v == meditationView || v == dataView || v == developerView) {

            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.elevation = 0F
                }
                MotionEvent.ACTION_UP -> {
                    v.elevation = 10F
                    v.performClick()
                    openActivity(v.tag.toString())
                }
            }
        }

        return true
    }


    private fun openActivity(tag: String) {

        intent = when (tag) {
            "PRINCIPLES" -> Intent(this, MeditationActivity::class.java)
            "MEDITATE" -> Intent(this, MeditationActivity::class.java)
            "DATA" -> Intent(this, SmokingActivity::class.java)
            "CODER" -> Intent(this, MeditationActivity::class.java)
            else -> throw IllegalArgumentException("No such tag")
        }
        startActivity(intent)
    }

}
