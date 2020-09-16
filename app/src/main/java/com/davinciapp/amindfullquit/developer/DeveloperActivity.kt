package com.davinciapp.amindfullquit.developer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.davinciapp.amindfullquit.R
import com.davinciapp.amindfullquit.di.ViewModelFactory
import com.davinciapp.amindfullquit.utils.bind

class DeveloperActivity : AppCompatActivity() {

    private val donateCoffeeButton by bind<Button>(R.id.btn_donation_coffee_developer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        //VM
        val viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[DeveloperViewModel::class.java]

        findViewById<TextView>(R.id.tv_price_donation_coffee_developer).text = "2€"
        findViewById<TextView>(R.id.tv_price_donation_sandwich_developer).text = "5€"

        donateCoffeeButton.setOnClickListener {
            Toast.makeText(this, "DONATE !!!!!!", Toast.LENGTH_SHORT).show()
            viewModel.donate()
        }
    }


    companion object {
        fun newIntent(context: Context): Intent = Intent(context, DeveloperActivity::class.java)
    }
}
