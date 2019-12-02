package com.example.amindfullquit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.amindfullquit.meditation.MeditationActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val meditationEditText = findViewById<TextView>(R.id.tv_meditation)
        meditationEditText.tag = "MEDITATE"

        meditationEditText.setOnClickListener { view ->  openActivity(view.tag.toString())}
    }

    private fun openActivity(tag: String){

        var intent = Intent()

        when(tag){
            "MEDITATE" -> intent = Intent(this, MeditationActivity::class.java)
            else -> throw IllegalArgumentException("No such tag")
        }
        startActivity(intent)
    }
}
